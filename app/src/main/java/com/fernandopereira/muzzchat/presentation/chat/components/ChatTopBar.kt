package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fernandopereira.muzzchat.domain.model.User.SARAH
import com.fernandopereira.muzzchat.presentation.chat.extensions.displayName
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.Dimen8
import com.fernandopereira.muzzchat.ui.theme.ElevationAppBar
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.SizeAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar() {
    Surface(shadowElevation = ElevationAppBar) {
        CenterAlignedTopAppBar(
            title = { ChatAppBarTitle() },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )
    }
}

@Composable
private fun ChatAppBarTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimen8),
    ) {
        Box(
            modifier = Modifier
                .size(SizeAvatar)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = SARAH.displayName().first().toString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Text(
            text = SARAH.displayName(),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@ThemePreviews
@Composable
private fun ChatTopBarPreview() {
    MuzzChatTheme {
        ChatTopBar()
    }
}
