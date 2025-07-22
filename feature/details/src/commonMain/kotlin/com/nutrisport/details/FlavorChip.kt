package com.nutrisport.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.modifiers.TextAutoSizeLayoutScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.BorderIdle
import com.sf.nutrisport.BorderSecondary
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.domain.ProductCategory

@Composable
fun FlavorChip(
    flavor: String,
    isSelected:Boolean = false,
    onClick:()->Unit
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(Surface)
            .border(
                width = 1.dp,
                color = if (isSelected) BorderSecondary else BorderIdle,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = flavor,
            color = if (isSelected) TextSecondary else TextPrimary,
            fontSize = FontSize.SMALL,
            fontWeight = FontWeight.Medium
        )
    }
}