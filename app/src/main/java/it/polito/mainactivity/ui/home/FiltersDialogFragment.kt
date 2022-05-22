package it.polito.mainactivity.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skydoves.expandablelayout.ExpandableLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentFiltersBottomDialogBinding
import java.util.*

private var MINDATE: Long = GregorianCalendar.getInstance().timeInMillis
private var MINHOUR: Int = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
private var MINMINUTE: Int = GregorianCalendar.getInstance().get(Calendar.MINUTE)

class FiltersDialogFragment() : BottomSheetDialogFragment() {

    private var _binding: FragmentFiltersBottomDialogBinding? = null
    private val binding get() = _binding!!

    private var selectedMinDuration: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBottomDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val expandableDate = binding.expandableDate
        val expandableHour = binding.expandableHour
        val expandableDuration = binding.expandableDuration

        expandableDate.parentLayout.setOnClickListener { toggleExpandable(expandableDate) }
        expandableHour.parentLayout.setOnClickListener { toggleExpandable(expandableHour) }
        expandableDuration.parentLayout.setOnClickListener { toggleExpandable(expandableDuration) }

        binding.btnApplyFilters.setOnClickListener{
            //put values of the filters in the bundle and give them back to filteredTimeslotListFragment
            val bundle = bundleOf(
                "startDate" to expandableDate.secondLayout.findViewById<TextView>(R.id.tvStartDate).text.toString(),
                "endDate" to expandableDate.secondLayout.findViewById<TextView>(R.id.tvEndDate).text.toString(),

                "startTime" to expandableHour.secondLayout.findViewById<TextView>(R.id.tvStartTime).text.toString(),
                "endTime" to expandableHour.secondLayout.findViewById<TextView>(R.id.tvEndTime).text.toString(),

                "minDuration" to expandableDuration.secondLayout.findViewById<AutoCompleteTextView>(R.id.tvMinDuration).text.toString(),
                "maxDuration" to expandableDuration.secondLayout.findViewById<AutoCompleteTextView>(R.id.tvMaxDuration).text.toString(),
            )
            setFragmentResult("applyFilters", bundle)
        }

        return root
    }

    private fun toggleExpandable(ex: ExpandableLayout) {
        if (ex.isExpanded) {
            ex.collapse()
        } else {
            ex.expand()
        }
    }

    override fun onResume() {
        val secondLayoutDate = binding.expandableDate.secondLayout
        val tvStartDate = secondLayoutDate.findViewById<TextView>(R.id.tvStartDate)
        val tvEndDate = secondLayoutDate.findViewById<TextView>(R.id.tvEndDate)

        tvStartDate.setOnClickListener {
            DatePickerFragment(tvStartDate, DatePickerFragment.DType.START, MINDATE).show(
                requireActivity().supportFragmentManager,
                "startDatePicker"
            )
        }

        tvEndDate.setOnClickListener {
            DatePickerFragment(tvEndDate, DatePickerFragment.DType.END, MINDATE).show(
                requireActivity().supportFragmentManager,
                "endDatePicker"
            )
        }

        val secondLayoutHour = binding.expandableHour.secondLayout
        val tvStartTime = secondLayoutHour.findViewById<TextView>(R.id.tvStartTime)
        val tvEndTime = secondLayoutHour.findViewById<TextView>(R.id.tvEndTime)

        tvStartTime.setOnClickListener {
            TimePickerFragment(
                tvStartTime,
                TimePickerFragment.HType.START,
                MINHOUR,
                MINHOUR,
                binding.root
            ).show(requireActivity().supportFragmentManager, "startTimePicker")
        }
        tvEndTime.setOnClickListener {
            TimePickerFragment(
                tvEndTime,
                TimePickerFragment.HType.END,
                MINHOUR,
                MINMINUTE,
                binding.root
            ).show(requireActivity().supportFragmentManager, "endTimePicker")
        }

        val secondLayoutDuration = binding.expandableDuration.secondLayout
        val tvMinDuration = secondLayoutDuration.findViewById<AutoCompleteTextView>(R.id.tvMinDuration)
        val tvMaxDuration = secondLayoutDuration.findViewById<AutoCompleteTextView>(R.id.tvMaxDuration)

        val durations = resources.getStringArray(R.array.durations_array)
        val anyObject: Any = durations
        val maxDurationsArrayList =
            (anyObject as Array<*>).map { it.toString() }.filter { it != "30m" }

        val minDurationsArray = durations.filter { it != "4h" }

        val minDurationsArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, minDurationsArray)
        val maxDurationsArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, maxDurationsArrayList)

        tvMinDuration.setAdapter(minDurationsArrayAdapter)
        tvMaxDuration.setAdapter(maxDurationsArrayAdapter)

        tvMinDuration.onItemClickListener = AdapterView.OnItemClickListener { _, _, idx, _ ->
            selectedMinDuration = idx
            maxDurationsArrayAdapter.clear()
            durations.forEachIndexed { index, duration ->
                if (index > selectedMinDuration)
                    maxDurationsArrayAdapter.add(duration)
            }
        }

        super.onResume()
    }

    class DatePickerFragment(private val tvDate: TextView, private var type: DType) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {

        enum class DType {
            START, END
        }

        private var minDate: Long? = null

        constructor(_tvDate: TextView, _type: DType, _minDate: Long) : this(_tvDate, _type) {
            minDate = _minDate
        }

        private lateinit var dialog: DatePickerDialog

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker only if it's the first time opening
            var year: Int
            var month: Int
            var day: Int
            val c = Calendar.getInstance()
            if (tvDate.text != "dd/mm/yyyy") {
                val list = tvDate.text.toString().split("/")
                val y = list[2].toInt()
                val m = list[1].toInt() - 1
                val d = list[0].toInt()
                c.set(y, m, d)
            }
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)


            // Create a new instance of DatePickerDialog and return it
            dialog = DatePickerDialog(requireContext(), this, year, month, day)
            if (type == DType.START)
                dialog.datePicker.minDate = c.timeInMillis
            else
                dialog.datePicker.minDate = minDate!!
            return dialog
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            tvDate.text = "${day}/${(month + 1)}/${year}"
            if (type == DType.START) {
                val cal = GregorianCalendar.getInstance()
                cal.set(year, month, day)
                MINDATE = cal.timeInMillis
            }
        }
    }

    class TimePickerFragment(
        private val tvTime: TextView,
        private var type: HType,
        private var minHour: Int,
        private var minMinute: Int,
        private var v: View
    ) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        enum class HType {
            START, END
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            return TimePickerDialog(activity, this, hour, minute, true)
        }

        @SuppressLint("SetTextI18n")
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            if (minute < 10) {
                tvTime.text = "$hourOfDay:0$minute"
            } else {
                tvTime.text = "$hourOfDay:$minute"
            }
            if (type == HType.START) {
                MINHOUR = hourOfDay
                MINMINUTE = minute
            } else {
                if (hourOfDay < MINHOUR) {
                    if (minute < 10) {
                        tvTime.text = "$MINHOUR:0$MINMINUTE"
                    } else {
                        tvTime.text = "$MINHOUR:$MINMINUTE"
                    }
                    //TODO: SHOW SNACKBAR
                }
            }
        }
    }

}


