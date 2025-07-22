package com.nutrisport.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrisport.home.domain.DrawerItem
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.domain.Customer
import com.sf.nutrisport.util.RequestState

@Composable
fun CustomDrawer(
    customer: RequestState<Customer>,
    modifier: Modifier = Modifier,
    onProfileClick:()->Unit,
    onContactUsClick:()->Unit,
    onSignOutClick:()->Unit,
    onAdminPanelClick:()->Unit
){
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
            .padding(horizontal = 12.dp)
    ){
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "NUTRISPORT",
            textAlign = TextAlign.Center,
            fontFamily = bebasNeueFont(),
            fontSize = FontSize.EXTRA_LARGE,
            color = TextSecondary
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Healthy Lifestyle",
            textAlign = TextAlign.Center,
            fontSize = FontSize.REGULAR,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(50.dp))
        DrawerItem.entries.take(5).forEach { item->
            DrawerItemCard(
                drawerItem = item,
                onClick = {
                    when(item){
                        DrawerItem.Profile -> onProfileClick()
                        DrawerItem.Contact -> onContactUsClick()
                        DrawerItem.SignOut -> onSignOutClick()
                        else -> {}
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        AnimatedContent(
            targetState = customer
        ) {
            if (it.isSuccess() && it.getSuccessData().isAdmin){
                DrawerItemCard(
                    drawerItem = DrawerItem.Admin,
                    onClick = onAdminPanelClick
                )
            }

        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}