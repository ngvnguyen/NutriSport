package com.sf.nutrisport.domain

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class CartItem(
    val id:String = Uuid.random().toHexString(),
    val productId:String,
    val flavor:String?=null,
    val quantity:Int
)
