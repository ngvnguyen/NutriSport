package com.sf.nutrisport.component.dialog


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.Alpha
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconWhite
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.SurfaceSecondary
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.component.CustomTextField
import com.sf.nutrisport.component.ErrorCard
import com.sf.nutrisport.domain.Country
import org.jetbrains.compose.resources.painterResource

@Composable
fun CountryPickerDialog(
    country: Country,
    onDismiss:()->Unit,
    onConfirmClick:(Country)->Unit
) {

    var selectedCountry by remember(country) {
        mutableStateOf(country)
    }
    val allCountry = remember {
        Country.entries.toList()
    }
    val filteredCountries = remember{
        mutableStateListOf<Country>().apply {
            addAll(allCountry)
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    AlertDialog(
        containerColor = Surface,
        title={
            Text(
                text = "Select a Country",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) {
                CustomTextField(
                    value = searchQuery,
                    onValueChange = { q ->
                        searchQuery = q
                        if (searchQuery.isNotBlank()) {
                            filteredCountries.clear()
                            filteredCountries.addAll(allCountry.filterByQuery(q))
                        } else {
                            filteredCountries.clear()
                            filteredCountries.addAll(allCountry)
                        }
                    },
                    placeholder = "Search..."
                )
                Spacer(modifier = Modifier.height((12.dp)))

                if (filteredCountries.isEmpty()){
                    ErrorCard(
                        modifier = Modifier.weight(1f),
                        message = "No countries found"
                    )
                }else{
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredCountries,
                            key = {it.ordinal}
                        ) {country->
                            CountryPicker(
                                country = country,
                                isSelected = selectedCountry == country,
                                onSelected = {
                                    selectedCountry = it
                                }
                            )
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {onConfirmClick(selectedCountry)},
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextSecondary
                )
            ){
                Text(
                    text = "Confirm",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary.copy(alpha = Alpha.HALF)
                )
            ){
                Text(
                    text = "Cancel",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Composable
fun CountryPicker(
    country: Country,
    isSelected: Boolean,
    onSelected:(Country)->Unit,
    modifier: Modifier= Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelected(country) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val saturation = remember { Animatable(if (isSelected) 1f else 0f) }

        LaunchedEffect(isSelected) {
            saturation.animateTo(if (isSelected) 1f else 0f)
        }
        val colorMatrix = remember(saturation.value) {
            ColorMatrix().apply {
                setToSaturation(saturation.value)
            }
        }
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(country.flag),
            contentDescription = "Country flag image",
            colorFilter = ColorFilter.colorMatrix(colorMatrix)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = "+${country.dialCode} (${country.name})",
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Selector(isSelected = isSelected)
    }
}

@Composable
private fun Selector(
    isSelected: Boolean = false,
    modifier: Modifier= Modifier
){
    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) SurfaceSecondary else SurfaceLighter
    )
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(animatedBackground),
        contentAlignment = Alignment.Center
    ){
        AnimatedVisibility(visible = isSelected) {
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(Resources.Icon.Checkmark),
                contentDescription = "Checkmark icon",
                tint = IconWhite
            )
        }
    }
}

fun List<Country>.filterByQuery(query:String): List<Country>{
    val queryLower = query.lowercase()
    val queryInt = query.toIntOrNull()
    return this.filter {
        it.name.contains(queryLower)||
                it.name.lowercase().contains(queryLower)||
                (queryInt!=null && it.dialCode == queryInt)
    }
}