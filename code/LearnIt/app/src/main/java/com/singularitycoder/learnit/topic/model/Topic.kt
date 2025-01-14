package com.singularitycoder.learnit.topic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.singularitycoder.learnit.helpers.constants.DbTable
import kotlinx.parcelize.Parcelize

@Entity(tableName = DbTable.TOPIC)
@Parcelize
data class Topic(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var title: String = "",
    var subjectId: Long = 0L,
    var studyMaterial: String = "",
    var dateStarted: Long = 0L,
    var nextSessionDate: Long = 0L,
    var finishedSessions: Int = 0, // 1 to 8
    var revisionCount: Int = 0,
    var alarmType: Int = 0,
    var alarmTone: String = "",
    var alarmVolume: Int = 0
) : Parcelable