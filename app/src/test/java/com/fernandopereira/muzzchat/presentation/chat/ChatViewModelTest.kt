package com.fernandopereira.muzzchat.presentation.chat

import app.cash.turbine.test
import com.fernandopereira.muzzchat.common.MainDispatcherRule
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import com.fernandopereira.muzzchat.domain.usecase.BuildChatItemsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: MessageRepository = mockk()
    private val buildChatItemsUseCase = BuildChatItemsUseCase()

    @Test
    fun `GIVEN repository emits messages WHEN view model starts observing THEN ui state emits built chat items`() =
        runTest {
            // GIVEN
            val messagesFlow =
                MutableStateFlow(
                    listOf(
                        message(id = 1, timestamp = T0, sender = ME),
                        message(id = 2, timestamp = T0 + 5_000, sender = ME),
                    ),
                )
            every { repository.messages } returns messagesFlow
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()
            val expectedItems = buildChatItemsUseCase(messagesFlow.value)

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                advanceUntilIdle()

                assertEquals(expectedItems, awaitItem().items)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN input changed action WHEN handled THEN ui state emits updated input text`() =
        runTest {
            // GIVEN
            every { repository.messages } returns MutableStateFlow(emptyList())
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                viewModel.onAction(ChatUiAction.InputChanged("hello"))

                assertEquals(
                    ChatUiState(inputText = "hello"),
                    awaitItem(),
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN switch user action WHEN handled THEN ui state emits toggled current user`() =
        runTest {
            // GIVEN
            every { repository.messages } returns MutableStateFlow(emptyList())
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                viewModel.onAction(ChatUiAction.OnSwitchUserClicked)

                assertEquals(
                    ChatUiState(currentUser = SARAH),
                    awaitItem(),
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN blank input WHEN send action is handled THEN repository insert is not called and no new state is emitted`() =
        runTest {
            // GIVEN
            every { repository.messages } returns MutableStateFlow(emptyList())
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                viewModel.onAction(ChatUiAction.InputChanged("   "))
                assertEquals(
                    ChatUiState(inputText = "   "),
                    awaitItem(),
                )

                viewModel.onAction(ChatUiAction.OnSendMessageClicked)

                expectNoEvents()

                coVerify(exactly = 0) { repository.insert(any()) }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN non blank input WHEN send action is handled THEN input is cleared and trimmed message is inserted`() =
        runTest {
            // GIVEN
            every { repository.messages } returns MutableStateFlow(emptyList())
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                viewModel.onAction(ChatUiAction.InputChanged("  hello world  "))
                assertEquals(
                    ChatUiState(inputText = "  hello world  "),
                    awaitItem(),
                )

                viewModel.onAction(ChatUiAction.OnSendMessageClicked)

                assertEquals(
                    ChatUiState(inputText = ""),
                    awaitItem(),
                )

                advanceUntilIdle()

                coVerify(exactly = 1) {
                    repository.insert(
                        match {
                            it.text == "hello world" &&
                                it.sender == ME &&
                                it.timestamp > 0L
                        },
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN switched user and valid input WHEN send action is handled THEN inserted message uses current user`() =
        runTest {
            // GIVEN
            every { repository.messages } returns MutableStateFlow(emptyList())
            coEvery { repository.insert(any()) } returns Unit

            val viewModel = createViewModel()

            // WHEN / THEN
            viewModel.uiState.test {
                assertEquals(ChatUiState(), awaitItem())

                viewModel.onAction(ChatUiAction.OnSwitchUserClicked)
                assertEquals(
                    ChatUiState(currentUser = SARAH),
                    awaitItem(),
                )

                viewModel.onAction(ChatUiAction.InputChanged("reply from sarah"))
                assertEquals(
                    ChatUiState(
                        inputText = "reply from sarah",
                        currentUser = SARAH,
                    ),
                    awaitItem(),
                )

                viewModel.onAction(ChatUiAction.OnSendMessageClicked)
                assertEquals(
                    ChatUiState(
                        inputText = "",
                        currentUser = SARAH,
                    ),
                    awaitItem(),
                )

                advanceUntilIdle()

                coVerify(exactly = 1) {
                    repository.insert(
                        match {
                            it.text == "reply from sarah" &&
                                it.sender == SARAH
                        },
                    )
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun createViewModel(): ChatViewModel =
        ChatViewModel(
            repository = repository,
            buildChatItemsUseCase = buildChatItemsUseCase,
        )

    private companion object {
        const val T0 = 1_700_000_000_000L

        fun message(
            id: Long,
            timestamp: Long,
            sender: User = ME,
            text: String = "msg-$id",
        ) = Message(
            id = id,
            text = text,
            sender = sender,
            timestamp = timestamp,
        )
    }
}
