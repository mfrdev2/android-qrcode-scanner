package com.ba.qrc_scanner.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ba.qrc_scanner.base.BaseViewModel
import com.ba.qrc_scanner.data.remote.repo.ApiServiceRepo
import com.ba.qrc_scanner.model.ScanResult
import com.ba.qrc_scanner.model.SuccessRes
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.decodeBase64
import com.ba.qrc_scanner.utils.exceptions.DecodeException
import com.ba.qrc_scanner.utils.isNetworkConnected
import com.ba.qrc_scanner.utils.jsonStringToDataBean
import com.ba.qrc_scanner.utils.remote.Resource
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate


class MainViewModel(application: Application) : BaseViewModel(application) {
    val apiRep: ApiServiceRepo by lazy { ApiServiceRepo() }

    private val _scanResult = MutableLiveData<String?>();
    val scanResultData: LiveData<String?> = _scanResult
    private val _scanResultBean = MutableLiveData<ScanResult?>();
    val scanResultBean: LiveData<ScanResult?> = _scanResultBean

    private val _errorMessage = MutableLiveData<String?>();
    val errorMsg: LiveData<String?> = _errorMessage


    private val _tokenState = MutableLiveData<Resource<SuccessRes>>()
    val tokenStateResult: LiveData<Resource<SuccessRes>> = _tokenState

    private val _isEnableApproveBtn = MutableLiveData<Boolean>();
    val isEnableApproveBtn: LiveData<Boolean> = _isEnableApproveBtn


    fun initScanResult(result: String?) {
        _scanResult.value = result
        _scanResultBean.value = getScanResult()
    }

    fun getScanResult(): ScanResult? {
        return try {
            val parseScanResult = parseScanResult()
            _errorMessage.value = null
            _isEnableApproveBtn.value = true
            parseScanResult
        } catch (e: Exception) {
            _isEnableApproveBtn.value = false
            _errorMessage.value = e.message
            return null;
        }
    }

    fun parseScanResult(): ScanResult? {
        val value = scanResultData.value;
        if (scanResultData.value == null) {
            throw DecodeException("Invalid data!!")
        }
        //{"name":"Sahad","serviceName":"Business Meeting","token":"025432","transId":"198","imageUrl":"http://image-url","date":"2025-07-15","time":"10:40:05 AM"}
        val split = value?.split(",")
        if (split?.size != 2) {
            throw DecodeException("Invalid data!!")
        }
        val base64Str = split[1]
        return try {
            val jsonStringToDataBean = jsonStringToDataBean<ScanResult?>(decodeBase64(base64Str))
            val date = jsonStringToDataBean?.date
            if (date == null) {
                throw DecodeException("Invalid date!!")
            }
            try {
                val date = LocalDate.parse(date)
                val now = LocalDate.now()
                if (date.isEqual(now)) {
                    return jsonStringToDataBean
                }
                if (date.isAfter(now)) {
                    throw DecodeException("Date is in the future!!")
                }
                if (date.isBefore(now)) {
                    throw DecodeException("Token has been expired!!")
                }
            } catch (e: Exception) {
                throw DecodeException("Invalid date format!!")
            }
            return jsonStringToDataBean
        } catch (e: Exception) {
            throw DecodeException("Formatted data is not valid.")
        }
    }


    fun changeTokenState() {
        val parseScanResult = parseScanResult()
        if (parseScanResult == null) {
            _tokenState.value = Resource.error("Formatted data is not valid.");
            return;
        }
        if (parseScanResult.transId == null) {
            _tokenState.value = Resource.error("Formatted data is not valid.");
            return;
        }
        val tokenState = TokenState(parseScanResult.transId, "0")
        if (!isNetworkConnected(getApplication())) {
            _tokenState.value = Resource.error("No internet connection");
            return;
        }
        _tokenState.value = Resource.loading()
        viewModelScope.launch {
            val result = apiRep.changeTokenState(tokenState)
            _tokenState.value = result
        }
    }

}