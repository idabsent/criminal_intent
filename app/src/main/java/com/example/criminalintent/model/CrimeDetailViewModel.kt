package com.example.criminalintent.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

import com.example.criminalintent.reps.CrimeRepository

import java.util.UUID
import java.io.File

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private var crimeIdLiveData = MutableLiveData<UUID>()

    val crimeLiveData = Transformations.switchMap(crimeIdLiveData) {uuid ->
        crimeRepository.getCrime(uuid)
    }

    fun loadCrime(id: UUID) {
        crimeIdLiveData.value = id
    }

    fun updateCrime(crime: Crime?) {
        crime?.let {
            crimeRepository.updateCrime(crime)
        }
    }

    fun saveCrime(crime: Crime?) {
        crime?.let {
            crimeRepository.updateCrime(crime)
        }
    }

    fun getFile(crime: Crime) : File {
        return crimeRepository.getFilePath(crime)
    }
}