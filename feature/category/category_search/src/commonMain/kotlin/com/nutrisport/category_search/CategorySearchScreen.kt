package com.nutrisport.category_search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.BorderIdle
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
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySearchScreen(
    navigateToDetails:(String)->Unit,
    navigateBack:()->Unit,
    category: ProductCategory
){
    val viewModel = koinViewModel<CategorySearchViewModel>()
    val products by viewModel.filterProduct.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var searchBarVisible by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(searchBarVisible) {
        if (searchBarVisible) focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = SurfaceLighter,
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
                                text = category.title,
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
        }
    ) {padding->
        products.DisplayResult(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
            onLoading = {
                LoadingCard(modifier = Modifier.fillMaxSize())
            },
            onSuccess = {categoryProducts->
                AnimatedContent(
                    targetState = categoryProducts
                ) {products->
                    if (products.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = products,
                                key = {it.id}
                            ) {product->
                                ProductCard(
                                    product = product,
                                    onClick = {navigateToDetails(it.id)}
                                )
                            }
                        }
                    }else{
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Nothing here!",
                            subtitle = "We couldn't find any product."
                        )
                    }
                }
            },
            onError = {message->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            },
            transitionSpec = fadeIn() togetherWith fadeOut()
        )
    }


}