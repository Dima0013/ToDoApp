package com.altun.dimasnotebook.room

import androidx.room.*


@Dao
interface NoteDao {
    @Query("SELECT * FROM notemodel")
    suspend fun getAll(): List<NoteModel>

    @Query("SELECT * FROM notemodel WHERE noteId IN (:noteIds)")
    suspend fun loadAllByIds(noteIds: IntArray): List<NoteModel>

    @Query("SELECT * FROM notemodel WHERE title LIKE :first AND " +
            "description LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): NoteModel

    @Insert
    suspend fun insertAll(vararg users: NoteModel)

    @Insert
    suspend fun insert(user: NoteModel)

    @Update
    suspend fun updateItem(noteModel: NoteModel)

    @Delete
    suspend fun delete(user: NoteModel)

    @Delete
    suspend fun deleteAll(vararg user: NoteModel)
}