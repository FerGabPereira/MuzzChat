package com.fernandopereira.muzzchat.domain.repository

import com.fernandopereira.muzzchat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    val messages: Flow<List<Message>>
    suspend fun insert(message: Message)
    suspend fun deleteAll()
}
