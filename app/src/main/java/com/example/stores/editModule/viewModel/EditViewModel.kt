package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity

class EditViewModel : ViewModel() {
    private val _currentStore = MutableLiveData<StoreEntity>()
    public val currentStore: LiveData<StoreEntity> = _currentStore

    private val _interactor = EditInteractor()

    private val _fabStatus = MutableLiveData<Boolean>()
    public val fabStatus: LiveData<Boolean> = _fabStatus

    private val _result = MutableLiveData<Any>()
    public val result: LiveData<Any> = _result

    fun getCurrentStore() = _currentStore.value

    fun setCurrentStore(storeEntity: StoreEntity) {
        _currentStore.value = storeEntity
    }

    fun setFabStatus(isVisible: Boolean) {
        _fabStatus.value = isVisible
    }

    fun getFabStatus() = _fabStatus.value

    fun setResult(value: Any) {
        _result.value = value
    }

    fun saveStore(storeEntity: StoreEntity) {
        _interactor.saveStore(storeEntity) {
            _result.value = it
        }
    }

    fun updateStore(storeEntity: StoreEntity) {
        _interactor.updateStore(storeEntity) {
            _result.value = it.id
        }
    }


}