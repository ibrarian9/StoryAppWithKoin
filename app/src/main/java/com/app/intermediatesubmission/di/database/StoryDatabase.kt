package com.app.intermediatesubmission.di.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.intermediatesubmission.di.models.StoryItem

@Database(
    entities = [StoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}