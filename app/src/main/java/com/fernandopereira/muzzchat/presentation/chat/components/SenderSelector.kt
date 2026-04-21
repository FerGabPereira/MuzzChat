package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.presentation.chat.extensions.displayName
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.Dimen16
import com.fernandopereira.muzzchat.ui.theme.Dimen2
import com.fernandopereira.muzzchat.ui.theme.Dimen4
import com.fernandopereira.muzzchat.ui.theme.Dimen8
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.RadiusFull

private const val ANIMATION_DURATION_MS = 200

@Composable
fun SenderSelector(
    currentUser: User,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(RadiusFull),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(Dimen2),
            horizontalArrangement = Arrangement.spacedBy(Dimen2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            User.entries.forEach { user ->
                SenderTab(
                    label = user.displayName(),
                    isSelected = user == currentUser,
                    onClick = { onUserSelected(user) },
                )
            }
        }
    }
}

@Composable
private fun SenderTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "tabBackground",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "tabContent",
    )

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = contentColor,
        modifier =
            Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(RadiusFull),
                )
                .clickable(onClick = onClick)
                .padding(horizontal = Dimen16, vertical = Dimen4),
    )
}

@ThemePreviews
@Composable
private fun SenderSelectorPreview() {
    MuzzChatTheme {
        SenderSelector(
            currentUser = ME,
            onUserSelected = {},
            modifier = Modifier.padding(Dimen8),
        )
    }
}
