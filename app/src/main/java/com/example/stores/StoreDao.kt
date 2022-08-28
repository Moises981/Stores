package com.example.stores

import androidx.room.*

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    suspend fun getAllStores(): MutableList<StoreEntity>

    @Query("SELECT * FROM StoreEntity WHERE id = :id")
    suspend fun getStoreById(id: Long): StoreEntity

    @Insert
    suspend fun addStore(storeEntity: StoreEntity): Long

    @Update
    suspend fun updateStore(storeEntity: StoreEntity)

    @Delete
    suspend fun deleteStore(storeEntity: StoreEntity)
}