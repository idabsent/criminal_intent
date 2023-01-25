package com.example.criminalintent.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import com.example.criminalintent.model.CrimesViewModel
import android.content.Context
import android.util.Log
import java.util.*

class CrimesFragment : Fragment() {
    interface FragmentCallback {
        fun uuidSelected(id: UUID)
    }

    private lateinit var recyclerView: RecyclerView
    private var fragmentCallback: FragmentCallback? = null

    private val crimesViewModel by lazy {
        ViewModelProvider(this)[CrimesViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_crimes, container, false)
        recyclerView = view.findViewById(R.id.crimes)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = Adapter(listOf())
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentCallback = context as FragmentCallback
    }

    override fun onDetach() {
        super.onDetach()

        fragmentCallback = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimesViewModel.crimesLiveData.observe(viewLifecycleOwner) {
            recyclerView.adapter = Adapter(it)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_crimes, menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem) : Boolean {
        super.onOptionsItemSelected(menuItem)
        return when(menuItem.itemId) {
            R.id.new_crime -> {
                val newCrime = Crime()
                crimesViewModel.addCrime(newCrime)
                fragmentCallback?.uuidSelected(newCrime.id)
                true
            }
            else -> false
        }
    }

    inner class CrimeHolder(view: View) :
        RecyclerView.ViewHolder(view)
    {
        val titleTextView: TextView = view.findViewById(R.id.crime_title)
        val dateTextView: TextView = view.findViewById(R.id.crime_date)
        val imgView: ImageView = view.findViewById(R.id.img)

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()
            Log.d("CrimesFragment", "crime{${crime.title}}.isSolved: ${crime.isSolved}")
            imgView.visibility = when(crime.isSolved) {
                false -> 0
                true -> 100
            }
        }

        var crime: Crime? = null
    }

    private inner class Adapter(val crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun getItemCount() = crimes.size

        override fun onCreateViewHolder(parent: ViewGroup, type: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.crime_item, parent, false)
            return CrimeHolder(view).apply {
                itemView.setOnClickListener{
                    fragmentCallback?.uuidSelected(crime!!.id)
                }
            }
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }
    }
}