package com.fernandopereira.muzzchat.data.repository

import app.cash.turbine.test
import com.fernandopereira.muzzchat.data.local.DatabaseSeeder
import com.fernandopereira.muzzchat.data.local.MessageDao
import com.fernandopereira.muzzchat.data.local.MessageEntity
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageRepositoryTest {
    private val dao: MessageDao = mockk()
    private val seeder: DatabaseSeeder = mockk()

    @Test
    fun `GIVEN dao emits entities WHEN messages flow is collected THEN entities are mapped to domain messages`() =
        runTest {
            // GIVEN
            val entities =
                listOf(
                    MessageEntity(
                        id = 1,
                        text = "hello",
                        senderId = User.ME.id,
                        timestamp = 1_000L,
                    ),
                    MessageEntity(
                        id = 2,
                        text = "hi there",
                        senderId = User.SARAH.id,
                        timestamp = 2_000L,
                    ),
                )
            every { dao.observeAll() } returns flowOf(entities)

            val repository = MessageRepositoryImpl(dao = dao, seeder = seeder)

            // WHEN / THEN
            repository.messages.test {
                assertEquals(
                    listOf(
                        Message(
                            id = 1,
                            text = "hello",
                            sender = User.ME,
                            timestamp = 1_000L,
                        ),
                        Message(
                            id = 2,
                            text = "hi there",
                            sender = User.SARAH,
                            timestamp = 2_000L,
                        ),
                    ),
                    awaitItem(),
                )
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN dao emits empty list WHEN messages flow is collected THEN repository emits empty list`() =
        runTest {
            // GIVEN
            every { dao.observeAll() } returns flowOf(emptyList())

            val repository = MessageRepositoryImpl(dao = dao, seeder = seeder)

            // WHEN / THEN
            repository.messages.test {
                assertEquals(emptyList<Message>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN a repository WHEN deleteAll is called THEN delegates to dao deleteAll`() =
        runTest {
            // GIVEN
            every { dao.observeAll() } returns flowOf(emptyList())
            coEvery { dao.deleteAll() } returns Unit

            val repository = MessageRepositoryImpl(dao = dao, seeder = seeder)

            // WHEN
            repository.deleteAll()

            // THEN
            coVerify(exactly = 1) { dao.deleteAll() }
        }

    @Test
    fun `GIVEN a repository WHEN seedIfNeeded is called THEN delegates to seeder seedIfEmpty`() =
        runTest {
            // GIVEN
            coJustRun { seeder.seedIfEmpty() }

            val repository = MessageRepositoryImpl(dao = dao, seeder = seeder)

            // WHEN
            repository.seedIfNeeded()

            // THEN
            coVerify(exactly = 1) { seeder.seedIfEmpty() }
        }

    @Test
    fun `GIVEN a domain message WHEN insert is called THEN repository maps it and delegates to dao`() =
        runTest {
            // GIVEN
            val message =
                Message(
                    id = 7,
                    text = "ping",
                    sender = User.SARAH,
                    timestamp = 9_999L,
                )
            every { dao.observeAll() } returns flowOf(emptyList())
            coEvery { dao.insert(any()) } returns Unit

            val repository = MessageRepositoryImpl(dao = dao, seeder = seeder)

            // WHEN
            repository.insert(message)

            // THEN
            coVerify(exactly = 1) {
                dao.insert(
                    MessageEntity(
                        id = 7,
                        text = "ping",
                        senderId = User.SARAH.id,
                        timestamp = 9_999L,
                    ),
                )
            }
        }
}
