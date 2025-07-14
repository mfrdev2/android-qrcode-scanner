package com.ba.qrc_scanner.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ba.qrc_scanner.base.BaseViewModel
import com.ba.qrc_scanner.data.remote.repo.ApiServiceRepo
import com.ba.qrc_scanner.model.ScanResult
import com.ba.qrc_scanner.model.SuccessRes
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.isNetworkConnected
import com.ba.qrc_scanner.utils.remote.Resource
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate


class MainViewModel(application: Application) : BaseViewModel(application) {
    val apiRep: ApiServiceRepo by lazy { ApiServiceRepo() }

    private val _scanResult = MutableLiveData<String?>();
    val scanResult: LiveData<String?> = _scanResult

    private val _errorMessage = MutableLiveData<String?>();
    val errorMsg: LiveData<String?> = _errorMessage


    private val _tokenState = MutableLiveData<Resource<SuccessRes>>()
    val tokenStateResult: LiveData<Resource<SuccessRes>> = _tokenState

    private val _isEnableApproveBtn = MutableLiveData<Boolean>();
    val isEnableApproveBtn: LiveData<Boolean> = _isEnableApproveBtn


    fun initScanResult(result: String?) {
        _scanResult.value = result
        driveApproveBtn()
    }

    fun driveApproveBtn() {
        val parseScanResult = parseScanResult()
        if (parseScanResult == null) {
            _isEnableApproveBtn.value = false
            _errorMessage.value = "Formatted data is not valid."
            return
        }
        _errorMessage.value = null
        _isEnableApproveBtn.value = true
    }

    fun parseScanResult(): ScanResult? {
        val value = scanResult.value ?: return null
        // Example scanned value:
        //Token: 025432
        //Transaction ID: 198
       // Date: 2024-07-14
        //Time: 10:40:05 AM

        val lines = value.lines().map { it.trim() }
        val tokenLine = lines.find { it.startsWith("Token:") }
        val transactionIdLine = lines.find { it.startsWith("Transaction ID:") }
        val dateLine = lines.find { it.startsWith("Date:") }

        if (transactionIdLine == null || dateLine == null) {
            Log.e("MainViewModel", "Invalid scan result")
            return null // missing required data
        }

        val tokenNumber = tokenLine?.removePrefix("Token:")?.trim()
        val transactionId = transactionIdLine.removePrefix("Transaction ID:").trim()
        val dateStr = dateLine.removePrefix("Date:").trim()

        val date = try {
            LocalDate.parse(dateStr) // format must be YYYY-MM-DD
        } catch (e: Exception) {
            Log.e("MainViewModel", "Invalid date format: $dateStr")
            return null // parsing failed
        }
        val now = LocalDate.now()
        if (!date.isEqual(now)) {
            Log.e("MainViewModel", "Date is not today")
            return null;
        }
        return ScanResult(
            visitorCode = tokenNumber,
            transactionId = transactionId,
            date = date
        )
    }


    fun changeTokenState() {
        val parseScanResult = parseScanResult()
        if (parseScanResult == null) {
            _tokenState.value = Resource.error("Formatted data is not valid.");
            return;
        }
        val tokenState = TokenState(parseScanResult.transactionId, "0")
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