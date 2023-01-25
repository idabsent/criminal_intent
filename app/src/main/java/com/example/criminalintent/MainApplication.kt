package com.example.criminalintent

import android.app.Application
import android.util.Log
import com.example.criminalintent.reps.CrimeRepository

class MainApplication : Application() {
    companion object {
        const val TAG = "MainApplication"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Application created")
        CrimeRepository.initialize(this)
    }
}