package com.example.criminalintent.reps

import androidx.lifecycle.LiveData
import android.content.Context
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.database.migrate_1_2
import com.example.criminalintent.model.Crime
import java.io.File
import java.lang.IllegalStateException
import java.util.UUID
import java.util.concurrent.Executors

class CrimeRepository private constructor (context: Context) {
    private var database : CrimeDatabase = Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(migrate_1_2)
        .build()

    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getCrimes() : LiveData<List<Crime>> {
        return database.getDao().getCrimes()
    }

    fun getCrime(id: UUID) : LiveData<Crime?> {
        return database.getDao().getCrime(id)
    }

    fun updateCrime(crime: Crime) {
        executor.execute {
            database.getDao().updateCrime(crime)
        }
    }

    fun insertCrime(crime: Crime) {
        executor.execute {
            database.getDao().insertCrime(crime)
        }
    }

    fun saveCrime(crime: Crime) {
        updateCrime(crime)
    }

    fun getFilePath(crime: Crime) : File{
        return File(filesDir, crime.imgPath)
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null
        private const val DATABASE_NAME = "crime-database"

        fun initialize(context: Context) {
            INSTANCE = CrimeRepository(context)
        }

        fun get() : CrimeRepository {
            return INSTANCE ?: let {
                throw IllegalStateException("CrimeRepository don't initialized")
            }
        }
    }
}
