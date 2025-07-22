package com.nutrisport.manage_product

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sf.nutrisport.BorderIdle
import com.sf.nutrisport.ButtonPrimary
import com.sf.nutrisport.ButtonSecondary
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.IconSecondary
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceDarker
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.SurfaceSecondary
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.component.AlertTextField
import com.sf.nutrisport.component.CustomTextField
import com.sf.nutrisport.component.ErrorCard
import com.sf.nutrisport.component.LoadingCard
import com.sf.nutrisport.component.PrimaryButton
import com.sf.nutrisport.component.dialog.CategoriesDialog
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.DisplayResult
import com.sf.nutrisport.util.RequestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    id:String?,
    navigateBack:()->Unit
) {
    val messageBarState = rememberMessageBarState()
    var showCategoriesDialog by remember { mutableStateOf(false) }
    val viewModel = koinViewModel<ManageProductViewModel>()
    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid
    val thumbnailUploaderState = viewModel.thumbnailUploaderState
    var dropDownMenuOpen by remember { mutableStateOf(false) }

    val photoPicker = koinInject<PhotoPicker>()
    photoPicker.InitializePhotoPicker { file->
        viewModel.uploadThumbnailToStorage(
            file = file,
            onSuccess = {
                messageBarState.addSuccess("Successfully uploaded thumbnail!")
            }
        )
    }

    AnimatedVisibility(visible = showCategoriesDialog) {
        CategoriesDialog(
            category =screenState.category,
            onDismiss = {showCategoriesDialog = false},
            onConfirmClick = {c->
                showCategoriesDialog = false
                viewModel.updateCategory(c)
            }
        )
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id!=null) "Edit Product" else "Add Product",
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
                            contentDescription = "Close Menu",
                            tint = IconPrimary
                        )
                    }
                },
                actions = {
                    id?.let {
                        Box() {
                            IconButton(onClick = {dropDownMenuOpen = !dropDownMenuOpen}) {
                                Icon(
                                    painter = painterResource(Resources.Icon.VerticalMenu),
                                    contentDescription = "Menu icon",
                                    tint = IconPrimary
                                )

                            }
                            DropdownMenu(
                                expanded = dropDownMenuOpen,
                                onDismissRequest = {dropDownMenuOpen = false},
                                containerColor = Surface
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Delete",
                                            color = TextPrimary
                                        )
                                    },
                                    onClick = {
                                        dropDownMenuOpen = false
                                        viewModel.deleteProduct(
                                            onSuccess = navigateBack,
                                            onError = {message-> messageBarState.addError(message)}
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Menu icon",
                                            tint = IconPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )

                                    }
                                )
                            }
                        }
                    }
                }

            )
        }
    ){padding->
        ContentWithMessageBar(
            messageBarState = messageBarState,
            errorMaxLines = 2,
            modifier = Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
            contentBackgroundColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp,bottom = 24.dp)

            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = BorderIdle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(SurfaceLighter)
                            .clickable(enabled = thumbnailUploaderState.isIdle()) {
                                photoPicker.open()
                            },
                        contentAlignment = Alignment.Center
                    ){
                        thumbnailUploaderState.DisplayResult(
                            onIdle = {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(Resources.Icon.Plus),
                                    contentDescription = "Plus icon",
                                    tint = IconPrimary
                                )
                            },
                            onSuccess = {
                                Box(modifier = Modifier.fillMaxSize()){
                                    AsyncImage(
                                        model = ImageRequest
                                            .Builder(LocalPlatformContext.current)
                                            .data(screenState.thumbnail)
                                            .crossfade(enable = true)
                                            .build(),
                                        modifier = Modifier.fillMaxSize(),
                                        contentDescription = "Product Thumbnail Image",
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .clip(RoundedCornerShape(6.dp))
                                            .padding(top=12.dp,end=12.dp)
                                            .background(ButtonPrimary)
                                            .padding(12.dp)
                                            .clickable {
                                                viewModel.deleteThumbnailFromStorage(
                                                    onSuccess={messageBarState.addSuccess("Thumbnail remove successfully")},
                                                    onError = {message->
                                                        messageBarState.addError(message)
                                                    }
                                                )
                                            },

                                    ){
                                        Icon(
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete Icon",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            onError = {message->
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ErrorCard(message = message)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TextButton(
                                        onClick = {
                                            viewModel.updateThumbnailUploaderState(RequestState.Idle)
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text ="Try again",
                                            fontSize = FontSize.SMALL,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            },
                            onLoading = {
                                LoadingCard(modifier = Modifier.fillMaxSize())
                            }
                        )
                    }

                    CustomTextField(
                        value = screenState.title,
                        onValueChange = viewModel::updateTitle,
                        placeholder = "Title"
                    )
                    CustomTextField(
                        modifier = Modifier.height(168.dp),
                        value = screenState.description,
                        onValueChange = viewModel::updateDescription,
                        placeholder = "Description",
                        expanded = true
                    )

                    AlertTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = screenState.category.title,
                        onClick={showCategoriesDialog = true}
                    )

                    AnimatedVisibility(
                        visible = screenState.category!= ProductCategory.Accessories
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomTextField(
                                value = "${screenState.weight ?:""}",
                                onValueChange = {viewModel.updateWeight(it.toIntOrNull())},
                                placeholder = "Weight",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            CustomTextField(
                                value = screenState.flavors,
                                onValueChange = viewModel::updateFlavors,
                                placeholder = "Flavors (Optional)"
                            )
                        }

                    }

                    CustomTextField(
                        value ="${screenState.price}",
                        onValueChange = {viewModel.updatePrice(it.toDoubleOrNull()?:0.0)},
                        placeholder = "Price",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text ="New",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )

                            Switch(
                                checked = screenState.isNew,
                                onCheckedChange = viewModel::updateNew,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text ="Popular",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )

                            Switch(
                                checked = screenState.isPopular,
                                onCheckedChange = viewModel::updatePopular,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text ="Discounted",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )

                            Switch(
                                checked = screenState.isDiscounted,
                                onCheckedChange = viewModel::updateDiscounted,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                PrimaryButton(
                    text = if (id == null) "Add New Product" else "Update",
                    onClick = {
                        if (id!= null){
                            viewModel.updateProduct(
                                onSuccess = {
                                    messageBarState.addSuccess("Product successfully updated!")
                                },
                                onError = {message->
                                    messageBarState.addError(message)
                                }
                            )
                        }else viewModel.createNewProduct(
                            onSuccess = {
                                messageBarState.addSuccess("Product successfully added!")
                            },
                            onError = {message->
                                messageBarState.addError(message)
                            }
                        )
                    },
                    icon = if (id != null) Resources.Icon.Checkmark
                        else Resources.Icon.Plus,
                    enabled = isFormValid
                )
            }
        }
    }
}