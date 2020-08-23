package com.altun.dimasnotebook.room

import android.content.Context
import androidx.room.Room
import com.altun.dimasnotebook.App

object DatabaseBuilder {
    private var instance: NoteDatabase? = null

    fun getInstance(): NoteDatabase {
        if (instance == null)
            instance = buildRoom()

        return instance!!
    }

    private fun buildRoom() =
        Room.databaseBuilder(App.context!!, NoteDatabase::class.java, "note-database")
            .build()


}