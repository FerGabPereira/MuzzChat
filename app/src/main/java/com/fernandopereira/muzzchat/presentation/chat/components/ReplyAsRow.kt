package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fernandopereira.muzzchat.R
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.Dimen8
import com.fernandopereira.muzzchat.ui.theme.Dimen12

@Composable
fun ReplyAsRow(
    currentUser: User,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.chat_reply_as_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SenderSelector(
            currentUser = currentUser,
            onUserSelected = onUserSelected,
        )
    }
}

@ThemePreviews
@Composable
private fun ReplyAsRowPreview() {
    MuzzChatTheme {
        Surface {
            ReplyAsRow(
                currentUser = User.ME,
                onUserSelected = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen12, vertical = Dimen8),
            )
        }
    }
}
