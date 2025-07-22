package com.nutrisport.admin_panel

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.BorderIdle
import com.sf.nutrisport.ButtonPrimary
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.component.InfoCard
import com.sf.nutrisport.component.LoadingCard
import com.sf.nutrisport.component.ProductCard
import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.util.DisplayResult
import com.sf.nutrisport.util.RequestState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navigateBack:()->Unit,
    navigateToManagerProduce:(String?)->Unit
){
    val viewModel = koinViewModel<AdminPanelViewModel>()
    val products = viewModel.filteredProducts.collectAsState()
    var searchBarVisible by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchBarVisible) {
        if (searchBarVisible) focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            AnimatedContent(
                targetState = searchBarVisible
            ) {visible->
                if (visible) {
                    SearchBar(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        inputField = {
                            SearchBarDefaults.InputField(
                                modifier = Modifier.focusRequester(focusRequester),
                                query = searchQuery,
                                onQueryChange = {
                                    viewModel.updateSearchQuery(it)
                                },
                                expanded = false,
                                onExpandedChange = { },
                                onSearch = {},
                                placeholder = {
                                    Text(
                                        text = "Search here",
                                        fontSize = FontSize.REGULAR,
                                        color = TextPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        modifier = Modifier.size(14.dp),
                                        onClick = {
                                            if (searchQuery.isNotEmpty()){
                                                viewModel.updateSearchQuery("")
                                            }else searchBarVisible = false

                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close icon",
                                        )
                                    }
                                }
                            )
                        },
                        colors = SearchBarColors(
                            containerColor = SurfaceLighter,
                            dividerColor = BorderIdle
                        ),
                        content = {},
                        expanded = false,
                        onExpandedChange = {}


                    )
                }else{
                    TopAppBar(
                        title = {
                            Text(
                                text = "Admin Panel",
                                fontFamily = bebasNeueFont(),
                                fontSize = FontSize.LARGE,
                                color = TextPrimary
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Surface,
                            scrolledContainerColor = Surface,
                            titleContentColor = TextPrimary,
                            navigationIconContentColor = IconPrimary,
                            actionIconContentColor = IconPrimary
                        ),
                        actions = {
                            IconButton(
                                onClick = {
                                    searchBarVisible = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Resources.Icon.Search),
                                    contentDescription = "Search icon",
                                    tint = IconPrimary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = navigateBack) {
                                Icon(
                                    painter = painterResource(Resources.Icon.BackArrow),
                                    contentDescription = "Close Menu",
                                    tint = IconPrimary
                                )
                            }
                        }

                    )
                }

            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick={navigateToManagerProduce(null)},
                containerColor = ButtonPrimary,
                contentColor = IconPrimary,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Icon(
                    painter = painterResource(Resources.Icon.Plus),
                    contentDescription = "Add icon"
                )
            }
        }
    ){padding->
        products.value.DisplayResult(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
            onIdle = {},
            onSuccess = {lastProducts->
                AnimatedContent(
                    targetState = lastProducts
                ) {products->
                    if (products.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = products,
                                key = {it.id}
                            ) {product->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        navigateToManagerProduce(product.id)
                                    }
                                )
                            }
                        }
                    }else{
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Oops!",
                            subtitle = "Product not found!"
                        )
                    }

                }
            },
            onLoading = {
                LoadingCard(
                    modifier = Modifier.fillMaxSize()
                )
            },
            onError = {message->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            }
        )
    }
}