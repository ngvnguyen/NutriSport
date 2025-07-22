package com.nutrisport.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisport.data.domain.CustomerRepository
import com.nutrisport.data.domain.ProductRepository
import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository
): ViewModel(){
    val customer = customerRepository.readCustomerFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = customer
        .flatMapLatest { customerState->
            when(customerState){
                is RequestState.Error->
                    flowOf(RequestState.Error(customerState.getErrorMessage()))
                is RequestState.Success-> {
                    val cartItems = customerState.getSuccessData().cart
                    val productIds = cartItems.map { it.productId }.toSet()
                    if (productIds.isNotEmpty()){
                        productRepository.readProductByIdsFlow(productIds.toList())
                    }else flowOf(RequestState.Success(emptyList()))
                }
                else -> flowOf(RequestState.Loading)
            }
        }

    val cartItemWithProducts = combine(customer, products) {customerState,productState->
        when{
            customerState.isSuccess() && productState.isSuccess() -> {
                val cart = customerState.getSuccessData().cart
                val products = productState.getSuccessData()

                val result = cart.mapNotNull { cartItem->
                    val product = products.find { it.id == cartItem.productId }
                    product?.let{cartItem to it}
                }
                RequestState.Success(result)
            }

            customerState.isError()->
                RequestState.Error(customerState.getErrorMessage())
            productState.isError() ->
                RequestState.Error(productState.getErrorMessage())

            else -> RequestState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    fun updateCartItemQuantity(
        id:String,
        quantity:Int,
        onSuccess: ()->Unit,
        onError:(String)->Unit
    ){
        viewModelScope.launch {
            customerRepository.updateCartItemQuantity(
                id = id,
                quantity = quantity,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun deleteCartItem(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            customerRepository.deleteCartItem(
                id = id,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}