package com.fernandopereira.muzzchat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fernandopereira.muzzchat.presentation.chat.ChatScreen
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MuzzChatTheme {
                ChatScreen()
            }
        }
    }
}
