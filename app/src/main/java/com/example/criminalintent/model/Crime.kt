package com.example.criminalintent.model

import java.util.Date
import java.util.UUID

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Crime(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var title: String = "",
    var suspect: String = ""
) {
    val imgPath
        get() = "IMG_$id.jpg"
}