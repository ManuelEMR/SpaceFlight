package com.manuelemr.melispacenews.ui.utils

import android.util.Log

interface PagingHandlerDelegate<T> {
    fun requestNextPage(page: Int, onResult: (List<T>) -> Unit)
    fun onItemsUpdated(items: List<T>)
    fun onNoMoreItems(hasItems: Boolean)
    fun onInvalidate()
}

class PagingHandler<T>(private var delegate: PagingHandlerDelegate<T>) {
    var page = 0
        private set

    private var isLoading = false
    private var hasMoreItems = true

    var items: List<T> = emptyList()
        private set

    fun fetchNextPage() {
        if (isLoading) return
        if (!hasMoreItems) {
            delegate.onNoMoreItems(hasMoreItems)
            return
        }

        isLoading = true
        page += 1

        delegate.requestNextPage(page) { newItems ->
            isLoading = false

            if (newItems.isEmpty()) {
                hasMoreItems = false
                delegate.onNoMoreItems(hasMoreItems)
                Log.d("Pager", "No more items ${newItems.count()}")
            }

            setItems(items + newItems)
        }
    }

    fun invalidate() {
        hasMoreItems = true
        isLoading = false
        page = 0
        items = emptyList()

        delegate.onInvalidate()
        fetchNextPage()
    }

    private fun setItems(newItems: List<T>) {
        items = newItems
        delegate.onItemsUpdated(newItems)
    }
}