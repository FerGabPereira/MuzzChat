package com.fernandopereira.muzzchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fernandopereira.muzzchat.domain.model.Message
import com.fernandopereira.muzzchat.domain.model.User

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val text: String,
    val senderId: String,
    val timestamp: Long,
)

fun MessageEntity.toMessage(): Message =
    Message(
        id = id,
        text = text,
        sender = User.entries.firstOrNull { it.id == senderId }
            ?: error("Unknown senderId: $senderId"),
        timestamp = timestamp,
    )

fun Message.toEntity(): MessageEntity =
    MessageEntity(
        id = id,
        text = text,
        senderId = sender.id,
        timestamp = timestamp,
    )
