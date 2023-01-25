package com.example.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.example.criminalintent.view.fragment.CrimeFragment
import com.example.criminalintent.view.fragment.CrimesFragment
import java.util.*

class MainActivity :
    AppCompatActivity() ,
    CrimesFragment.FragmentCallback {

    override fun uuidSelected(id: UUID) {
        val detailFragment = CrimeFragment.newInstance(id)

        supportFragmentManager
            .beginTransaction()
                .replace(R.id.crime_fragment, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.crime_fragment)
        if (fragment == null) {
            val crimesFragment = CrimesFragment()
            supportFragmentManager
                .beginTransaction()
                    .add(R.id.crime_fragment, crimesFragment)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
}