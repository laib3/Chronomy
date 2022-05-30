package it.polito.mainactivity.ui.timeslot.timeslot_edit

import android.annotation.SuppressLint
import android.app.*
import android.graphics.Color
import java.util.Calendar
import java.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.viewModel.TimeslotViewModel
import kotlin.math.max

class TimeslotEditFragment : Fragment() {

    // Extending DialogFragment for a time picker
    class TimePickerFragment(
        private val tiTime: TextView?,
        private val vm: TimeslotViewModel,
        private val type: Type,
        private val timeslotId: String? = null
    ) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        enum class Type {
            START, END
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the saved time as initial value for the picker
            // Otherwise use the current time
            val c = Calendar.getInstance()
            var hour = c.get(Calendar.HOUR_OF_DAY)
            var minute = c.get(Calendar.MINUTE)

            if (type == Type.START) {
                val savedHourMinutes =
                    if (timeslotId != null) vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.startHour else vm.submitTimeslot.value?.startHour
                hour = savedHourMinutes?.split(":")?.get(0)?.toInt() ?: hour
                minute = savedHourMinutes?.split(":")?.get(1)?.toInt() ?: minute
            } else {
                val savedHourMinutes =
                    if (timeslotId != null) vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.endHour else vm.submitTimeslot.value?.endHour
                hour = savedHourMinutes?.split(":")?.get(0)?.toInt() ?: hour
                minute = savedHourMinutes?.split(":")?.get(1)?.toInt() ?: minute
            }

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
        override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
            val timeText = Utils.formatTime(hourOfDay, minute)
            // modify existing timeslot
            if (timeslotId != null) {
                val old = tiTime?.text
                if (type == Type.START && old != timeText) {
                    vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                        !vm.updateTimeslotField(this.timeslotId, "startHour", timeText)
                        if (timeText > this.endHour) {
                            vm.updateTimeslotField(this.timeslotId, "endHour", timeText)
                        }
                    } ?: apply {
                        // TODO: Show error message
                    }
                } else if (type == Type.END && old != timeText) {
                    vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                        if (timeText >= this.startHour) {
                            vm.updateTimeslotField(this.timeslotId, "endHour", timeText)
                        } else {
                            // display SnackBar when end time is before start time
                            val s: Snackbar = Snackbar.make(
                                requireActivity().findViewById(R.id.drawer_layout),
                                "ERROR: End time must be after start time!",
                                Snackbar.LENGTH_LONG
                            )
                            s.setTextColor(Color.parseColor("#ffff00"))
                            s.show()
                        }
                    } ?: apply {
                        // TODO: Show error message
                    }
                }
            }
            // new timeslot
            else {
                if (vm.submitTimeslot.value != null) {
                    if (type == Type.START) {
                        vm.setSubmitFields(startHour = timeText)
                        // if start hour is after end hour, set end hour to start hour

                        if (timeText > vm.submitTimeslot.value!!.endHour) {
                            vm.setSubmitFields(endHour = timeText)
                        }
                    } else if (type == Type.END) {
                        if (timeText >= vm.submitTimeslot.value!!.startHour)
                            vm.setSubmitFields(endHour = timeText)
                        else {
                            // display SnackBar when end time is before start time
                            val s: Snackbar = Snackbar.make(
                                requireActivity().findViewById(R.id.drawer_layout),
                                "ERROR: End time must be after start time!",
                                Snackbar.LENGTH_LONG
                            )
                            s.setTextColor(Color.parseColor("#ffff00"))
                            s.show()
                        }
                    }
                }
            }
        }
    }

    // Extending DialogFragment for a startDate picker
    class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

        enum class DType {
            START, END
        }

        private var tiDate: TextView? = null
        private var vm: TimeslotViewModel? = null
        private var type: DType? = null
        private var timeslotId: String? = null

        private lateinit var dialog: DatePickerDialog

        constructor(
            _tiDate: TextView?,
            _vm: TimeslotViewModel,
            _type: DType,
            _timeslotId: String? = null
        ) : this() {
            tiDate = _tiDate
            vm = _vm
            type = _type
            timeslotId = _timeslotId
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            dialog =
                when {
                    timeslotId != null -> { // update existing timeslot
                        val date: Calendar = vm?.timeslots?.value!!.find{ t -> t.timeslotId == timeslotId }?.date!!
                        DatePickerDialog(requireContext(), this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                            .also{ it.datePicker.minDate = GregorianCalendar.getInstance().timeInMillis }
                    }
                    type == DType.START -> {
                        val date: Calendar = vm?.submitTimeslot?.value?.date!!
                        DatePickerDialog(requireContext(), this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                            .also{ it.datePicker.minDate = GregorianCalendar.getInstance().timeInMillis }
                    }
                    type == DType.END -> {
                        val date: Calendar = vm?.submitEndRepetitionDate?.value!!
                        DatePickerDialog(requireContext(), this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                            .also{ it.datePicker.minDate = vm?.submitTimeslot?.value?.date!!.timeInMillis } // set start date of the interval as min date
                    }
                    else -> {
                        val date: Calendar = GregorianCalendar.getInstance()
                        DatePickerDialog(requireContext(), this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                            .also{ it.datePicker.minDate = GregorianCalendar.getInstance().timeInMillis }
                    }
                }
            return dialog
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val oldDateText = tiDate?.text
            val newDateText = Utils.formatYearMonthDayToString(year, month, day)
            // if the timeslot is being created
            val date: Calendar = GregorianCalendar(year, month, day)
            // modify existing timeslot
            if (timeslotId != null) {
                if (type == DType.START && oldDateText != newDateText) {
                    vm?.timeslots?.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                        vm?.updateTimeslotField(
                            this.timeslotId,
                            "date",
                            Utils.formatDateToString(date)
                        )
                    } ?: apply {
                        // TODO: Show error message
                    }
                }
            }
            // if new timeslot
            else {
                if (type == DType.START) {
                    vm?.setSubmitFields(date = date)
                    if (vm?.submitEndRepetitionDate?.value!!.before(date))
                        vm?.setSubmitFields(endRepetitionDate = date)
                } else if (type == DType.END) {
                    vm?.setSubmitFields(endRepetitionDate = date)
                }
            }
        }
    }

    private val vm: TimeslotViewModel by activityViewModels()

    private var _binding: FragmentTimeslotEditBinding? = null
    private val binding get() = _binding!!
    private var timeslotId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() { // set click listeners
        // check https://www.youtube.com/watch?v=741l_fPKL3Y
        super.onResume()

        timeslotId = arguments?.getString("id", null)

        val categories = resources.getStringArray(R.array.skills_array)
        val repetitions = resources.getStringArray(R.array.repetitionMw)
        val categoryArrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
        val repetitionsArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, repetitions)
        binding.tvCategory.setAdapter(categoryArrayAdapter)
        binding.tvRepetition.setAdapter(repetitionsArrayAdapter)

        val chips: MutableList<Chip> = mutableListOf(
            binding.S0,
            binding.M1,
            binding.T2,
            binding.W3,
            binding.T4,
            binding.F5,
            binding.S6
        )

        if(timeslotId != null){
            binding.lStartDate.text = "Date:"
        }
        else {
            binding.lStartDate.text = "Start date:"
        }

        // set listeners
        if (timeslotId != null)
            addFocusChangeListeners()
        else {
            addTextChangedListeners()
        }
        if (timeslotId != null) {
            val ts = vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }

            val positionCategory = categoryArrayAdapter.getPosition(ts?.category)
            // val positionRepetition = max(repetitionsArrayAdapter.getPosition(ts?.repetition), 0)
            binding.tvCategory.setText(
                categoryArrayAdapter.getItem(positionCategory).toString(),
                false
            )
            binding.tvCategory.onItemClickListener = OnItemClickListener { _, _, idx, _ ->
                vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                    vm.updateTimeslotField(this.timeslotId, "category", categories[idx])
                } ?: apply {
                    // TODO: Show error message
                }
            }
        } else { // submit timeslot
            val positionCategory =
                categoryArrayAdapter.getPosition(vm.submitTimeslot.value?.category)
            val positionRepetition =
                max(repetitionsArrayAdapter.getPosition(vm.submitRepetitionType.value), 0)
            binding.tvCategory.setText(
                categoryArrayAdapter.getItem(positionCategory).toString(),
                false
            )
            binding.tvCategory.onItemClickListener =
                OnItemClickListener { _, _, idx, _ -> vm.setSubmitFields(category = categories[idx]) }
            // dropdown menu
            binding.tvRepetition.setText(
                repetitionsArrayAdapter.getItem(positionRepetition).toString(), false
            )
            // dropdown menu
            binding.tvRepetition.onItemClickListener =
                OnItemClickListener { _, _, idx, _ -> vm.setSubmitFields(repetitionType = repetitions[idx]) }
            binding.bSubmit.apply { visibility = View.VISIBLE }

            val oldDays = vm.submitDaysOfWeek.value!!.toMutableSet()
            chips.forEachIndexed { idx, chip ->
                chip.setOnClickListener {
                    if (oldDays.contains(idx + 1) && oldDays.size > 1)
                        oldDays.remove(idx + 1)
                    else
                        oldDays.add(idx + 1)
                    vm.setSubmitFields(daysOfWeek = oldDays.toList())
                }
            }
        }

        // show start startDate picker dialog
        binding.tvStartDate.setOnClickListener {
            DatePickerFragment(binding.tvStartDate, vm, DatePickerFragment.DType.START, timeslotId)
                .show(requireActivity().supportFragmentManager, "startDatePicker")
        }
        // show end date picker dialog
        binding.tvEndDate.setOnClickListener {
            DatePickerFragment(binding.tvEndDate, vm, DatePickerFragment.DType.END, timeslotId)
                .show(requireActivity().supportFragmentManager, "endDatePicker")
        }
        // show start time picker dialog
        binding.tvStartTime.setOnClickListener {
            TimePickerFragment(binding.tvStartTime, vm, TimePickerFragment.Type.START, timeslotId)
                .show(requireActivity().supportFragmentManager, "startTimePicker")
        }
        // show end time picker dialog
        binding.tvEndTime.setOnClickListener {
            TimePickerFragment(binding.tvEndTime, vm, TimePickerFragment.Type.END, timeslotId)
                .show(requireActivity().supportFragmentManager, "endTimePicker")
        }

        binding.swRepetition.setOnClickListener {
            if (timeslotId != null) { // timeslotId is null if new timeslot
                // if the switch is checked, set repetition to weekly
                val repetition = if (binding.swRepetition.isChecked) "Weekly" else null
                vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                    vm.updateTimeslotField(this.timeslotId, "repetition", repetition)
                } ?: apply {
                    // TODO: Show error message
                }
            } else {
                // if the switch is checked, set repetition to weekly
                // to set repetition to null in submitTimeslot you need to pass empty string
                val repetition = if (binding.swRepetition.isChecked) "Weekly" else ""
                vm.setSubmitFields(repetitionType = repetition)
            }
        }

        binding.bSubmit.setOnClickListener {
            if (vm.submitTimeslot()) {
                // display SnackBar when end time is before start time
                val s: Snackbar = Snackbar.make(
                    requireActivity().findViewById(R.id.drawer_layout),
                    "New timeslot saved correctly!",
                    Snackbar.LENGTH_LONG
                )
                s.setTextColor(Color.parseColor("#55ff55"))
                s.show()
                findNavController().navigateUp()
            }
        }

        // OBSERVE
        // observe existing timeslot
        if (timeslotId != null) {
            hideRepetitionComponents() // repetition components not required for edit timeslot
            vm.timeslots.observe(viewLifecycleOwner) {
                val t: Timeslot? = it.find { t -> t.timeslotId == timeslotId }
                binding.tilTitle.editText?.setText(t?.title)
                binding.tilDescription.editText?.setText(t?.description)
                binding.tilLocation.editText?.setText(t?.location)
                binding.tvStartDate.text = Utils.formatDateToString(t?.date)
                binding.tvStartTime.text = t?.startHour
                binding.tvEndTime.text = t?.endHour
                val positionCategory = categoryArrayAdapter.getPosition(t?.category)
                binding.tvCategory.setText(
                    categoryArrayAdapter.getItem(positionCategory).toString(), false
                )
                if (vm.isValid(t))
                    notifySuccess(true)
                else
                    notifySuccess(false)
            }
        }
        // observe new timeslot
        else {
            vm.submitTimeslot.observe(viewLifecycleOwner) {
                binding.tvStartDate.text = Utils.formatDateToString(it.date)
                binding.tvStartTime.text = it.startHour
                binding.tvEndTime.text = it.endHour
                val positionCategory = categoryArrayAdapter.getPosition(it.category)
                binding.tvCategory.setText(
                    categoryArrayAdapter.getItem(positionCategory).toString(), false
                )
                binding.tvEndDate.text =
                    if (!it.date.after(vm.submitEndRepetitionDate.value))
                        Utils.formatDateToString(vm.submitEndRepetitionDate.value)
                    else
                        Utils.formatDateToString(it.date)
                binding.bSubmit.apply { isEnabled = vm.isValid(it) }
            }
            vm.submitDaysOfWeek.observe(viewLifecycleOwner){
                chips.forEachIndexed { idx, chip -> chip.isChecked = it.contains(idx + 1) }
            }
            vm.submitRepetitionType.observe(viewLifecycleOwner){
                val positionRepetition = max(repetitionsArrayAdapter.getPosition(it), 0)
                binding.tvRepetition.setText(
                    repetitionsArrayAdapter.getItem(positionRepetition).toString(), false
                )
                setRepetitionComponentsVisibility(it)
            }
            vm.submitEndRepetitionDate.observe(viewLifecycleOwner) {
                binding.tvEndDate.text =
                    if (!vm.submitTimeslot.value!!.date.after(it))
                        Utils.formatDateToString(it)
                    else
                        Utils.formatDateToString(vm.submitTimeslot.value!!.date)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideRepetitionComponents(){
        binding.swRepetition.visibility = View.GONE
        binding.lEndDate.visibility = View.GONE
        binding.cvEndDate.visibility = View.GONE
        binding.lRepeat.visibility = View.GONE
        binding.dmRepetition.visibility = View.GONE
        binding.cgDays.visibility = View.GONE
    }

    private fun setRepetitionComponentsVisibility(repetitionType: String?) {
        binding.lEndDate.visibility =
            if (repetitionType != "" && repetitionType != null) View.VISIBLE else View.INVISIBLE
        binding.cvEndDate.visibility =
            if (repetitionType != "" && repetitionType != null) View.VISIBLE else View.INVISIBLE
        binding.lRepeat.visibility =
            if (repetitionType != "" && repetitionType != null) View.VISIBLE else View.GONE
        binding.dmRepetition.visibility =
            if (repetitionType != "" && repetitionType != null) View.VISIBLE else View.GONE
        binding.cgDays.visibility = if (repetitionType == "Weekly") View.VISIBLE else View.GONE
    }

    // use only when the timeslot exists
    private fun addFocusChangeListeners() {
        binding.teTitle.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.title.toString()
            val new = binding.teTitle.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new && new.isNotBlank()) {
                vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                    vm.updateTimeslotField(this.timeslotId, "title", new)
                } ?: apply {
                    // TODO: Show error message
                }
            }
        }
        binding.teDescription.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.description.toString()
            val new = binding.teDescription.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new) {
                vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                    vm.updateTimeslotField(this.timeslotId, "description", new)
                } ?: apply {
                    // TODO: Show error message
                }
            }
        }
        binding.teLocation.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.location.toString()
            val new = binding.teLocation.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new && new.isNotBlank()) {
                vm.timeslots.value!!.find { t -> t.timeslotId == timeslotId }?.apply {
                    vm.updateTimeslotField(this.timeslotId, "location", new)
                } ?: apply {
                    // TODO: Show error message
                }
            }
        }
    }

    // use only when the timeslot is new
    private fun addTextChangedListeners() {
        binding.teTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                vm.setSubmitFields(title = s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.teDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                vm.setSubmitFields(description = p0.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.teLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                vm.setSubmitFields(location = p0.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun notifySuccess(success: Boolean) {
        if (success)
            (activity as MainActivity).snackBarMessage = "Timeslot changed successfully!"
        else
            (activity as MainActivity).snackBarMessage =
                "ERROR: Some fields were missing. Incomplete update."
    }

}