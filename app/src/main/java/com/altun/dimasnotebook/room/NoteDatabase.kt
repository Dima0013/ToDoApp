package com.altun.dimasnotebook.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(NoteModel::class),version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao() : NoteDao
}