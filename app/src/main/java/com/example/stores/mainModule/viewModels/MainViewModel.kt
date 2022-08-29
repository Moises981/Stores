package com.example.stores.mainModule.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor

class MainViewModel : ViewModel() {
    private val _interactor: MainInteractor = MainInteractor()
    private lateinit var _storeList: MutableList<StoreEntity>
    private val _stores: MutableLiveData<List<StoreEntity>> = MutableLiveData<List<StoreEntity>>()
    private val _progressBarStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    public val progressBarStatus: LiveData<Boolean> = _progressBarStatus

    init {
        loadStores()
    }

    val stores: LiveData<List<StoreEntity>> = _stores

    private fun loadStores() {

        _interactor.findAllStores {
            _progressBarStatus.value = false
            _stores.value = it
            _storeList = it
        }
    }

    private fun setProgressBarStatus(visible: Boolean) {
        _progressBarStatus.value = visible
    }

    fun updateStore(storeEntity: StoreEntity) {
        _interactor.updateStore(storeEntity) {
            val index = _storeList.indexOf(storeEntity)
            if (index != -1) {
                _storeList[index] = storeEntity
                _stores.value = _storeList
            }
        }
    }

    fun deleteStore(storeEntity: StoreEntity) {
        _interactor.deleteStore(storeEntity) {
            val index = _storeList.indexOf(storeEntity)
            if (index != -1) {
                _storeList.removeAt(index)
                _stores.value = _storeList
            }
        }
    }

}