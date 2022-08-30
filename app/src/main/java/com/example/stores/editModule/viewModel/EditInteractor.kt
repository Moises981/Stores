package com.example.stores.editModule.viewModel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditInteractor {

    fun getUserById(id: Long): LiveData<StoreEntity> {
        return StoreApplication.database.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO) {
        try {
            StoreApplication.database.storeDao().addStore(storeEntity)
        } catch (e: SQLiteConstraintException) {
            throw StoresException(TypeError.INSERT)
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO) {
        try {
            StoreApplication.database.storeDao().updateStore(storeEntity)
        } catch (e: SQLiteConstraintException) {
            throw StoresException(TypeError.UPDATE)
        }
    }
}