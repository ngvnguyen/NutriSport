package com.nutrisport.data.domain

import com.sf.nutrisport.domain.CartItem
import com.sf.nutrisport.domain.Customer
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun createCustomer(
        user: FirebaseUser?,
        onSuccess: ()->Unit,
        onError: (String)->Unit
    )
    suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError : (String)->Unit
    )
    fun getCurrentUserId():String?
    suspend fun signOut() : RequestState<Unit>
    fun readCustomerFlow(): Flow<RequestState<Customer>>

    suspend fun addItemToCart(
        cartItem: CartItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun updateCartItemQuantity(
        id:String,
        quantity:Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
    suspend fun deleteCartItem(
        id:String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}