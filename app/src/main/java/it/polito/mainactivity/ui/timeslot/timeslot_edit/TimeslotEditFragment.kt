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
import android.util.Log
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
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import kotlin.math.max

class TimeslotEditFragment : Fragment() {

    // Extending DialogFragment for a time picker
    class TimePickerFragment(private val tiTime: TextView?,private val vm: TimeslotViewModel,private val type: Type,private val tId: Int? = null) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
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
                    if (tId != null) vm.timeslots.value?.elementAt(tId)?.startHour else vm.submitTimeslot.value?.startHour
                hour = savedHourMinutes?.split(":")?.get(0)?.toInt() ?: hour
                minute = savedHourMinutes?.split(":")?.get(1)?.toInt() ?: minute
            } else {
                val savedHourMinutes =
                    if (tId != null) vm.timeslots.value?.elementAt(tId)?.endHour else vm.submitTimeslot.value?.endHour
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
            if (tId != null) {
                val old = tiTime?.text
                if (type == Type.START && old != timeText) {
                    vm.timeslots.value?.elementAt(tId)?.apply {
                        // TODO: CHECK THIS !
                        !vm.updateTimeslotField(this.tid, "startHour", timeText)
                        if (timeText > this.endHour) {
                            vm.updateTimeslotField(this.tid, "endHour", timeText)
                        }
                    } ?: apply {
                        // TODO: Show error message
                    }
                } else if (type == Type.END && old != timeText) {
                    vm.timeslots.value?.elementAt(tId)?.apply {
                        if (timeText >= this.startHour) {
                            vm.updateTimeslotField(this.tid, "endHour", timeText)
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
                        vm.setSubmitTimeslotFields(startHour = timeText)
                        // if start hour is after end hour, set end hour to start hour
                        if (timeText > vm.submitTimeslot.value!!.endHour) {
                            vm.setSubmitTimeslotFields(endHour = timeText)
                        }
                    } else if (type == Type.END) {
                        if (timeText >= vm.submitTimeslot.value!!.startHour)
                            vm.setSubmitTimeslotFields(endHour = timeText)
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
        private var tId: Int? = null

        private lateinit var dialog: DatePickerDialog

        constructor(
            _tiDate: TextView?,
            _vm: TimeslotViewModel,
            _type: DType,
            _timeslotId: Int? = null
        ) : this() {
            tiDate = _tiDate
            vm = _vm
            type = _type
            tId = _timeslotId
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current startDate as the default startDate in the picker in new Timeslot,
            // otherwise use the saved one
            val startDate =
                if (tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.startDate else vm?.submitTimeslot?.value?.startDate
            val endDate =
                if (tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.endRepetitionDate else vm?.submitTimeslot?.value?.endRepetitionDate
            val repetition =
                if (tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.repetition else vm?.submitTimeslot?.value?.repetition

            val year =
                if (type == DType.END && repetition != null) endDate?.get(Calendar.YEAR) else startDate?.get(
                    Calendar.YEAR
                )
            val month =
                if (type == DType.END && repetition != null) endDate?.get(Calendar.MONTH) else startDate?.get(
                    Calendar.MONTH
                )
            val day =
                if (type == DType.END && repetition != null) endDate?.get(Calendar.DAY_OF_MONTH) else startDate?.get(
                    Calendar.DAY_OF_MONTH
                )

            // Create a new instance of DatePickerDialog and return it
            dialog = DatePickerDialog(requireContext(), this, year!!, month!!, day!!)
            if (type == DType.END)
                dialog.datePicker.minDate = startDate!!.timeInMillis
            else if (type == DType.START)
                dialog.datePicker.minDate = GregorianCalendar.getInstance().timeInMillis
            return dialog
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val old = tiDate?.text
            val new = Utils.formatYearMonthDayToString(year, month, day)
            // if the timeslot is being created
            val date: Calendar = GregorianCalendar(year, month, day)
            // modify existing timeslot
            if (tId != null) {

                if (type == DType.START && old != new) {
                    vm?.timeslots?.value?.elementAt(tId!!)?.apply {
                        vm?.updateTimeslotField(
                            this.tid,
                            "startDate",
                            Utils.formatDateToString(date)
                        )
                        if (this.endRepetitionDate.before(date) == true)
                            vm?.updateTimeslotField(
                                this.tid,
                                "endRepetitionDate",
                                Utils.formatDateToString(date)
                            )
                    } ?: apply {
                        // TODO: Show error message
                    }
                } else if (type == DType.END && old != new) {
                    vm?.timeslots?.value?.elementAt(tId!!)?.apply {
                        vm?.updateTimeslotField(
                            this.tid,
                            "endRepetitionDate",
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
                    vm?.setSubmitTimeslotFields(date = date)
                    if (vm?.submitTimeslot?.value?.endRepetitionDate?.before(date) == true)
                        vm?.setSubmitTimeslotFields(endRepetitionDate = date)
                } else if (type == DType.END) {
                    vm?.setSubmitTimeslotFields(endRepetitionDate = date)
                }
            }
        }
    }

    private val vm: TimeslotViewModel by activityViewModels()

    private var _binding: FragmentTimeslotEditBinding? = null
    private val binding get() = _binding!!
    private var tId: Int? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onResume() {
        // check https://www.youtube.com/watch?v=741l_fPKL3Y
        super.onResume()

        tId = arguments?.getInt("id", -1)
        tId = if (tId == -1) null else tId

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

        // set listeners
        if (tId != null)
            addFocusChangeListeners()
        else {
            addTextChangedListeners()
        }
        if (tId != null) {
            val t = vm.timeslots.value?.elementAt(tId!!)
            val positionCategory = categoryArrayAdapter.getPosition(t?.category)
            val positionRepetition = max(repetitionsArrayAdapter.getPosition(t?.repetition), 0)
            binding.tvCategory.setText(
                categoryArrayAdapter.getItem(positionCategory).toString(),
                false
            )
            binding.tvRepetition.setText(
                repetitionsArrayAdapter.getItem(positionRepetition).toString(), false
            )
            binding.tvCategory.onItemClickListener = OnItemClickListener { _, _, idx, _ ->
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "category", categories[idx])
                } ?: apply {
                    // TODO: Show error message
                }
            }
            binding.tvRepetition.onItemClickListener = OnItemClickListener { _, _, idx, _ ->
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "repetition", repetitions[idx])
                } ?: apply {
                    // TODO: Show error message
                }
            }
            chips.forEachIndexed { index, chip ->
                chip.setOnClickListener {
                    val oldDays = vm.timeslots.value?.elementAt(tId!!)?.days?.toMutableSet()
                    if (oldDays?.contains(index + 1) == true && oldDays.size > 1)
                        oldDays.remove(index + 1)
                    else
                        oldDays?.add(index + 1)
                    vm.timeslots.value?.elementAt(tId!!)?.apply {
                        vm.updateTimeslotField(this.tid, "days", oldDays!!.toList())
                    } ?: apply {
                        // TODO: Show error message
                    }
                }
            }
        } else {
            val positionCategory =
                categoryArrayAdapter.getPosition(vm.submitTimeslot.value?.category)
            val positionRepetition =
                max(repetitionsArrayAdapter.getPosition(vm.submitTimeslot.value?.repetition), 0)
            binding.tvCategory.setText(
                categoryArrayAdapter.getItem(positionCategory).toString(),
                false
            )
            binding.tvCategory.onItemClickListener =
                OnItemClickListener { _, _, idx, _ -> vm.setSubmitTimeslotFields(category = categories[idx]) }
            // dropdown menu
            binding.tvRepetition.setText(
                repetitionsArrayAdapter.getItem(positionRepetition).toString(), false
            )
            // dropdown menu
            binding.tvRepetition.onItemClickListener =
                OnItemClickListener { _, _, idx, _ -> vm.setSubmitTimeslotFields(repetition = repetitions[idx]) }
            binding.bSubmit.apply { visibility = View.VISIBLE }

            val oldDays = vm.submitTimeslot.value?.days?.toMutableSet()
            chips.forEachIndexed { idx, chip ->
                chip.setOnClickListener {
                    if (oldDays?.contains(idx + 1) == true && oldDays.size > 1)
                        oldDays.remove(idx + 1)
                    else
                        oldDays?.add(idx + 1)
                    vm.setSubmitTimeslotFields(days = oldDays?.toList())
                }
            }
        }

        // show start startDate picker dialog
        binding.tvStartDate.setOnClickListener {
            DatePickerFragment(binding.tvStartDate, vm, DatePickerFragment.DType.START, tId)
                .show(requireActivity().supportFragmentManager, "startDatePicker")
        }
        // show end date picker dialog
        binding.tvEndDate.setOnClickListener {
            DatePickerFragment(binding.tvEndDate, vm, DatePickerFragment.DType.END, tId)
                .show(requireActivity().supportFragmentManager, "endDatePicker")
        }
        // show start time picker dialog
        binding.tvStartTime.setOnClickListener {
            TimePickerFragment(binding.tvStartTime, vm, TimePickerFragment.Type.START, tId)
                .show(requireActivity().supportFragmentManager, "startTimePicker")
        }
        // show end time picker dialog
        binding.tvEndTime.setOnClickListener {
            TimePickerFragment(binding.tvEndTime, vm, TimePickerFragment.Type.END, tId)
                .show(requireActivity().supportFragmentManager, "endTimePicker")
        }
        binding.swRepetition.setOnClickListener {
            if (tId != null) { // tId is null if new timeslot
                // if the switch is checked, set repetition to weekly
                val repetition = if (binding.swRepetition.isChecked) "Weekly" else null
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "repetition", repetition)
                } ?: apply {
                    // TODO: Show error message
                }
            } else {
                // if the switch is checked, set repetition to weekly
                // to set repetition to null in submitTimeslot you need to pass empty string
                val repetition = if (binding.swRepetition.isChecked) "Weekly" else ""
                vm.setSubmitTimeslotFields(repetition = repetition)
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
        // edit existing timeslot
        if (tId != null) {
            vm.timeslots.observe(viewLifecycleOwner) { it ->
                val t: Timeslot = it.elementAt(tId!!)
                binding.tilTitle.editText?.setText(t.title)
                binding.tilDescription.editText?.setText(t.description)
                binding.tilLocation.editText?.setText(t.location)
                binding.tvStartDate.text = Utils.formatDateToString(t.startDate)
                binding.tvStartTime.text = t.startHour
                binding.tvEndTime.text = t.endHour
                binding.tvEndDate.text =
                    if (t.startDate.before(t.endRepetitionDate) || t.startDate == t.endRepetitionDate)
                        Utils.formatDateToString(t.endRepetitionDate)
                    else
                        Utils.formatDateToString(t.startDate)
                val positionCategory = categoryArrayAdapter.getPosition(t.category)
                val positionRepetition = max(repetitionsArrayAdapter.getPosition(t.repetition), 0)
                binding.tvCategory.setText(
                    categoryArrayAdapter.getItem(positionCategory).toString(), false
                )
                binding.tvRepetition.setText(
                    repetitionsArrayAdapter.getItem(positionRepetition).toString(), false
                )
                binding.swRepetition.isChecked = t.repetition != null
                chips.forEachIndexed { idx, chip -> chip.isChecked = t.days.any{it == idx+1}}
                if (vm.isValid(t))
                    notifySuccess(true)
                else
                    notifySuccess(false)
                setRepetitionComponentsVisibility(t.repetition)
            }
        }
        // add new timeslot
        else {
            vm.submitTimeslot.observe(viewLifecycleOwner) { ts ->
                binding.tvStartDate.text = Utils.formatDateToString(ts.startDate)
                binding.tvStartTime.text = ts.startHour
                binding.tvEndTime.text = ts.endHour
                binding.tvEndDate.text =
                    if (ts.startDate.before(ts.endRepetitionDate) || ts.startDate == ts.endRepetitionDate)
                        Utils.formatDateToString(ts.endRepetitionDate)
                    else
                        Utils.formatDateToString(ts.startDate)
                val positionCategory = categoryArrayAdapter.getPosition(ts.category)
                binding.tvCategory.setText(
                    categoryArrayAdapter.getItem(positionCategory).toString(), false
                )
                chips.forEachIndexed { idx, chip -> chip.isChecked = ts.days.any{it == idx+1}}
                setRepetitionComponentsVisibility(ts.repetition)
                binding.bSubmit
                    .apply { isEnabled = vm.isValid(ts) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRepetitionComponentsVisibility(repetition: String?) {
        binding.lEndDate.visibility = if (repetition != null) View.VISIBLE else View.INVISIBLE
        binding.cvEndDate.visibility = if (repetition != null) View.VISIBLE else View.INVISIBLE
        binding.lRepeat.visibility = if (repetition != null) View.VISIBLE else View.GONE
        binding.dmRepetition.visibility = if (repetition != null) View.VISIBLE else View.GONE
        binding.cgDays.visibility = if (repetition == "Weekly") View.VISIBLE else View.GONE
    }

    // use only when the timeslot exists
    private fun addFocusChangeListeners() {
        binding.teTitle.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.title.toString()
            val new = binding.teTitle.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new && new.isNotBlank()) {
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "title", new)
                } ?: apply {
                    // TODO: Show error message
                }
            }
        }
        binding.teDescription.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.description.toString()
            val new = binding.teDescription.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new) {
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "description", new)
                } ?: apply {
                    // TODO: Show error message
                }
            }
        }
        binding.teLocation.setOnFocusChangeListener { _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.location.toString()
            val new = binding.teLocation.text.toString()
            if (new.isBlank())
                notifySuccess(false)
            if (!focused && old != new && new.isNotBlank()) {
                vm.timeslots.value?.elementAt(tId!!)?.apply {
                    vm.updateTimeslotField(this.tid, "location", new)
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
                vm.setSubmitTimeslotFields(title = s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.teDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                vm.setSubmitTimeslotFields(description = p0.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.teLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                vm.setSubmitTimeslotFields(location = p0.toString())
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