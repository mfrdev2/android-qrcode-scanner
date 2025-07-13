package com.ba.qrc_scanner.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ba.qrc_scanner.base.BaseViewModel
import com.ba.qrc_scanner.data.remote.repo.ApiServiceRepo
import com.ba.qrc_scanner.model.SuccessRes
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.isNetworkConnected
import com.ba.qrc_scanner.utils.remote.Resource
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {
    val apiRep: ApiServiceRepo by lazy { ApiServiceRepo() }

    private val _tokenState = MutableLiveData<Resource<SuccessRes>>()
    val tokenStateResult: LiveData<Resource<SuccessRes>> = _tokenState


    fun changeTokenState(review: TokenState) {
        if (!isNetworkConnected(getApplication())) {
            _tokenState.value = Resource.error("No internet connection");
            return;
        }
        _tokenState.value = Resource.loading()
        viewModelScope.launch {
            val result = apiRep.changeTokenState(review)
            _tokenState.value = result
        }
    }

}