package com.fernandopereira.muzzchat.domain.usecase

import com.fernandopereira.muzzchat.common.ONE_HOUR_MS
import com.fernandopereira.muzzchat.common.TWENTY_SECONDS_MS
import com.fernandopereira.muzzchat.common.formatDay
import com.fernandopereira.muzzchat.common.formatTime
import com.fernandopereira.muzzchat.domain.model.ChatItem
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildChatItemsUseCaseTest {
    private val useCase = BuildChatItemsUseCase()

    @Test
    fun `GIVEN empty list WHEN invoked THEN returns empty output`() {
        // GIVEN
        val messages = emptyList<Message>()

        // WHEN
        val result = useCase(messages)

        // THEN
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN single message WHEN invoked THEN returns one header followed by one ungrouped message item`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)

        // WHEN
        val result = useCase(listOf(first))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN two messages WHEN gap is below one hour THEN inserts only the first header`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + ONE_HOUR_MS - 1,
                sender = SARAH,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN two messages WHEN gap equals one hour THEN inserts a second header before the second message`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + ONE_HOUR_MS,
                sender = SARAH,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
                header(T0 + ONE_HOUR_MS),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN a message WHEN invoked THEN header uses the shared day and time formatters`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0)

        // WHEN
        val result = useCase(listOf(first))

        // THEN
        val header = result.first() as ChatItem.DateHeader

        assertEquals(formatDay(T0), header.day)
        assertEquals(formatTime(T0), header.time)
    }

    @Test
    fun `GIVEN same sender WHEN gap is below twenty seconds THEN first message is grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + TWENTY_SECONDS_MS - 1,
                sender = ME,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = true,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN same sender WHEN gap equals twenty seconds THEN first message is not grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + TWENTY_SECONDS_MS,
                sender = ME,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN same sender WHEN gap is above twenty seconds THEN first message is not grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + TWENTY_SECONDS_MS + 1,
                sender = ME,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN different senders WHEN gap is below twenty seconds THEN first message is not grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + 1_000,
                sender = SARAH,
            )

        // WHEN
        val result = useCase(listOf(first, second))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN multiple messages WHEN evaluating the last one THEN it is never grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + 1_000,
                sender = ME,
            )
        val third =
            message(
                id = 3,
                timestamp = T0 + 2_000,
                sender = ME,
            )

        // WHEN
        val result = useCase(listOf(first, second, third))

        // THEN
        val lastItem = result.last() as ChatItem.MessageItem

        assertEquals(false, lastItem.isGroupedWithNext)
        assertEquals(third, lastItem.message)
    }

    @Test
    fun `GIVEN three same sender messages in a burst WHEN invoked THEN all but the last are grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second =
            message(
                id = 2,
                timestamp = T0 + 5_000,
                sender = ME,
            )
        val third =
            message(
                id = 3,
                timestamp = T0 + 10_000,
                sender = ME,
            )

        // WHEN
        val result = useCase(listOf(first, second, third))

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = first,
                    isGroupedWithNext = true,
                ),
                ChatItem.MessageItem(
                    message = second,
                    isGroupedWithNext = true,
                ),
                ChatItem.MessageItem(
                    message = third,
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN complex conversation WHEN invoked THEN headers and grouping remain consistent across bursts`() {
        // GIVEN
        val messages =
            listOf(
                message(id = 1, timestamp = T0, sender = ME),
                message(id = 2, timestamp = T0 + 5_000, sender = ME),
                message(id = 3, timestamp = T0 + 10_000, sender = SARAH),
                message(id = 4, timestamp = T0 + 2 * ONE_HOUR_MS, sender = SARAH),
                message(id = 5, timestamp = T0 + 2 * ONE_HOUR_MS + 30_000, sender = ME),
            )

        // WHEN
        val result = useCase(messages)

        // THEN
        val expected =
            listOf(
                header(T0),
                ChatItem.MessageItem(
                    message = messages[0],
                    isGroupedWithNext = true,
                ),
                ChatItem.MessageItem(
                    message = messages[1],
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = messages[2],
                    isGroupedWithNext = false,
                ),
                header(T0 + 2 * ONE_HOUR_MS),
                ChatItem.MessageItem(
                    message = messages[3],
                    isGroupedWithNext = false,
                ),
                ChatItem.MessageItem(
                    message = messages[4],
                    isGroupedWithNext = false,
                ),
            )

        assertEquals(expected, result)
    }

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

        fun header(timestamp: Long) =
            ChatItem.DateHeader(
                day = formatDay(timestamp),
                time = formatTime(timestamp),
                timestamp = timestamp,
            )
    }
}
