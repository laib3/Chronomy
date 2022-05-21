package it.polito.mainactivity.ui.home


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skydoves.expandablelayout.ExpandableLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentFiltersBottomDialogBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private var MINDATE:Long = GregorianCalendar.getInstance().timeInMillis

class FiltersDialogFragment(): BottomSheetDialogFragment() {

    private var _binding: FragmentFiltersBottomDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentFiltersBottomDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val expandableDate= binding.expandableDate
        val expandableHour = binding.expandableHour
        val expandableDuration = binding.expandableDuration

        expandableDate.parentLayout.setOnClickListener{toggleExpandable(expandableDate)}
        expandableHour.parentLayout.setOnClickListener{toggleExpandable(expandableHour)}
        expandableDuration.parentLayout.setOnClickListener{toggleExpandable(expandableDuration)}

        return root
    }

    private fun toggleExpandable(ex: ExpandableLayout){
        if(ex.isExpanded){
            ex.parentLayout.setBackgroundResource(R.drawable.searchview_background)
            ex.collapse()
        }else{
            ex.parentLayout.setBackgroundResource(R.drawable.expandable_parent_background)
            ex.expand()
        }
    }

    override fun onResume() {
        var secondLayoutDate = binding.expandableDate.secondLayout
        var tvStartDate = secondLayoutDate.findViewById<TextView>(R.id.tvStartDate)
        var tvEndDate = secondLayoutDate.findViewById<TextView>(R.id.tvEndDate)

        tvStartDate.setOnClickListener{
            DatePickerFragment(tvStartDate, DatePickerFragment.DType.START, MINDATE).show(requireActivity().supportFragmentManager, "startDatePicker")
        }

       tvEndDate.setOnClickListener{
            DatePickerFragment(tvEndDate, DatePickerFragment.DType.END, MINDATE).show(requireActivity().supportFragmentManager, "endDatePicker")
        }

        var secondLayoutHour = binding.expandableHour.secondLayout
        var tvStartTime = secondLayoutHour.findViewById<TextView>(R.id.tvStartTime)
        var tvEndTime = secondLayoutHour.findViewById<TextView>(R.id.tvEndTime)

        tvStartTime.setOnClickListener{
            TimePickerFragment(tvStartTime).show(requireActivity().supportFragmentManager, "startTimePicker")
        }
        tvEndTime.setOnClickListener{
            TimePickerFragment(tvEndTime).show(requireActivity().supportFragmentManager, "endTimePicker")
        }

        super.onResume()
    }

    class DatePickerFragment(private val tvDate : TextView, private var type:DType) : DialogFragment(), DatePickerDialog.OnDateSetListener {

        enum class DType {
            START, END
        }

        private var minDate:Long? = null

        constructor( _tvDate:TextView, _type:DType, _minDate:Long) : this(_tvDate, _type){
            minDate = _minDate
        }

        private lateinit var dialog: DatePickerDialog

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker only if it's the first time opening
            var year = 0
            var month = 0
            var day = 0
            val c = Calendar.getInstance()
            if(tvDate.text != "dd/mm/yyyy"){
                var list = tvDate.text.toString().split("/")
                var y = list[2].toInt()
                var m = list[1].toInt()-1
                var d = list[0].toInt()
                c.set(y, m, d)
            }
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)


            // Create a new instance of DatePickerDialog and return it
           dialog = DatePickerDialog(requireContext(), this, year, month, day)
            if(type == DType.START)
                    dialog.datePicker.minDate = c.timeInMillis
                else
                    dialog.datePicker.minDate = minDate!!
            return dialog
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            tvDate?.text = "${day}/${(month + 1)}/${year}"
            if(type == DType.START){
                val cal  = GregorianCalendar.getInstance()
                    cal.set(year, month, day)
                MINDATE = cal.timeInMillis
                }
            }
        }

    }

    class TimePickerFragment(private val tvTime: TextView) : DialogFragment(),TimePickerDialog.OnTimeSetListener {

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
