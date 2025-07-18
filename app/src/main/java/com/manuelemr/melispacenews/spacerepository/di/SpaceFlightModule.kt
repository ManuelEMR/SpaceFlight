package com.manuelemr.melispacenews.spacerepository.di

import com.manuelemr.melispacenews.spacerepository.SpaceFlightApi
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val spaceFlightModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://ipinfo.io/")
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single<SpaceFlightApi> {
        get<Retrofit>().create(SpaceFlightApi::class.java)
    }

    single {
        SpaceFlightRepository(get())
    }
}