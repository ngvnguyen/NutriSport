package com.nutrisport.cart

import ContentWithMessageBar
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutrisport.cart.component.CartItemCard
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.component.InfoCard
import com.sf.nutrisport.component.LoadingCard
import com.sf.nutrisport.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@Composable
fun CartScreen(){
    val messageBarState = rememberMessageBarState()
    val viewModel = koinViewModel<CartViewModel>()
    val cartItemWithProducts by viewModel.cartItemWithProducts.collectAsState()
    ContentWithMessageBar(
        contentBackgroundColor = Surface,
        messageBarState = messageBarState,
        errorMaxLines = 2
    ) {
        cartItemWithProducts.DisplayResult(
            transitionSpec = fadeIn() togetherWith fadeOut(),
            onLoading = {
                LoadingCard(modifier = Modifier.fillMaxSize())
            },
            onError = {message->
                InfoCard(
                    image = Resources.Image.ShoppingCart,
                    title = "Oops!",
                    subtitle = message
                )
            },
            onSuccess = {data->
                if (data.isNotEmpty()){
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = data,
                            key = {it.first.id}
                        ) {pair->
                            CartItemCard(
                                product = pair.second,
                                cartItem = pair.first,
                                onMinusClick = {quantity->
                                    viewModel.updateCartItemQuantity(
                                        id = pair.first.id,
                                        quantity = quantity,
                                        onSuccess = {},
                                        onError = {messageBarState.addError(it)}
                                    )
                                },
                                onPlusClick = { quantity ->
                                    viewModel.updateCartItemQuantity(
                                        id = pair.first.id,
                                        quantity = quantity,
                                        onSuccess = {},
                                        onError = { messageBarState.addError(it) }
                                    )
                                },
                                onDeleteClick = {
                                    viewModel.deleteCartItem(
                                        id = pair.first.id,
                                        onSuccess = {},
                                        onError = {message-> messageBarState.addError(message)}
                                    )
                                }
                            )
                        }
                    }
                }else{
                    InfoCard(
                        image = Resources.Image.ShoppingCart,
                        title = "Empty Cart",
                        subtitle = "Check some of our products."
                    )
                }

            }
        )
    }
}