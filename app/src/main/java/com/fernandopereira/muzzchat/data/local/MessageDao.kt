package com.fernandopereira.muzzchat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(entity: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}
