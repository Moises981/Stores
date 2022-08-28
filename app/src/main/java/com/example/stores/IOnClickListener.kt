package com.example.stores

interface IOnClickListener {
    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDelete(storeEntity: StoreEntity)
}