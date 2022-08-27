package com.example.stores

interface IOnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDelete(storeEntity: StoreEntity)
}