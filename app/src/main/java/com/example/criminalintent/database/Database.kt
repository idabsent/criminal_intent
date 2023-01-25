package com.example.criminalintent.database

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.model.Crime
import java.util.Date
import java.util.UUID

@Database(
    entities = [ Crime::class ],
    version = 2
)
@TypeConverters(CrimeConverter::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun getDao() : CrimeDao
}

class CrimeConverter {
    @TypeConverter
    fun fromDate(date: Date?) : Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?) : Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?) : UUID? {
        return UUID.fromString(uuid)
    }
}

object migrate_1_2 : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}