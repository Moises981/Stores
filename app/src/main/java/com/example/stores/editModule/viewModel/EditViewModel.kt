package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditViewModel : ViewModel() {

    private val _interactor = EditInteractor()
    private var storeEntityId: Long = 0L

    private val _fabStatus = MutableLiveData<Boolean>()
    val fabStatus: LiveData<Boolean> = _fabStatus

    private val _typeError = MutableLiveData<TypeError>()
    val typeError: LiveData<TypeError> = _typeError

    private val _result = MutableLiveData<Any>()
    val result: LiveData<Any> = _result

    fun getCurrentStore(): LiveData<StoreEntity> {
        return _interactor.getUserById(storeEntityId)
    }

    fun setCurrentStoreById(id: Long) {
        storeEntityId = id
    }

    fun setFabStatus(isVisible: Boolean) {
        _fabStatus.value = isVisible
    }

    fun setResult(value: Any) {
        _result.value = value
    }

    fun clearError() {
        _typeError.value = TypeError.NONE
    }

    fun saveStore(storeEntity: StoreEntity) {
        executeAction(storeEntity) {
            _interactor.saveStore(storeEntity)
        }
    }

    fun updateStore(storeEntity: StoreEntity) {
        executeAction(storeEntity) {
            _interactor.updateStore(storeEntity)
        }
    }

    private fun executeAction(storeEntity: StoreEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
                _result.value = storeEntity
            } catch (e: StoresException) {
                _typeError.value = e.typeError
            }
        }
    }


}