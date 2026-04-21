package com.fernandopereira.muzzchat.domain.usecase

import com.fernandopereira.muzzchat.common.ONE_HOUR_MS
import com.fernandopereira.muzzchat.common.TWENTY_SECONDS_MS
import com.fernandopereira.muzzchat.common.dateHeaderFormatter
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.presentation.chat.model.ChatItem
import java.time.Instant

/**
 * Transforms a list of [Message]s into the flat list of [ChatItem]s the UI renders.
 *
 * Pure Kotlin, zero Android dependencies — safe to unit test on the JVM.
 *
 * ### Rules
 * - A [ChatItem.DateHeader] is inserted before the first message and before any message
 *   whose gap with the previous one is `>= 1 hour`.
 * - [ChatItem.MessageItem.isGroupedWithNext] is a **look-ahead** flag: `true` when the next
 *   message exists, has the same sender, and is `< 20 seconds` away.
 *
 * ### Precondition
 * `messages` MUST be sorted ascending by [Message.timestamp]. Callers are responsible for
 * ordering; this use case does not re-sort to keep it O(n) and predictable.
 */
class BuildChatItemsUseCase {
    operator fun invoke(messages: List<Message>): List<ChatItem> =
        buildList(messages.size * 2) {
            messages.forEachIndexed { index, message ->
                val previous = messages.getOrNull(index - 1)
                val next = messages.getOrNull(index + 1)

                if (needsDateHeader(message, previous)) {
                    add(ChatItem.DateHeader(formatLabel(message.timestamp)))
                }

                add(
                    ChatItem.MessageItem(
                        message = message,
                        isGroupedWithNext = isGroupedWithNext(message, next),
                    ),
                )
            }
        }

    private fun needsDateHeader(message: Message, previous: Message?): Boolean =
        previous == null || message.timestamp - previous.timestamp >= ONE_HOUR_MS

    private fun isGroupedWithNext(message: Message, next: Message?): Boolean =
        next != null &&
            next.sender == message.sender &&
            next.timestamp - message.timestamp < TWENTY_SECONDS_MS

    private fun formatLabel(timestamp: Long): String =
        dateHeaderFormatter.format(Instant.ofEpochMilli(timestamp))
}
