package com.fernandopereira.muzzchat.data.repository

import app.cash.turbine.test
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageRepositoryTest {
    private val repository: MessageRepository = mockk()

    @Test
    fun `GIVEN messages in repository WHEN messages flow collected THEN emits current list`() =
        runTest {
            // GIVEN
            val messages =
                listOf(
                    Message(id = 1, text = "hello", sender = User.ME, timestamp = 0L),
                    Message(id = 2, text = "hey", sender = User.SARAH, timestamp = 1_000L),
                )
            every { repository.messages } returns flowOf(messages)

            // WHEN / THEN
            repository.messages.test {
                assertEquals(messages, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN empty repository WHEN messages flow collected THEN emits empty list`() =
        runTest {
            // GIVEN
            every { repository.messages } returns flowOf(emptyList())

            // WHEN / THEN
            repository.messages.test {
                assertEquals(emptyList<Message>(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `GIVEN a message WHEN insert called THEN delegates to repository exactly once`() =
        runTest {
            // GIVEN
            val message = Message(id = 1, text = "hello", sender = User.ME, timestamp = 0L)
            coEvery { repository.insert(message) } returns Unit

            // WHEN
            repository.insert(message)

            // THEN
            coVerify(exactly = 1) { repository.insert(message) }
        }
}
