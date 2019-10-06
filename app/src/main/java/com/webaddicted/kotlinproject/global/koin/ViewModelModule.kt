package com.webaddicted.kotlinproject.global.koin

import com.webaddicted.kotlinproject.viewmodel.HomeViewModel
import com.webaddicted.kotlinproject.viewmodel.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Deepak Sharma on 01/07/19.
 */
val viewModelModule = module {
    viewModel { NewsViewModel(get()) }
    viewModel { HomeViewModel(get()) }

}