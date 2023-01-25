package com.example.criminalintent.view.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DateFragment : DialogFragment() {
    interface DateCallbacks {
        fun dateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val calendar = Calendar.getInstance()

        val argDate = arguments?.getSerializable(DATE_KEY) as? Date
        argDate?.let{
            calendar.time = it
        }

        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val dateListener = DatePickerDialog.OnDateSetListener { _ :DatePicker, year: Int, month: Int, day: Int ->
            val gregorianCalendar = GregorianCalendar(year, month, day)
            (targetFragment as? DateCallbacks)?.let {
                it.dateSelected(gregorianCalendar.time)
            }
        }

        return DatePickerDialog(
            requireContext(),
            dateListener,
            currentYear,
            currentMonth,
            currentDay
        )
    }

    companion object {
        private const val DATE_KEY = "DateKey"

        fun newInstance(date: Date) : DateFragment {
            val args = Bundle().apply {
                putSerializable(DATE_KEY, date)
            }

            return DateFragment().apply {
                arguments = args
            }
        }
    }
}