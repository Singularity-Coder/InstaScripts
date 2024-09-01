package com.singularitycoder.learnit

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.singularitycoder.learnit.helpers.DbTable
import kotlinx.parcelize.Parcelize

@Entity(tableName = DbTable.SUBJECT)
@Parcelize
data class Subject(
    @PrimaryKey var id: String,
    var title: String = ""
) : Parcelable