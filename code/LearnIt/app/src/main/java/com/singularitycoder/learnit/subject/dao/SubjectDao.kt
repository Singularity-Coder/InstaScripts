package com.singularitycoder.learnit.subject.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.singularitycoder.learnit.helpers.constants.DbTable
import com.singularitycoder.learnit.subject.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    /** room database will replace data based on primary key */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Subject>)

    @Query("SELECT EXISTS(SELECT * FROM ${DbTable.SUBJECT} WHERE id = :id)")
    suspend fun isItemPresent(id: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM ${DbTable.SUBJECT})")
    suspend fun hasItems(): Boolean

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(subject: Subject)

//    @Query("UPDATE ${DbTable.SUBJECT} SET completedPageNum = :completedPageNum WHERE id LIKE :id")
//    suspend fun updateCompletedPageWithId(completedPageNum: Int, id: String)

    @Query("SELECT * FROM ${DbTable.SUBJECT} WHERE id LIKE :id LIMIT 1")
    suspend fun getItemById(id: String): Subject

    @Query("SELECT * FROM ${DbTable.SUBJECT}")
    fun getAllItemsLiveData(): LiveData<List<Subject>>

    @Query("SELECT * FROM ${DbTable.SUBJECT}")
    fun getAllItemsStateFlow(): Flow<List<Subject>>

//    @Query("SELECT * FROM ${Table.BOOK} WHERE website = :website")
//    fun getAllItemsByWebsiteStateFlow(website: String?): Flow<List<Book>>
//
//    @Query("SELECT * FROM ${Table.BOOK} WHERE isSaved = 1")
//    fun getAllSavedItemsStateFlow(): Flow<List<Book>>
//
//    @Query("SELECT * FROM ${Table.BOOK} WHERE website = :website")
//    fun getItemByWebsiteStateFlow(website: String?): Flow<Book>

    @Query("SELECT * FROM ${DbTable.SUBJECT}")
    suspend fun getAll(): List<Subject>


    @Transaction
    @Delete
    suspend fun delete(subject: Subject)

//    @Transaction
//    @Query("DELETE FROM ${Table.BOOK} WHERE website = :website")
//    suspend fun deleteByWebsite(website: String?)

//    @Transaction
//    @Query("DELETE FROM ${DbTable.SUBJECT} WHERE time >= :time")
//    suspend fun deleteAllByTime(time: Long?)

    @Transaction
    @Query("DELETE FROM ${DbTable.SUBJECT}")
    suspend fun deleteAll()
}
