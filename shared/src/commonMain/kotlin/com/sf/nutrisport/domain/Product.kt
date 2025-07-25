package com.sf.nutrisport.domain

import androidx.compose.ui.graphics.Color
import com.sf.nutrisport.CategoryBlue
import com.sf.nutrisport.CategoryGreen
import com.sf.nutrisport.CategoryPurple
import com.sf.nutrisport.CategoryRed
import com.sf.nutrisport.CategoryYellow
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
@OptIn(ExperimentalTime::class)
@Serializable
data class Product(
    val id:String,
    val createdAt:Long = Clock.System.now().toEpochMilliseconds(),
    val title:String,
    val description:String,
    val thumbnail:String,
    val category:String,
    val flavors: List<String>?=null,
    val weight:Int?=null,
    val price: Double,
    val isPopular:Boolean = false,
    val isDiscounted:Boolean = false,
    val isNew:Boolean = false
)

enum class ProductCategory(
    val title:String,
    val color:Color
){
    Protein(
        title = "Protein",
        color = CategoryYellow
    ),
    Creatine(
        title ="Creatine",
        color = CategoryBlue
    ),
    PreWorkout(
        title = "Pre-Workout",
        color = CategoryGreen
    ),
    Gainers(
        title = "Gainers",
        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
        color = CategoryRed
    )
}