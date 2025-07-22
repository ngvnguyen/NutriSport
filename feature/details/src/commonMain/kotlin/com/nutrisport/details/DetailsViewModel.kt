package com.nutrisport.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisport.data.domain.CustomerRepository
import com.nutrisport.data.domain.ProductRepository
import com.sf.nutrisport.domain.CartItem
import com.sf.nutrisport.util.RequestState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId = savedStateHandle.get<String>("id")
    val product = productRepository.readProductByIdFlow(productId?: "")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    var quantity by mutableStateOf(1)
        private set

    var selectedFlavor:String? by mutableStateOf(null)
        private set

    fun updateQuantity(value:Int){
        quantity = value
    }

    fun updateFlavor(value:String?){
        selectedFlavor = value
    }

    fun addItemToCart(
        onSuccess:()->Unit,
        onError:(String)->Unit
    ){
        viewModelScope.launch {
            if (productId != null){
                customerRepository.addItemToCart(
                    cartItem = CartItem(
                        productId = productId,
                        flavor = selectedFlavor,
                        quantity = quantity
                    ),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }else{
                onError("Product Id is not found")
            }
        }
    }
}