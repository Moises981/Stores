package com.example.stores.mainModule.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val _interactor: MainInteractor = MainInteractor()
    private val _progressBarStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val progressBarStatus: LiveData<Boolean> = _progressBarStatus
    private val _stores = _interactor.stores
    val stores: LiveData<MutableList<StoreEntity>> = _stores

    private val _typeError = MutableLiveData<TypeError>()
    val typeError: LiveData<TypeError> = _typeError

    fun setProgressBarStatus(visible: Boolean) {
        _progressBarStatus.value = visible
    }

    fun updateStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite != storeEntity.isFavorite
        executeAction { _interactor.updateStore(storeEntity) }
    }

    fun deleteStore(storeEntity: StoreEntity) {
        executeAction { _interactor.deleteStore(storeEntity) }
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            setProgressBarStatus(true)
            try {
                block()
            } catch (e: StoresException) {
                _typeError.value = e.typeError
            } finally {
                setProgressBarStatus(false)
            }
        }
    }
}