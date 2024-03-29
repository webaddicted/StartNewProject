package com.webaddicted.kotlinproject.global.koin

import com.webaddicted.kotlinproject.model.repository.NewsRepository
import org.koin.dsl.module
/**
 * Created by Deepak Sharma on 01/07/19.
 */
val repoModule = module {

    single { NewsRepository(get()) }

}