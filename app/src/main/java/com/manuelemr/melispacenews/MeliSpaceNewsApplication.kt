package com.manuelemr.melispacenews

import android.app.Application
import com.manuelemr.melispacenews.spacerepository.di.spaceFlightModule
import com.manuelemr.melispacenews.ui.di.uiModule
import com.manuelemr.melispacenews.ui.search.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MeliSpaceNewsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MeliSpaceNewsApplication)
            modules(spaceFlightModule, searchModule, uiModule)
        }
    }
}