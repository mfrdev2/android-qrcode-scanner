package com.ba.qrc_scanner.data.remote.repo

import com.ba.qrc_scanner.data.remote.RetrofitClient
import com.ba.qrc_scanner.data.remote.service.ApiService
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.remote.Resource

class ApiRepo {
   private val apiService: ApiService by lazy {
        RetrofitClient.createService<ApiService>()
    }
    // Simulate network delay
    private suspend fun delay() = kotlinx.coroutines.delay(2000)

    suspend fun changeTokenState(tokenState: TokenState): Resource<TokenState> {
        return try {
            val response = apiService.changeTokenState(tokenState)
            Resource.success(response)
        } catch (e: Exception) {
            Resource.error(e.message ?: "Unknown error occurred")
        }
    }

}