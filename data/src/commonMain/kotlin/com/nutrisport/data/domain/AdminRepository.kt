package com.nutrisport.data.domain

import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getCurrentUserId():String?
    suspend fun createNewProduct(
        product: Product,
        onSuccess:()->Unit,
        onError:(String)->Unit
    )
    suspend fun uploadImageToStorage(file:File):String?
    suspend fun deleteImageFromStorage(
        downloadUrl:String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun readLastTenProducts(): Flow<RequestState<List<Product>>>

    suspend fun readProductById(id:String): RequestState<Product>

    suspend fun updateImageThumbnail(
        productId:String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError:(String)->Unit
    )

    suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError:(String)->Unit
    )

    suspend fun deleteProduct(
        productId:String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun searchProductsByTitle(
        searchQuery:String,
    ): Flow<RequestState<List<Product>>>
}