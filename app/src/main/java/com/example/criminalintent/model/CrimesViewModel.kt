package com.example.criminalintent.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.criminalintent.reps.CrimeRepository

class CrimesViewModel : ViewModel() {
    private val repository = CrimeRepository.get()
    val crimesLiveData = repository.getCrimes()

    fun addCrime(crime: Crime) {
        repository.insertCrime(crime)
    }
}