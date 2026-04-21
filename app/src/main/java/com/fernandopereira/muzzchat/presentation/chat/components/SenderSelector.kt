package com.fernandopereira.muzzchat.presentation.chat.components

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
import androidx.compose.ui.Modifier
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.presentation.chat.extensions.displayName
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.RadiusInfinite
import com.fernandopereira.muzzchat.ui.theme.Space050
import com.fernandopereira.muzzchat.ui.theme.Space100
import com.fernandopereira.muzzchat.ui.theme.Space200
import com.fernandopereira.muzzchat.ui.theme.Space300

@Composable
fun SenderSelector(
    currentUser: User,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(RadiusInfinite),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(Space050),
            horizontalArrangement = Arrangement.spacedBy(Space050),
        ) {
            User.entries.forEach { user ->
                val isSelected = user == currentUser

                Text(
                    text = user.displayName(),
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isSelected) 1f else 0f),
                                shape = RoundedCornerShape(RadiusInfinite),
                            )
                            .clickable { onUserSelected(user) }
                            .padding(horizontal = Space300, vertical = Space100),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SenderSelectorPreview() {
    MuzzChatTheme {
        SenderSelector(
            currentUser = ME,
            onUserSelected = {},
            modifier = Modifier.padding(Space200),
        )
    }
}
