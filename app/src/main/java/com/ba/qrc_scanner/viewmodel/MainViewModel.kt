package com.ba.qrc_scanner.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ba.qrc_scanner.base.BaseViewModel
import com.ba.qrc_scanner.data.remote.repo.ApiRepo
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.remote.Resource
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {
    val apiRep: ApiRepo by lazy { ApiRepo() }

    private val _tokenState = MutableLiveData<Resource<TokenState>>()
    val tokenStateResult: LiveData<Resource<TokenState>> = _tokenState


    fun changeTokenState(review: TokenState) {
        _tokenState.value = Resource.loading()
        viewModelScope.launch {
            val result = apiRep.changeTokenState(review)
            _tokenState.value = result
        }
    }

}