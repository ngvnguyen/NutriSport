package com.nutrisport.data

import com.nutrisport.data.domain.ProductRepository
import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class ProductRepositoryImpl: ProductRepository{
    fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>>
        = channelFlow{
            try{
                val userId = getCurrentUserId()
                if(userId!=null){
                    val database = Firebase.firestore
                    database.collection("product")
                        .where {"isDiscounted" equalTo true}
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

    override fun readProducts(): Flow<RequestState<List<Product>>>
        = channelFlow{
            try{
                val userId = getCurrentUserId()
                if(userId!=null){
                    val database = Firebase.firestore
                    database.collection("product")
                        .orderBy("createdAt", Direction.DESCENDING)
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

    override fun readProductByIdFlow(id: String): Flow<RequestState<Product>>
        = channelFlow {
            try{
                val userId = getCurrentUserId()
                userId?.let {
                    val firestore = Firebase.firestore
                    firestore.collection("product")
                        .document(id)
                        .snapshots
                        .collectLatest { document->
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

                                send(RequestState.Success(product.copy(title = product.title.uppercase())))
                            }else
                                send(RequestState.Error("Selected product not found"))
                        }


                }?: send(RequestState.Error("User is not available"))
            }catch (e: Exception){
                send(RequestState.Error("Error while reading selected product: ${e.message}"))
            }
        }

    override fun readProductByIdsFlow(ids: List<String>): Flow<RequestState<List<Product>>>
        = channelFlow {
            try{
                val userId = getCurrentUserId()
                userId?.let {
                    val firestore = Firebase.firestore

                    val allProduct = mutableListOf<Product>()
                    val chunks = ids.chunked(10)

                    chunks.forEachIndexed { index,chunk->
                        firestore.collection("product")
                            .where { "id" inArray chunk }
                            .snapshots
                            .collectLatest { query->
                                val products = query.documents.map {document->
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
                                allProduct.addAll(products)
                                if (index == chunks.lastIndex){
                                    send(RequestState.Success(allProduct.map { it.copy(title = it.title.uppercase()) }))
                                }
                            }
                    }

                }?: send(RequestState.Error("User is not available"))
            }catch (e: Exception){
                send(RequestState.Error("Error while reading selected product: ${e.message}"))
            }
        }

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<RequestState<List<Product>>>
        = channelFlow {
            try{
                val userId = getCurrentUserId()
                userId?.let {
                    val firestore = Firebase.firestore
                    firestore.collection("product")
                        .where { "category" equalTo category.name }
                        .snapshots
                        .collectLatest { query->
                            val products = query.documents.map{document->
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
                            send(RequestState.Success(products.map { it.copy(title = it.title.uppercase()) }))
                        }
                }?: send(RequestState.Error("User is not available"))
            }catch (e: Exception){
                send(RequestState.Error("Error while reading selected product category: ${e.message}"))
            }
        }

//    override fun readNewProducts(): Flow<RequestState<List<Product>>>
//        = channelFlow{
//            try{
//                val userId = getCurrentUserId()
//                if(userId!=null){
//                    val database = Firebase.firestore
//                    database.collection("product")
//                        .where { "isNew" equalTo true}
//                        .snapshots
//                        .collectLatest {query->
//                            val products = query.documents.map { document->
//                                Product(
//                                    id = document.id,
//                                    createdAt = document.get("createdAt"),
//                                    title = document.get("title"),
//                                    description = document.get("description"),
//                                    thumbnail = document.get("thumbnail"),
//                                    category = document.get("category"),
//                                    flavors = document.get("flavors"),
//                                    weight = document.get("weight"),
//                                    price = document.get("price"),
//                                    isPopular = document.get("isPopular"),
//                                    isDiscounted = document.get("isDiscounted"),
//                                    isNew = document.get("isNew")
//                                )
//                            }
//                            send(RequestState.Success(products.map { it.copy(title= it.title.uppercase()) }))
//                        }
//                }else {
//                    send(RequestState.Error("User is not available"))
//                }
//            }catch (e: Exception){
//                send(RequestState.Error("Error while reading the last ten products: ${e.message}"))
//            }
//        }
}
