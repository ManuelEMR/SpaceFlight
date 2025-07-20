package com.manuelemr.melispacenews.ui.search.di

import com.manuelemr.melispacenews.ui.search.SpaceFlightListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    viewModel {
        SpaceFlightListViewModel(
            spaceFlightRepository = get(),
            resourceProvider = get()
        )
    }
}