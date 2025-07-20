package com.manuelemr.melispacenews.ui.di

import com.manuelemr.melispacenews.ui.utils.AppResourceProvider
import com.manuelemr.melispacenews.ui.utils.ResourceProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val uiModule = module {
    single<ResourceProvider> { AppResourceProvider(appContext = androidApplication()) }
}