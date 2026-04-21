package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.Radius0
import com.fernandopereira.muzzchat.ui.theme.Radius100
import com.fernandopereira.muzzchat.ui.theme.Radius400
import com.fernandopereira.muzzchat.ui.theme.Space050
import com.fernandopereira.muzzchat.ui.theme.Space200
import com.fernandopereira.muzzchat.ui.theme.Space300

@Composable
fun MessageBubble(
    message: Message,
    isGroupedWithNext: Boolean,
    modifier: Modifier = Modifier,
) {
    val style =
        bubbleStyle(
            sender = message.sender,
            isGroupedWithNext = isGroupedWithNext,
        )

    val bottomSpacing = if (isGroupedWithNext) Space050 else Space200

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = bottomSpacing),
        horizontalArrangement = style.arrangement,
    ) {
        BubbleSurface(
            text = message.text,
            shape = style.shape,
            bubbleColor = style.bubbleColor,
            contentColor = style.contentColor,
        )
    }
}

@Composable
private fun BubbleSurface(
    text: String,
    shape: RoundedCornerShape,
    bubbleColor: Color,
    contentColor: Color,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.75f),
        shape = shape,
        color = bubbleColor,
        contentColor = contentColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = Space300, vertical = Space200),
        )
    }
}

@Composable
private fun bubbleStyle(
    sender: User,
    isGroupedWithNext: Boolean,
): BubbleStyle {
    val isMine = sender == ME
    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    return BubbleStyle(
        arrangement = if (isMine) Arrangement.End else Arrangement.Start,
        bubbleColor = bubbleColor,
        contentColor =
            if (isMine) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        shape =
            bubbleShape(
                sender = sender,
                isGroupedWithNext = isGroupedWithNext,
            ),
    )
}

private fun bubbleShape(
    sender: User,
    isGroupedWithNext: Boolean,
): RoundedCornerShape =
    when {
        !isGroupedWithNext -> {
            RoundedCornerShape(Radius400)
        }

        sender == ME -> {
            RoundedCornerShape(
                topStart = Radius400,
                topEnd = Radius400,
                bottomEnd = Radius0,
                bottomStart = Radius400,
            )
        }

        else -> {
            RoundedCornerShape(
                topStart = Radius400,
                topEnd = Radius400,
                bottomEnd = Radius400,
                bottomStart = Radius0,
            )
        }
    }

private data class BubbleStyle(
    val arrangement: Arrangement.Horizontal,
    val bubbleColor: Color,
    val contentColor: Color,
    val shape: RoundedCornerShape,
)

@ThemePreviews
@Composable
private fun MessageBubblePreview() {
    MuzzChatTheme {
        Surface {
        Column(modifier = Modifier.padding(Space300)) {
            MessageBubble(
                message =
                    Message(
                        text = "Hey, are you free later?",
                        sender = SARAH,
                        timestamp = 0L,
                    ),
                isGroupedWithNext = true,
            )

            MessageBubble(
                message =
                    Message(
                        text = "Yep, I can jump on a call after lunch.",
                        sender = ME,
                        timestamp = 0L,
                    ),
                isGroupedWithNext = false,
            )
        }
        }
    }
}
