package com.nutrisport.category_search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisport.data.domain.ProductRepository
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.util.RequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class CategorySearchViewModel(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val categoryName = savedStateHandle.get<String>("categoryName") ?: ProductCategory.Protein.name
    private val products = productRepository.readProductsByCategoryFlow(
        ProductCategory.valueOf(categoryName)
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RequestState.Loading
    )

    private val _searchQuery = MutableStateFlow("")
    var searchQuery = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filterProduct = _searchQuery
        .debounce(500)
        .flatMapLatest {query->
            if (query.isBlank()) products
            else{
                if (products.value.isSuccess()){
                    flowOf(
                        RequestState.Success(
                            products.value.getSuccessData().filter {
                                it.title.lowercase().contains(query.lowercase())
                            }
                        )
                    )
                }else products
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    fun updateSearchQuery(query:String){
        _searchQuery.value = query
    }
}