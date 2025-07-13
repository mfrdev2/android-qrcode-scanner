package com.ba.qrc_scanner.data.remote.repo

import com.ba.qrc_scanner.data.remote.RetrofitClient
import com.ba.qrc_scanner.data.remote.service.ApiService
import com.ba.qrc_scanner.model.SuccessRes
import com.ba.qrc_scanner.model.TokenState
import com.ba.qrc_scanner.utils.remote.Resource
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class ApiServiceRepo {
   private val apiService: ApiService by lazy {
        RetrofitClient.createService<ApiService>()
    }
    // Simulate network delay
    private suspend fun delay() {
        kotlinx.coroutines.delay(2000)
    }

    suspend fun changeTokenState(tokenState: TokenState): Resource<SuccessRes> {
        return try {
            val response = apiService.changeTokenState(tokenState)
            Resource.success(response)
        }  catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody)
            Resource.error("HTTP ${e.code()}: $errorMessage")
        } catch (e: IOException) {
            Resource.error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.error("Unexpected error: ${e.localizedMessage}")
        }
    }

    fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody == null) return "No error details"
            val jsonObject = JSONObject(errorBody)
            jsonObject.optString("message", "Unknown error")
        } catch (e: JSONException) {
            "Malformed error response"
        }
    }


}