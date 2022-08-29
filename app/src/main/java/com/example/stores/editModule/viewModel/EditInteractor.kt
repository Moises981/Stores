package com.example.stores.editModule.viewModel

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import kotlinx.coroutines.runBlocking

class EditInteractor {
    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit) {
        runBlocking {
            callback(StoreApplication.database.storeDao().addStore(storeEntity))
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        runBlocking {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            callback(storeEntity)
        }
    }
}