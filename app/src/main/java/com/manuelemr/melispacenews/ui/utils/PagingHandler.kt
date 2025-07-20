package com.manuelemr.melispacenews.ui.utils

import android.util.Log

interface PagingHandlerDelegate<T> {
    fun requestNextPage(page: Int, onResult: (List<T>) -> Unit)
    fun onItemsUpdated(items: List<T>)
    fun onNoMoreItems(hasItems: Boolean)
    fun onInvalidate()
}

class PagingHandler<T>(
    private var delegate: PagingHandlerDelegate<T>,
    private val initialPage: Int = 1,
) {
    var page: Int
        private set

    private var isLoading = false
    private var hasMoreItems = true

    var items: List<T> = emptyList()
        private set

    init {
        page = initialPage - 1
        println("Page $page")
    }

    fun fetchNextPage() {
        if (isLoading) return
        if (!hasMoreItems) {
            delegate.onNoMoreItems(false)
            return
        }

        isLoading = true
        page += 1

        delegate.requestNextPage(page) { newItems ->
            isLoading = false

            if (newItems.isEmpty()) {
                hasMoreItems = false
                delegate.onNoMoreItems(false)
                Log.d("Pager", "No more items ${newItems.count()}")
            }

            setItems(items + newItems)
        }
    }

    fun invalidate() {
        hasMoreItems = true
        isLoading = false
        page = initialPage - 1
        items = emptyList()

        delegate.onInvalidate()
        fetchNextPage()
    }

    private fun setItems(newItems: List<T>) {
        items = newItems
        delegate.onItemsUpdated(newItems)
    }
}