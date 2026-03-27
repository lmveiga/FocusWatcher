package com.lucasmveigabr.focuswatcher.data.repository

import com.lucasmveigabr.focuswatcher.data.database.dao.SessionDao
import com.lucasmveigabr.focuswatcher.data.network.SessionService
import com.lucasmveigabr.focuswatcher.data.network.dto.SessionDto
import com.lucasmveigabr.focuswatcher.domain.model.Session
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRepositoryImplTest {

    private val testScope = TestScope(StandardTestDispatcher())

    private val sessionDao: SessionDao = mockk(relaxed = true)
    private val sessionService: SessionService = mockk(relaxed = true)

    private lateinit var repository: SessionRepositoryImpl

    @Before
    fun setUp() {
        repository = SessionRepositoryImpl(sessionDao, sessionService, testScope)
    }

    @Test
    fun `refreshSessions fetches from API and inserts into DAO`() = testScope.runTest {
        val mockDto = listOf(SessionDto(uuid = "1", recordTimestamp = 1000L))
        coEvery { sessionService.getAll() } returns mockDto

        repository.refreshSessions()
        runCurrent()

        coVerify {
            sessionDao.insertAll(match {
                val item = it.firstOrNull()
                item != null && item.recordTimestamp == 1000L && item.id == "1"
            })
        }
    }

    @Test
    fun `insertSession saves to DAO immediately and syncs to API in background`() =
        testScope.runTest {
            val session = Session(recordTimestamp = 2000L)
            repository.insertSession(session)

            coVerify { sessionDao.insertSession(any()) }
            runCurrent()
            coVerify { sessionService.syncSessions(any()) }
        }
}