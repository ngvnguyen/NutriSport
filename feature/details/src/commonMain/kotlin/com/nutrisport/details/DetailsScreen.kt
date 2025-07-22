package com.nutrisport.details

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sf.nutrisport.BorderIdle
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.component.InfoCard
import com.sf.nutrisport.component.LoadingCard
import com.sf.nutrisport.component.PrimaryButton
import com.sf.nutrisport.component.QuantityCounter
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.domain.QuantityCounterSize
import com.sf.nutrisport.robotoCondensedFont
import com.sf.nutrisport.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navigateBack:()->Unit
){

    val messageBarState = rememberMessageBarState()
    val viewModel = koinViewModel<DetailsViewModel>()
    val product by viewModel.product.collectAsState()
    val quantity = viewModel.quantity
    val selectedFlavor = viewModel.selectedFlavor
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
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
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow Icon",
                            tint = IconPrimary
                        )
                    }
                },
                actions = {
                    QuantityCounter(
                        size = QuantityCounterSize.Large,
                        onMinusClick = viewModel::updateQuantity,
                        onPlusClick = viewModel::updateQuantity,
                        value = quantity
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        }
    ){padding->
        product.DisplayResult(
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
            onLoading = {
                LoadingCard(modifier = Modifier.fillMaxSize())
            },
            onError = {message->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            },
            onSuccess = {selectedProduct->
                ContentWithMessageBar(
                    modifier = Modifier
                        .fillMaxSize(),
                    messageBarState = messageBarState,
                    contentBackgroundColor = Surface,
                    errorMaxLines = 2
                ) {


                    Column {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp)
                                .padding(top = 12.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = BorderIdle,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                model = ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(selectedProduct.thumbnail)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Product thumbnail",
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AnimatedContent(
                                    targetState = selectedProduct.category
                                ) {  category->
                                    if (ProductCategory.valueOf(category) == ProductCategory.Accessories){
                                        Spacer(modifier = Modifier.weight(1f))
                                    }else{
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(Resources.Icon.Weight),
                                                contentDescription = "Weight icon",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${selectedProduct.weight ?:0}g",
                                                fontSize = FontSize.REGULAR,
                                                color = TextPrimary,
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "$${selectedProduct.price}",
                                    fontSize = FontSize.MEDIUM,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = selectedProduct.title,
                                fontSize = FontSize.EXTRA_MEDIUM,
                                fontWeight = FontWeight.Medium,
                                fontFamily = robotoCondensedFont(),
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = selectedProduct.description,
                                fontSize = FontSize.REGULAR,
                                lineHeight = FontSize.REGULAR*1.3,
                                color = TextPrimary
                            )
                        }

                        Column(
                            modifier = Modifier
                                .background(
                                    if (selectedProduct.flavors?.isNotEmpty() == true)
                                        SurfaceLighter else Surface
                                )
                                .padding(24.dp)
                        ) {
                            if (selectedProduct.flavors?.isNotEmpty() == true){
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    selectedProduct.flavors?.forEach { flavor->
                                        FlavorChip(
                                            flavor = flavor,
                                            isSelected = flavor == selectedFlavor,
                                            onClick = {
                                                viewModel.updateFlavor(flavor)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                            }

                            PrimaryButton(
                                icon = Resources.Icon.ShoppingCart,
                                text = "Add to Cart",
                                onClick = {
                                    viewModel.addItemToCart(
                                        onSuccess = {messageBarState.addSuccess("Product added to cart!")},
                                        onError = {message->messageBarState.addError(message)}
                                    )
                                },
                                enabled = if (selectedProduct.flavors?.isNotEmpty() == true) selectedFlavor != null
                                else true
                            )
                        }
                    }

                }
            }
        )


    }
}