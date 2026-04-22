package com.fernandopereira.muzzchat.presentation.chat.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fernandopereira.muzzchat.R
import com.fernandopereira.muzzchat.domain.model.User
import com.fernandopereira.muzzchat.domain.model.User.ME
import com.fernandopereira.muzzchat.domain.model.User.SARAH

@Composable
fun User.displayName(): String =
    when (this) {
        ME -> stringResource(R.string.chat_user_me)
        SARAH -> stringResource(R.string.chat_user_sarah)
    }
