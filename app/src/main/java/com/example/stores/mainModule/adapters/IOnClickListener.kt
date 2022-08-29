package com.example.stores.mainModule.adapters

import com.example.stores.common.entities.StoreEntity

interface IOnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDelete(storeEntity: StoreEntity)
}