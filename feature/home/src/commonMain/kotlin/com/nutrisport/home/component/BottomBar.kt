package com.nutrisport.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.nutrisport.home.domain.BottomBarDestination
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.IconSecondary
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.domain.Customer
import com.sf.nutrisport.navigation.Screen
import com.sf.nutrisport.util.RequestState
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(
    modifier : Modifier = Modifier,
    customer : RequestState<Customer>,
    selected : BottomBarDestination,
    onSelect : (BottomBarDestination)->Unit
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceLighter)
            .padding(
                vertical = 24.dp,
                horizontal = 36.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomBarDestination.entries.forEach { destination->
            val animatedTint by animateColorAsState(
                targetValue = if (selected == destination) IconSecondary else IconPrimary
            )

            Box(contentAlignment = Alignment.TopEnd){
                Icon(
                    painter = painterResource(destination.icon),
                    tint = animatedTint,
                    contentDescription = "Bottom Bar destination icon",
                    modifier = Modifier.clickable{
                        onSelect(destination)
                    }
                )
                if (destination == BottomBarDestination.Cart){
                    AnimatedContent(
                        targetState = customer
                    ) { customerState->
                        if (customerState.isSuccess() && customerState.getSuccessData().cart.isNotEmpty()){
                            Box(
                                modifier = Modifier
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(IconSecondary)
                            )
                        }

                    }
                }
            }

        }
    }
}