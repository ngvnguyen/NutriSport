package com.nutrisport.di

import com.nutrisport.manage_product.PhotoPicker
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule: Module = module {
    single<PhotoPicker> {PhotoPicker()}
}