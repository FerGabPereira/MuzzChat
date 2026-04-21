package com.fernandopereira.muzzchat.data.repository

import com.fernandopereira.muzzchat.data.local.MessageDao
import com.fernandopereira.muzzchat.data.local.toEntity
import com.fernandopereira.muzzchat.data.local.toMessage
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepositoryImpl(private val dao: MessageDao) : MessageRepository {
    override val messages: Flow<List<Message>> =
        dao.observeAll().map { entities -> entities.map { it.toMessage() } }

    override suspend fun insert(message: Message) {
        dao.insert(message.toEntity())
    }
}
