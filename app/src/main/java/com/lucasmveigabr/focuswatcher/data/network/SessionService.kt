package com.lucasmveigabr.focuswatcher.data.network

import com.lucasmveigabr.focuswatcher.data.network.dto.PostSessionRequestDto
import com.lucasmveigabr.focuswatcher.data.network.dto.SessionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface SessionService {

    @GET("/sessions")
    suspend fun getAll(): List<SessionDto>

    @GET("/session/{uuid}")
    suspend fun get(uuid: String): SessionDto

    @POST("/sessions")
    suspend fun syncSessions(request: PostSessionRequestDto): Response<Any>

}