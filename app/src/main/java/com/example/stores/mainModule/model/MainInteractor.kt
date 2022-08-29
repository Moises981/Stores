package com.example.stores.mainModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import kotlinx.coroutines.runBlocking

class MainInteractor {

    fun findAllStores(callback: (storeList: MutableList<StoreEntity>) -> Unit) {
        runBlocking {
            val storeList = StoreApplication.database.storeDao().getAllStores()
            callback(storeList)
        }
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        runBlocking {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            callback(storeEntity)
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        runBlocking {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            callback(storeEntity)
        }
    }
}
