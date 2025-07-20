package com.manuelemr.melispacenews.ui.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg args: Any): String
    fun getColor(@ColorRes resId: Int): Int
}

class AppResourceProvider(private val appContext: Context): ResourceProvider {
    override fun getString(resId: Int): String = appContext.getString(resId)
    override fun getString(resId: Int, vararg args: Any): String = appContext.getString(resId, *args)
    override fun getColor(resId: Int): Int = ContextCompat.getColor(appContext, resId)
}

class TestResourceProvider: ResourceProvider {
    override fun getString(resId: Int): String = "Test"
    override fun getString(resId: Int, vararg args: Any): String = "Test"
    override fun getColor(resId: Int): Int = 0
}