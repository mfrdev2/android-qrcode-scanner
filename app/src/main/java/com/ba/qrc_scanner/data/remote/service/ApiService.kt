package com.ba.qrc_scanner.data.remote.service

import com.ba.qrc_scanner.model.SuccessRes
import com.ba.qrc_scanner.model.TokenState
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("public/qpro-external-api/counter-assign-token")
    suspend fun changeTokenState(@Body tokenState: TokenState): SuccessRes

}