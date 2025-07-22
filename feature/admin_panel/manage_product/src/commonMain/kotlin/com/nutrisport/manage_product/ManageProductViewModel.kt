package com.nutrisport.manage_product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisport.data.domain.AdminRepository
import com.sf.nutrisport.domain.Product
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.RequestState
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ManageProductState(
    val id:String = Uuid.random().toHexString(),
    val title:String="",
    val description:String="",
    val thumbnail:String="thumbnail image",
    val category: ProductCategory = ProductCategory.Protein,
    val flavors: String="",
    val weight:Int?=null,
    val price: Double=0.0,
    val isNew:Boolean = false,
    val isPopular:Boolean = false,
    val isDiscounted:Boolean = false
)

fun ManageProductState.toProduct() = Product(
    id = id,
    title = title,
    description = description,
    thumbnail = thumbnail,
    category = category.name,
    flavors = flavors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    weight = weight,
    price = price,
    isNew = isNew,
    isPopular = isPopular,
    isDiscounted = isDiscounted
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId = savedStateHandle.get<String>("id") ?:""

    var screenState by mutableStateOf(ManageProductState())
        private set
    val isFormValid : Boolean
        get() = screenState.title.isNotBlank() &&
                screenState.description.isNotEmpty() &&
                screenState.thumbnail.isNotEmpty() &&
                screenState.price != 0.0

    var thumbnailUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)

    init{
        productId.takeIf { it.isNotEmpty() }?.let { id->
            viewModelScope.launch {
                val selectedProduct = adminRepository.readProductById(id)
                if (selectedProduct.isSuccess()){
                    val product = selectedProduct.getSuccessData()
                    updateId(product.id)
                    updateTitle(product.title)
                    updateDescription(product.description)
                    updateThumbnail(product.thumbnail)
                    if (product.thumbnail.isNotEmpty())
                        updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateCategory(ProductCategory.valueOf(product.category))
                    updateFlavors(product.flavors?.joinToString(",")?:"")
                    updateWeight(product.weight)
                    updatePrice(product.price)
                    updateNew(product.isNew)
                    updatePopular(product.isPopular)
                    updateDiscounted(product.isDiscounted)
                }
            }
        }
    }

    private fun updateId(value:String){
        screenState = screenState.copy(
            id = value
        )
    }

    fun updateThumbnailUploaderState(value: RequestState<Unit>){
        thumbnailUploaderState = value
    }

    fun updateTitle(value:String){
        screenState = screenState.copy(
            title = value
        )
    }

    fun updateDescription(value:String){
        screenState = screenState.copy(
            description = value
        )
    }

    fun updateThumbnail(value:String){
        screenState = screenState.copy(
            thumbnail = value
        )
    }

    fun updateCategory(value: ProductCategory){
        screenState = screenState.copy(
            category = value
        )
    }

    fun updateFlavors(value:String){
        screenState = screenState.copy(
            flavors = value
        )
    }
    fun updateWeight(value:Int?){
        screenState = screenState.copy(
            weight = value
        )
    }
    fun updatePrice(value: Double){
        screenState = screenState.copy(
            price = value
        )
    }

    fun updateNew(value: Boolean){
        screenState = screenState.copy(
            isNew = value
        )
    }

    fun updatePopular(value: Boolean){
        screenState = screenState.copy(
            isPopular = value
        )
    }

    fun updateDiscounted(value: Boolean){
        screenState = screenState.copy(
            isDiscounted = value
        )
    }

    fun createNewProduct(
        onSuccess:()->Unit,
        onError:(String)->Unit
    ){
        viewModelScope.launch {
            adminRepository.createNewProduct(
                product = screenState.toProduct(),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun uploadThumbnailToStorage(
        file: File?,
        onSuccess: () -> Unit
    ){
        if (file==null){
            updateThumbnailUploaderState(RequestState.Error("File is null. Error while selecting an image"))
            return
        }
        updateThumbnailUploaderState(RequestState.Loading)
        viewModelScope.launch {
            try{
                val downloaderUrl = adminRepository.uploadImageToStorage(file)

                if (downloaderUrl.isNullOrEmpty()){
                    throw Exception("Failed to retrieve download URL after uploading image")
                }

                productId.takeIf{it.isNotEmpty()}?.let { id->
                    adminRepository.updateImageThumbnail(
                        productId = id,
                        downloadUrl = downloaderUrl,
                        onSuccess = {
                            onSuccess()
                            updateThumbnailUploaderState(RequestState.Success(Unit))
                            updateThumbnail(downloaderUrl)
                        },
                        onError = {message->
                            throw Exception(message)
                        }
                    )
                }?:run{
                    onSuccess()
                    updateThumbnailUploaderState(RequestState.Success(Unit))
                    updateThumbnail(downloaderUrl)
                }

            }catch (e: Exception){
                updateThumbnailUploaderState(RequestState.Error("Error while uploading image: ${e.message}"))
            }
        }
    }

    fun updateProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        if (isFormValid){
            viewModelScope.launch {
                adminRepository.updateProduct(
                    product = screenState.toProduct(),
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        }else onError("Please fill in the information")
    }

    fun deleteThumbnailFromStorage(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            adminRepository.deleteImageFromStorage(
                downloadUrl = screenState.thumbnail,
                onSuccess = {

                    productId.takeIf { it.isNotEmpty() }?.let {
                        viewModelScope.launch{
                            adminRepository.updateImageThumbnail(
                                productId = productId,
                                downloadUrl = "",
                                onSuccess = {
                                    updateThumbnail("")
                                    updateThumbnailUploaderState(RequestState.Idle)
                                    onSuccess()
                                },
                                onError = {message->
                                    onError(message)
                                }
                            )
                        }

                    }?: run{
                        updateThumbnail("")
                        updateThumbnailUploaderState(RequestState.Idle)
                        onSuccess()
                    }
                },
                onError = onError
            )
        }
    }

    fun deleteProduct(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        productId.takeIf { it.isNotEmpty() }?.let {id->
            viewModelScope.launch {
                adminRepository.deleteProduct(
                    productId = id,
                    onSuccess = {
                        deleteThumbnailFromStorage(
                            onSuccess = {},
                            onError = {}
                        )
                        onSuccess()
                    },
                    onError = onError
                )
            }
        }
    }
}