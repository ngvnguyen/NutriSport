package com.sf.nutrisport.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.IconPrimary

@Composable
fun LoadingCard(
    modifier : Modifier = Modifier,

){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            color = IconPrimary,
            strokeWidth = 2.dp,
            modifier = Modifier.size(24.dp)
        )
    }
}