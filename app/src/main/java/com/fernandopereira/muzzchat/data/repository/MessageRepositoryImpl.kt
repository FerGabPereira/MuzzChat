package com.fernandopereira.muzzchat.data.repository

import com.fernandopereira.muzzchat.data.local.DatabaseSeeder
import com.fernandopereira.muzzchat.data.local.MessageDao
import com.fernandopereira.muzzchat.data.local.toEntity
import com.fernandopereira.muzzchat.data.local.toMessage
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MessageRepositoryImpl(
    private val dao: MessageDao,
    private val seeder: DatabaseSeeder,
) : MessageRepository {
    // Demo app: seed sample data the first time the chat is observed.
    override val messages: Flow<List<Message>> =
        dao.observeAll()
            .onStart { seeder.seedIfEmpty() }
            .map { entities -> entities.map { it.toMessage() } }

    override suspend fun insert(message: Message) {
        dao.insert(message.toEntity())
    }

    override suspend fun deleteAll() = dao.deleteAll()
}
