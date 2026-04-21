package com.fernandopereira.muzzchat.domain.usecase

import com.fernandopereira.muzzchat.common.ONE_HOUR_MS
import com.fernandopereira.muzzchat.common.TWENTY_SECONDS_MS
import com.fernandopereira.muzzchat.common.formatDay
import com.fernandopereira.muzzchat.common.formatTime
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.presentation.chat.model.ChatItem
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
    fun `GIVEN single message WHEN invoked THEN produces date header and ungrouped item`() {
        // GIVEN
        val message = message(id = 1, timestamp = T0, sender = ME)

        // WHEN
        val items = useCase(listOf(message))

        // THEN
        assertEquals(2, items.size)
        assertEquals(ChatItem.DateHeader(formatDay(T0), formatTime(T0)), items[0])
        assertEquals(
            ChatItem.MessageItem(message = message, isGroupedWithNext = false),
            items[1],
        )
    }

    @Test
    fun `GIVEN two messages WHEN gap equals one hour THEN second header is inserted`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0)
        val second = message(id = 2, timestamp = T0 + ONE_HOUR_MS)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN — [header, first, header, second]
        assertEquals(4, items.size)
        assertTrue(items[0] is ChatItem.DateHeader)
        assertTrue(items[2] is ChatItem.DateHeader)
    }

    @Test
    fun `GIVEN two messages WHEN gap is one millisecond below one hour THEN no second header`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0)
        val second = message(id = 2, timestamp = T0 + ONE_HOUR_MS - 1)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN — [header, first, second]
        assertEquals(3, items.size)
        assertTrue(items[0] is ChatItem.DateHeader)
        assertTrue(items[1] is ChatItem.MessageItem)
        assertTrue(items[2] is ChatItem.MessageItem)
    }

    @Test
    fun `GIVEN same sender WHEN gap is below twenty seconds THEN first message is grouped with next`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second = message(id = 2, timestamp = T0 + TWENTY_SECONDS_MS - 1, sender = ME)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN
        assertEquals(true, (items[1] as ChatItem.MessageItem).isGroupedWithNext)
        assertEquals(false, (items[2] as ChatItem.MessageItem).isGroupedWithNext)
    }

    @Test
    fun `GIVEN same sender WHEN gap equals twenty seconds THEN grouping is false`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second = message(id = 2, timestamp = T0 + TWENTY_SECONDS_MS, sender = ME)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN
        assertEquals(false, (items[1] as ChatItem.MessageItem).isGroupedWithNext)
    }

    @Test
    fun `GIVEN different senders WHEN gap is below twenty seconds THEN grouping is false`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second = message(id = 2, timestamp = T0 + 1_000, sender = User.SARAH)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN
        assertEquals(false, (items[1] as ChatItem.MessageItem).isGroupedWithNext)
    }

    @Test
    fun `GIVEN same sender messages WHEN last item evaluated THEN grouping is always false`() {
        // GIVEN
        val first = message(id = 1, timestamp = T0, sender = ME)
        val second = message(id = 2, timestamp = T0 + 1_000, sender = ME)

        // WHEN
        val items = useCase(listOf(first, second))

        // THEN
        assertEquals(false, (items.last() as ChatItem.MessageItem).isGroupedWithNext)
    }

    @Test
    fun `GIVEN complex conversation WHEN invoked THEN headers and grouping are placed consistently`() {
        // GIVEN — two bursts separated by a 1h+ gap
        // Burst 1: ME@T0, ME@T0+5s, SARAH@T0+10s
        // Burst 2: SARAH@T0+2h, ME@T0+2h+30s
        val messages =
            listOf(
                message(id = 1, timestamp = T0, sender = ME),
                message(id = 2, timestamp = T0 + 5_000, sender = ME),
                message(id = 3, timestamp = T0 + 10_000, sender = User.SARAH),
                message(id = 4, timestamp = T0 + 2 * ONE_HOUR_MS, sender = User.SARAH),
                message(id = 5, timestamp = T0 + 2 * ONE_HOUR_MS + 30_000, sender = ME),
            )

        // WHEN
        val items = useCase(messages)

        // THEN — [H, M1(g=true), M2(g=false: sender changes), M3(g=false: >20s to next), H, M4(g=false: >20s), M5(g=false: last)]
        assertEquals(7, items.size)
        assertTrue(items[0] is ChatItem.DateHeader)
        assertEquals(true, (items[1] as ChatItem.MessageItem).isGroupedWithNext)
        assertEquals(false, (items[2] as ChatItem.MessageItem).isGroupedWithNext)
        assertEquals(false, (items[3] as ChatItem.MessageItem).isGroupedWithNext)
        assertTrue(items[4] is ChatItem.DateHeader)
        assertEquals(false, (items[5] as ChatItem.MessageItem).isGroupedWithNext)
        assertEquals(false, (items[6] as ChatItem.MessageItem).isGroupedWithNext)
    }

    @Test
    fun `GIVEN a message WHEN invoked THEN date header label uses the shared formatter`() {
        // GIVEN
        val message = message(id = 1, timestamp = T0)

        // WHEN
        val items = useCase(listOf(message))

        // THEN
        assertEquals(ChatItem.DateHeader(formatDay(T0), formatTime(T0)), items[0])
    }

    private companion object {
        // Arbitrary fixed epoch millis — keeps tests timezone-independent via `label()`.
        const val T0 = 1_700_000_000_000L

        fun message(
            id: Long,
            timestamp: Long,
            sender: User = ME,
            text: String = "msg-$id",
        ) = Message(id = id, text = text, sender = sender, timestamp = timestamp)
    }
}
