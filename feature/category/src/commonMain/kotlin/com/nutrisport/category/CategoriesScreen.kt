package com.nutrisport.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutrisport.category.component.CategoryCard
import com.sf.nutrisport.domain.ProductCategory

@Composable
fun CategoriesScreen(
    navigateToCategoriesSearch:(String)->Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductCategory.entries.forEach { category->
            CategoryCard(
                category = category,
                onClick = {navigateToCategoriesSearch(category.name)}
            )
        }
    }
}