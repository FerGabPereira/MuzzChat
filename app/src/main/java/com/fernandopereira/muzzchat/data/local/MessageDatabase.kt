package com.fernandopereira.muzzchat.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fernandopereira.muzzchat.common.DATABASE_NAME

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        fun build(context: Context): MessageDatabase =
            Room.databaseBuilder(context, MessageDatabase::class.java, DATABASE_NAME)
                .build()
    }
}
