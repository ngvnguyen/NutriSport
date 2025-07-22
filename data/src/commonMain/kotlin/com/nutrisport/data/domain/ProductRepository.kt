package com.nutrisport.data.domain

import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
    fun readProducts(): Flow<RequestState<List<Product>>>
    //fun readNewProducts(): Flow<RequestState<List<Product>>>
    fun readProductByIdFlow(id:String): Flow<RequestState<Product>>
    fun readProductByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>>
    fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>>
}