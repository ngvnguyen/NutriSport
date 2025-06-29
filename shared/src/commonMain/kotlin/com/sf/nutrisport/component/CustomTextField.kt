package com.sf.nutrisport.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.Alpha
import com.sf.nutrisport.BorderError
import com.sf.nutrisport.BorderIdle
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.IconSecondary
import com.sf.nutrisport.SurfaceDarker
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.TextPrimary


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value:String,
    onValueChange :(String)-> Unit,
    placeholder:String?=null,
    enabled:Boolean = true,
    error:Boolean = false,
    expanded: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text
    )
){
    val borderColor by animateColorAsState(
        targetValue = if (error) BorderError else BorderIdle
    )
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp)),
        placeholder = {
            placeholder?.let {
                Text(
                    text = it,
                    fontSize = FontSize.REGULAR
                )
            }
        },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        singleLine = !expanded,
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = TextPrimary,
            focusedTextColor = TextPrimary,
            disabledTextColor = TextPrimary.copy(alpha = Alpha.DISABLE) ,
            errorTextColor = TextPrimary,
            focusedContainerColor = SurfaceLighter,
            unfocusedContainerColor = SurfaceLighter,
            disabledContainerColor = SurfaceDarker,
            errorContainerColor = SurfaceLighter,
            focusedPlaceholderColor = TextPrimary.copy(alpha = Alpha.HALF),
            unfocusedPlaceholderColor = TextPrimary.copy(alpha = Alpha.HALF),
            disabledPlaceholderColor = TextPrimary.copy(alpha = Alpha.DISABLE),
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = IconSecondary,
                backgroundColor = Color.Unspecified
            ),
            cursorColor = IconSecondary
        )

    )
}