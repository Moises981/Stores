package com.example.stores.mainModule.model

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking

class MainInteractor {

    fun findAllStores(callback: (MutableList<StoreEntity>) -> Unit) {
        var storeList = mutableListOf<StoreEntity>()
        val url = Constants.BASE_URL + Constants.STORE_PATH
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            val success = it.optBoolean(Constants.SUCCESS_PROPERTY, false)
            if (success) {
                val jsonList = it.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if (jsonList != null) {
                    val mutableList = object : TypeToken<MutableList<StoreEntity>>() {}.type
                    storeList =
                        Gson().fromJson(jsonList, mutableList)
                    callback(storeList)
                    return@JsonObjectRequest
                }
            }
            callback(storeList)
        }, {
            it.printStackTrace()
            callback(storeList)
        })
        StoreApplication.storeAPI.addToRequestQueue(request)
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
