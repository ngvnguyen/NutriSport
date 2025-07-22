package com.nutrisport.data

import com.nutrisport.data.domain.AdminRepository
import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withTimeout
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AdminRepositoryImpl: AdminRepository {
    override fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun createNewProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try{
            val currentUserId = getCurrentUserId()
            if (currentUserId!=null){
                val firestore = Firebase.firestore
                val productCollection = firestore.collection("product")
                productCollection.document(product.id).set(product.copy(title = product.title.lowercase()) )
                onSuccess()
            }else {
                onError("User is not available")
            }
        }catch (e: Exception){
            onError("Error while creating new product: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun uploadImageToStorage(file: File): String? {
        return getCurrentUserId()?.let{
            val storage = Firebase.storage.reference
            val imagePath = storage.child(path = "image/${Uuid.random().toHexString()}")
            try{
                withTimeout(timeMillis = 20000) {
                    imagePath.putFile(file)
                    imagePath.getDownloadUrl()
                }
            }catch (e: Exception){
                null
            }
        }
    }

    override suspend fun deleteImageFromStorage(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try{
            val storagePath = extractFirebaseStoragePath(downloadUrl)
            storagePath?.let {
                Firebase.storage.reference(storagePath).delete()
                onSuccess()
            }?: onError("Storage path is null")
        }catch (e: Exception){
            onError("Error while deleting image: ${e.message}")
        }
    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow{
        try{
            val userId = getCurrentUserId()
            if(userId!=null){
                val database = Firebase.firestore
                database.collection("product")
                    .orderBy("createdAt",direction = Direction.DESCENDING)
                    .limit(10)
                    .snapshots
                    .collectLatest {query->
                        val products = query.documents.map { document->
                            Product(
                                id = document.id,
                                createdAt = document.get("createdAt"),
                                title = document.get("title"),
                                description = document.get("description"),
                                thumbnail = document.get("thumbnail"),
                                category = document.get("category"),
                                flavors = document.get("flavors"),
                                weight = document.get("weight"),
                                price = document.get("price"),
                                isPopular = document.get("isPopular"),
                                isDiscounted = document.get("isDiscounted"),
                                isNew = document.get("isNew")
                            )
                        }
                        send(RequestState.Success(products.map { it.copy(title= it.title.uppercase()) }))
                    }
            }else {
                send(RequestState.Error("User is not available"))
            }
        }catch (e: Exception){
            send(RequestState.Error("Error while reading the last ten products: ${e.message}"))
        }
    }

    override suspend fun readProductById(id: String): RequestState<Product> {
        return try{
            val userId = getCurrentUserId()
            userId?.let {
                val firestore = Firebase.firestore
                val document = firestore.collection("product")
                    .document(id)
                    .get()

                if (document.exists){
                    val product = Product(
                        id = document.id,
                        createdAt = document.get("createdAt"),
                        title = document.get("title"),
                        description = document.get("description"),
                        thumbnail = document.get("thumbnail"),
                        category = document.get("category"),
                        flavors = document.get("flavors"),
                        weight = document.get("weight"),
                        price = document.get("price"),
                        isPopular = document.get("isPopular"),
                        isDiscounted = document.get("isDiscounted"),
                        isNew = document.get("isNew")
                    )

                    RequestState.Success(product.copy(title = product.title.uppercase()))
                }else
                    RequestState.Error("Selected product not found")

            }?: RequestState.Error("User is not available")
        }catch (e: Exception){
            RequestState.Error("Error while reading a selected product: ${e.message}")
        }
    }

    override suspend fun updateImageThumbnail(
        productId: String,
        downloadUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            userId?.let {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists){
                    productCollection.document(productId)
                        .update("thumbnail" to downloadUrl)
                    onSuccess()
                }else onError("Selected product not found")
            }?: onError("User is not available")
        }catch (e: Exception){
            onError("Error while updating a thumbnail image: ${e.message}")
        }
    }

    override suspend fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            userId?.let {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(product.id)
                    .get()

                if (existingProduct.exists){
                    productCollection.document(product.id)
                        .update(
                            "title" to product.title.lowercase(),
                            "description" to product.description,
                            "thumbnail" to product.thumbnail,
                            "category" to product.category,
                            "flavors" to product.flavors,
                            "weight" to product.weight,
                            "price" to product.price,
                            "isPopular" to product.isPopular,
                            "isDiscounted" to product.isDiscounted,
                            "isNew" to product.isNew
                            // no createdAt update
                        )
                    onSuccess()
                }else onError("Selected product not found")
            }?: onError("User is not available")
        }catch (e: Exception){
            onError("Error while updating product: ${e.message}")
        }
    }

    override suspend fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            userId?.let {
                val database = Firebase.firestore
                val productCollection = database.collection("product")
                val existingProduct = productCollection
                    .document(productId)
                    .get()

                if (existingProduct.exists){
                    productCollection.document(productId)
                        .delete()
                    onSuccess()
                }else onError("Selected product not found")
            }?: onError("User is not available")
        }catch (e: Exception){
            onError("Error while deleting product: ${e.message}")
        }
    }

    override fun searchProductsByTitle(searchQuery: String): Flow<RequestState<List<Product>>> = channelFlow {
        try{
            val userId = getCurrentUserId()
            if (userId!=null){
                val database = Firebase.firestore
//                val queryText = searchQuery.trim()
                database.collection("product")
                    .orderBy("createdAt",direction = Direction.DESCENDING)
                    .snapshots
                    .collectLatest { query->
                        val products = query.documents.map { document ->
                            Product(
                                id = document.id,
                                createdAt = document.get("createdAt"),
                                title = document.get("title"),
                                description = document.get("description"),
                                thumbnail = document.get("thumbnail"),
                                category = document.get("category"),
                                flavors = document.get("flavors"),
                                weight = document.get("weight"),
                                price = document.get("price"),
                                isPopular = document.get("isPopular"),
                                isDiscounted = document.get("isDiscounted"),
                                isNew = document.get("isNew")
                            )
                        }
                        send(
                            RequestState.Success(
                                products
                                    .filter { it.title.contains(searchQuery) }
                                    .map { it.copy(title = it.title.uppercase()) }
                            )
                        )
                    }
            }else send(RequestState.Error("User is not available"))
        }catch (e: Exception){
            send(RequestState.Error("Error while searching products: ${e.message}"))
        }
    }

    private fun extractFirebaseStoragePath(downloadUrl: String):String?{
        val startIndex = downloadUrl.indexOf("/o/")+3
        if (startIndex<3) return null
        val endIndex = downloadUrl.indexOf("?",startIndex)
        val encodePath = if (endIndex!= -1){
            downloadUrl.substring(startIndex,endIndex)
        }else downloadUrl.substring(startIndex)

        return decodeFirebasePath(encodePath)
    }

    private fun decodeFirebasePath(encodedPath:String):String{
        return encodedPath
            .replace("%2F","/")
            .replace("%20"," ")
    }
}