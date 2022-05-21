package it.polito.mainactivity.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import it.polito.mainactivity.ui.timeslot.timeslot_edit.TimeslotEditFragment
import java.text.DateFormat
import java.util.*
import android.text.format.DateFormat.is24HourFormat

class FiltersDialogFragment(): BottomSheetDialogFragment() {

    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView

    class DatePickerFragment() : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        private val tvDate: TextView? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            tvDate?.text = "${day}/${(month + 1)}/${year}"
        }

    }

    class TimePickerFragment(tvTime: TextView) : DialogFragment(),
        TimePickerDialog.OnTimeSetListener {

        private val tvTime = tvTime

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            return TimePickerDialog(
                activity,
                this,
                hour,
                minute,
                true
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            if (minute < 10) {
                tvTime.text = "$hourOfDay:0$minute"
            } else {
                tvTime.text = "$hourOfDay:$minute"
            }
        }
    }

    private fun showTimePickerDialog() {
        val timeFragment = TimePickerFragment(tvTime)
        timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_filters_bottom_dialog, container, false)
        return view

        // show start startDate picker dialog

    }

}