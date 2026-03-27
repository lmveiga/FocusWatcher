package com.lucasmveigabr.focuswatcher.data.network

import com.lucasmveigabr.focuswatcher.data.network.dto.PostSessionRequestDto
import com.lucasmveigabr.focuswatcher.data.network.dto.SessionDto
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response

class SessionServiceMockImpl : SessionService {

    val getAllResponse = listOf(
        SessionDto("uuid-api-test-1", 1774270810000L, 3, 5, 100000),
        SessionDto("uuid-api-test-2", 1774288810000L, 4, 2, 10000),
        SessionDto("uuid-api-test-3", 1774292410000L, 1, 1, 140000),
        SessionDto("uuid-api-test-4", 1774293510000L, 0, 0, 166600),
    )

    /**
     * This class is built to mimic a RESTful API behavior
     * It implements the interface that Retrofit would give a real
     * implementation in a real server
     */

    override suspend fun getAll(): List<SessionDto> {
        delay(1000L)
        return getAllResponse
    }

    override suspend fun get(uuid: String): SessionDto =
        getAllResponse.firstOrNull { it.uuid == uuid }
            ?: throw HttpException(Response.error<SessionDto>(404, null))

    override suspend fun syncSessions(request: PostSessionRequestDto): Response<Any> {
        delay(1000L)
        return Response.success(201, null as Any?)
    }
}