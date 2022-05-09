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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel

//TODO: CHECK BEFORE SAVE, BLOCK BACK NAVIGATION

class TimeslotEditFragment : Fragment() {

    // Extending DialogFragment for a time picker
    class TimePickerFragment(private val tiTime: TextView?,
                             private val vm: TimeslotViewModel,
                             private val type: Type,
                             private val tId: Int? = null) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        enum class Type {
            START, END
        }

        @RequiresApi(Build.VERSION_CODES.N)
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
        override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
            val timeText = Utils.formatTime(hourOfDay, minute)
            // modify existing timeslot
            if(tId != null){
                // TODO refactor
                val old = tiTime?.text
                val new = timeText
                if(type == Type.START && old != new){
                    val oldTimeslots = vm.timeslots.value
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(startHour = new) else ts}
                    vm.setTimeslots(newTimeslots)
                }
                else if(type == Type.END && old != new) {
                    val oldTimeslots = vm.timeslots.value
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(endHour = new) else ts}
                    vm.setTimeslots(newTimeslots)
                }
            }
            // new timeslot
            else {
                if(vm.submitTimeslot.value != null){
                    if(type == Type.START){
                        vm.setSubmitFields(startHour = timeText)
                        // if start hour is after end hour, set end hour to start hour
                        if(timeText.compareTo(vm.submitTimeslot.value!!.endHour.toString()) > 0){
                            vm.setSubmitFields(endHour = timeText)
                        }
                    }
                    else if(type == Type.END){
                        if(timeText.compareTo(vm.submitTimeslot.value!!.startHour) >= 0)
                            vm.setSubmitFields(endHour = timeText)
                        else {
                            // display snackbar when end time is before start time
                            val s: Snackbar = Snackbar.make(requireActivity().findViewById(R.id.drawer_layout), "ERROR: End time must be after start time!", Snackbar.LENGTH_LONG)
                            s.setTextColor(Color.parseColor("#ffff00"))
                            s.show()
                        }
                    }
                }
            }
        }
    }

    // Extending DialogFragment for a date picker
    class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

        enum class DType {
            START, END
        }

        private var tiDate: TextView? = null
        private var vm: TimeslotViewModel? = null
        private var type: DType? = null
        private var tId: Int? = null

        private lateinit var dialog: DatePickerDialog

        constructor(_tiDate: TextView?, _vm: TimeslotViewModel, _type: DType, _timeslotId: Int? = null): this(){
            tiDate = _tiDate
            vm = _vm
            type = _type
            tId = _timeslotId
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            // val c = Calendar.getInstance()
            val startDate = if(tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.date else vm?.submitTimeslot?.value?.date
            val endDate = if(tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.endRepetitionDate else vm?.submitTimeslot?.value?.endRepetitionDate
            val repetition = if(tId != null) vm?.timeslots?.value?.elementAt(tId!!)?.repetition else vm?.submitTimeslot?.value?.repetition

            val year = if(type == DType.END && repetition != null) endDate?.get(Calendar.YEAR) else startDate?.get(Calendar.YEAR)
            val month = if(type == DType.END && repetition != null) endDate?.get(Calendar.MONTH) else startDate?.get(Calendar.MONTH)
            val day = if(type == DType.END && repetition != null) endDate?.get(Calendar.DAY_OF_MONTH) else startDate?.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            dialog = DatePickerDialog(requireContext(), this, year!!, month!!, day!!)
            if(type == DType.END)
                dialog.datePicker.minDate = startDate!!.timeInMillis
            else if(type == DType.START)
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
            if(tId != null){
                if(type == DType.START && old != new){
                    val oldTimeslots = vm?.timeslots?.value
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(date = date) else ts}
                    vm?.setTimeslots(newTimeslots)
                }
                else if (type == DType.END && old != new){
                    val oldTimeslots = vm?.timeslots?.value
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(endRepetitionDate = date) else ts}
                    vm?.setTimeslots(newTimeslots)
                }
            }
            // if new timeslot
            else {
                if(type == DType.START){
                    vm?.setSubmitFields(date = date)
                }
                else if(type == DType.END)
                    vm?.setSubmitFields(endRepetitionDate = date)
            }
            // TODO modify move up in observe
            tiDate?.text = new
        }
    }

    private val vm:TimeslotViewModel by activityViewModels()

    private var _binding: FragmentTimeslotEditBinding? = null
    private val binding get() = _binding!!
    private var cgWeekDays: ChipGroup? = null
    private var mAlertDialog: AlertDialog? = null
    private var days: List<Int>? = null
    private var tId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tId = arguments?.getInt("id", -1)
        tId = if(tId == -1) null else tId

        // edit existing timeslot
        if(tId != null){
            vm.timeslots.observe(viewLifecycleOwner) {
                val timeslot: Timeslot = it.elementAt(tId!!)
                binding.tilTitle.editText?.setText(timeslot.title)
                binding.tilDescription.editText?.setText(timeslot.description)
                binding.tilLocation.editText?.setText(timeslot.location)
                binding.tvStartDate.text = Utils.formatDateToString(timeslot.date)
                binding.tvStartTime.text = timeslot.startHour
                binding.tvEndTime.text = timeslot.endHour
                requireActivity().findViewById<TextView>(R.id.tvEndDate)?.apply{ text = Utils.formatDateToString(timeslot.endRepetitionDate) }
                days = timeslot.days
            }
            addFocusChangeListeners()
        } else {
            // if new timeslot (to be added)
            vm.submitTimeslot.observe(viewLifecycleOwner){
                binding.tvStartDate.text = Utils.formatDateToString(it.date)
                binding.tvStartTime.text = it.startHour
                binding.tvEndTime.text = it.endHour
                val tv = requireActivity().findViewById<TextView>(R.id.tvEndDate)
                tv?.apply{ text = Utils.formatDateToString(it.endRepetitionDate) }
                binding.bSubmit
                    .apply{ visibility = View.VISIBLE }
                    .apply{ isEnabled = vm.isValid(it) }
            }
            addTextChangedListeners()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO refactor do not use variables
        cgWeekDays = view.findViewById(R.id.cgDays)
        // show start date picker dialog
        binding.tvStartDate.setOnClickListener {
            DatePickerFragment(binding.tvStartDate, vm, DatePickerFragment.DType.START, tId)
                .show(requireActivity().supportFragmentManager, "startDatePicker") }
        // show start time picker dialog
        binding.tvStartTime.setOnClickListener {
            TimePickerFragment(binding.tvStartTime, vm, TimePickerFragment.Type.START, tId)
                .show(requireActivity().supportFragmentManager, "startTimePicker") }
        // show end time picker dialog
        binding.tvEndTime.setOnClickListener {
            val timeFragment = TimePickerFragment(binding.tvEndTime, vm, TimePickerFragment.Type.END, tId)
            timeFragment.show(requireActivity().supportFragmentManager, "endTimePicker")
        }
        binding.bSetRepetition.setOnClickListener { showRepetitionDialog() }
        binding.bSubmit.setOnClickListener{ if(vm.submitTimeslot()) findNavController().navigateUp() }

        val categories = resources.getStringArray(R.array.skills_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
        binding.dmCategory.setAdapter(arrayAdapter)
        if(tId != null){
            val positionCategory = arrayAdapter.getPosition(vm.timeslots.value?.elementAt(tId!!)?.category)
            binding.dmCategory.setText(arrayAdapter.getItem(positionCategory).toString(), false)
            binding.dmCategory.onItemClickListener = OnItemClickListener{ _, _, idx, _ -> vm.timeslots.value?.get(tId!!)?.category = categories[idx] }
        }
        else {
            val positionCategory = arrayAdapter.getPosition(vm.submitTimeslot.value?.category)
            binding.dmCategory.setText(arrayAdapter.getItem(positionCategory).toString(), false)
            binding.dmCategory.onItemClickListener = OnItemClickListener{ _, _, idx, _ -> vm.setSubmitFields(category = categories[idx]) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // use only when the timeslot exists
    private fun addFocusChangeListeners() {
        binding.teTitle.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.title.toString()
            val new = binding.teTitle.text.toString()
            if(!focused && old != new && new.isNotBlank()){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(title = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
        binding.teDescription.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.description.toString()
            val new = binding.teDescription.text.toString()
            if(!focused && old != new){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(description = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
        binding.teLocation.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(tId!!)?.location.toString()
            val new = binding.teLocation.text.toString()
            if(!focused && old != new && new.isNotBlank()){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(location = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
    }

    // use only when the timeslot is new
    private fun addTextChangedListeners() {
        val t = if(tId != null) vm.timeslots.value?.elementAt(tId!!) else vm.submitTimeslot.value
        val oldTimeslots = vm.timeslots.value
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

    // REPETITION
    private fun showRepetitionDialog() {

        // TODO check
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_repetition, null)
        val mBuilder = AlertDialog.Builder(requireContext()).setView(mDialogView)

         val repetitions = resources.getStringArray(R.array.repetitionMw)
         val chips: MutableList<Chip> = mutableListOf(
            mDialogView.findViewById(R.id.S0),
            mDialogView.findViewById(R.id.M1),
            mDialogView.findViewById(R.id.T2),
            mDialogView.findViewById(R.id.W3),
            mDialogView.findViewById(R.id.T4),
            mDialogView.findViewById(R.id.F5),
            mDialogView.findViewById(R.id.S6)
        )

        cgWeekDays = mDialogView.findViewById(R.id.cgDays)
        val tvEndDate = mDialogView.findViewById<TextView>(R.id.tvEndDate)
        val tvRepetition = mDialogView?.findViewById<AutoCompleteTextView>(R.id.tvRepetition)
        val lRepeatOn = mDialogView?.findViewById<TextView>(R.id.lRepeatOn)

        // required to put weekly / monthly
        val repetitionsArrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, repetitions)
        tvRepetition?.setAdapter(repetitionsArrayAdapter)
        tvRepetition?.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                if(position == 0){ //weekly
                    lRepeatOn?.visibility = View.VISIBLE
                    cgWeekDays?.visibility= View.VISIBLE
                } else{
                    lRepeatOn?.visibility = View.GONE
                    cgWeekDays?.visibility= View.GONE
                }
            }

        // select right value inside dropdown (monthly/weekly)
        if(tId != null){
            if(vm.timeslots.value?.get(tId!!)?.repetition == "weekly"){
                tvRepetition?.setText(repetitionsArrayAdapter.getItem(0).toString(), false)
            }
            if(vm.timeslots.value?.get(tId!!)?.repetition == "monthly"){
                tvRepetition?.setText(repetitionsArrayAdapter.getItem(1).toString(), false)
                lRepeatOn?.visibility = View.GONE
                cgWeekDays?.visibility= View.GONE
            }
            if (vm.timeslots.value?.get(tId!!)?.repetition != "") {
                days?.forEach {
                    val chip = cgWeekDays?.getChildAt(it - 1) as Chip
                    cgWeekDays?.check(chip.id)
                }
                // tvEndDate?.text = vm.timeslots.value?.get(tId!!)?.dateFormat?.format(vm.timeslots.value?.get(tId)?.endRepetitionDate!!.time)
            }
        }
        else {
            tvEndDate.text = Utils.formatDateToString(vm.submitTimeslot.value?.date)
        }

        tvEndDate?.setOnClickListener{
            DatePickerFragment(view?.findViewById(R.id.tvEndDate), vm, DatePickerFragment.DType.END, tId)
                .show(requireActivity().supportFragmentManager,"endDatePicker") }


        mAlertDialog = mBuilder.show()

        //Attaching the corresponding functions to close and save buttons
        val closeButton = mDialogView.findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener {
            mAlertDialog?.dismiss()
        }

        val saveButton = mDialogView.findViewById<Button>(R.id.bSave)
        saveButton.setOnClickListener {
            val selectedDays: MutableList<Int> = mutableListOf()
            chips.forEachIndexed{ idx, c -> if(c.isChecked) selectedDays.add(idx + 1) }
            val repetitionType = tvRepetition?.text.toString()
            val oldTimeslots = vm.timeslots.value
            val timeslot = if(tId == null) vm.timeslots.value?.get(tId!!) else vm.submitTimeslot.value
            if(tId != null){
                // check selected days chips
                if(timeslot?.repetition != repetitionType || !timeslot.days.containsAll(selectedDays) || !selectedDays.containsAll(timeslot.days)){
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == tId) ts.copy(repetition = repetitionType, days = selectedDays) else ts}
                    vm.setTimeslots(newTimeslots)
                }
            }
            else {
                if(timeslot?.repetition != repetitionType || !timeslot.days.containsAll(selectedDays) || !selectedDays.containsAll(timeslot.days))
                    vm.setSubmitFields(repetition = repetitionType, days = selectedDays)
            }
            //TODO CHECK THAT END DATE IS PRESENT
            //TODO CHECK THAT ONE CHIP HAS BEEN SELECTED
            mAlertDialog?.dismiss() //close dialog
        }
    }

}