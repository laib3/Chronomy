package it.polito.mainactivity.ui.timeslot.timeslot_edit

import android.annotation.SuppressLint
import android.app.*
import java.util.Calendar
import java.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel

//TODO: CHECK BEFORE SAVE, BLOCK BACK NAVIGATION

var CHOSEN_DATE: Calendar? = null

class TimeslotEditFragment : Fragment() {

    private val vm:TimeslotViewModel by activityViewModels()

    private var _binding: FragmentTimeslotEditBinding? = null

    private var tiTitle: TextInputLayout? = null
    private var tiDescription: TextInputLayout? = null
    private var tiStartDate: TextView? = null
    private var tiEndDate: TextView? = null
    private var tiStartTime: TextView? = null
    private var tiEndTime: TextView? = null
    private var tiLocation: TextInputLayout? = null
    private var cgCategory: ChipGroup? = null
    private var cgWeekDays: ChipGroup? = null
    private var mAlertDialog: AlertDialog? = null
    private var timeslotId: Int = -1
    private var submitTimeslot: Timeslot? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var days: List<Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        timeslotId = arguments?.getInt("id") ?: -1

        // edit existing timeslot
        if(timeslotId != -1){
            vm.timeslots.observe(viewLifecycleOwner) {
                val timeslot: Timeslot = it.elementAt(timeslotId)
                tiTitle?.editText?.setText(timeslot.title)
                tiDescription?.editText?.setText(timeslot.description)
                tiStartDate?.text = Utils.formatDateToString(timeslot.date)
                tiStartTime?.text = timeslot.startHour
                tiEndTime?.text = timeslot.endHour
                tiLocation?.editText?.setText(timeslot.location)
                // TODO change to dropdown menu
                val categories: List<String> = resources.getStringArray(R.array.skills_array).toList()
                val index = categories.indexOf(timeslot.category)
                val chip : Chip = cgCategory?.getChildAt(index) as Chip
                cgCategory?.check(chip.id)
                days = timeslot.days
            }
        } else {
            // populate fields of timeslot to be submitted
            submitTimeslot =
                Timeslot("Submit!", "", GregorianCalendar.getInstance(), "", "", "", "", null, listOf(), null)
        }

        addFocusChangeListeners()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO refactor do not use variables
        tiTitle = binding.tilTitle
        tiDescription = binding.tilDescription
        tiStartDate = binding.tvTimeslotStartDate
        tiStartTime = binding.tvTimeslotStartTime
        tiEndTime = binding.tvTimeslotEndTime
        tiLocation = binding.tilLocation
        cgCategory = binding.cgCategory
        cgWeekDays = view.findViewById(R.id.cgDays)
        val btnDate = binding.bStartDate
        val btnStartTime = binding.bStartTime
        val btnEndTime = binding.bEndTime
        val btnRepetition = binding.bSetRepetition
        val btnEndDate = view.findViewById<MaterialCardView>(R.id.cvEndDate)
        btnDate.setOnClickListener { showDatePickerDialog() }
        btnStartTime.setOnClickListener { showStartTimePickerDialog() }
        btnEndTime.setOnClickListener { showEndTimePickerDialog() }
        btnRepetition.setOnClickListener { showRepetitionDialog() }
        btnEndDate?.setOnClickListener { showEndDatePickerDialog() }
        val id: Int = arguments?.getInt("id") ?: -1
        if(id == -1){
            // populate fields
            tiTitle?.editText?.setText(submitTimeslot?.title)
            tiDescription?.editText?.setText(submitTimeslot?.description)
            tiStartDate?.text = Utils.formatDateToString(submitTimeslot?.date)
            tiStartTime?.text = submitTimeslot?.startHour
            tiEndTime?.text = submitTimeslot?.endHour
            tiLocation?.editText?.setText(submitTimeslot?.location)
            // TODO check category set to dropdown menu
            val saveBtnLayout = view.findViewById<LinearLayout>(R.id.buttonSaveLayout)
            //set the properties for button
            val btnSaveTimeslot = AppCompatButton(requireContext())
                .apply{
                    background = this.context.resources.getDrawable(R.drawable.rectangle_save_button)
                    setTextColor(this.context.resources.getColor(R.color.white))
                    setPadding(32,0,32,0)
                    text = "Save timeslot"
                    textSize = 20f
                }

            btnSaveTimeslot.layoutParams =
                GridLayoutManager.LayoutParams(
                    GridLayoutManager.LayoutParams.WRAP_CONTENT,
                    GridLayoutManager.LayoutParams.WRAP_CONTENT,
                    ).apply {
                    setMargins(0, 32, 0, 0)
                    }
            saveBtnLayout.addView(btnSaveTimeslot)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addFocusChangeListeners() {
        binding.TextInputEditTitle.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(timeslotId)?.title.toString()
            val new = binding.TextInputEditTitle.text.toString()
            if(!focused && old != new){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(title = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
        binding.TextInputEditDescription.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(timeslotId)?.description.toString()
            val new = binding.TextInputEditDescription.text.toString()
            if(!focused && old != new){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(description = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
        binding.TextInputEditLocation.setOnFocusChangeListener{ _, focused ->
            val old = vm.timeslots.value?.elementAt(timeslotId)?.location.toString()
            val new = binding.TextInputEditLocation.text.toString()
            if(!focused && old != new){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(location = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
        }
    }

    // Extending DialogFragment for a time picker
    class TimePickerFragment(private val tiTime: TextView?,
                             private val timeslotId: Int,
                             private val vm: TimeslotViewModel,
                             private val type: Type) : DialogFragment(),TimePickerDialog.OnTimeSetListener {

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
                DateFormat.is24HourFormat(activity)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
            val timeText = if(minute < 10) "$hourOfDay:0$minute" else "$hourOfDay:$minute"
            val old = tiTime?.text
            val new = timeText
            if(type == Type.START && old != new){
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(startHour = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
            else if(type == Type.END && old != new) {
                val oldTimeslots = vm.timeslots.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(endHour = new) else ts}
                vm.setTimeslots(newTimeslots)
            }
            // TODO change
            // set time in the view
            tiTime?.text = timeText
        }
    }

    // START TIME
    private fun showStartTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiStartTime, timeslotId, vm, TimePickerFragment.Type.START)
        timeFragment.show(requireActivity().supportFragmentManager, "startTimePicker")
    }

    // END TIME
    private fun showEndTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiEndTime, timeslotId, vm, TimePickerFragment.Type.END)
        timeFragment.show(requireActivity().supportFragmentManager, "endTimePicker")
    }

    // Extending DialogFragment for a date picker
    class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

        enum class DType {
            START, END
        }

        private var tiDate: TextView? = null
        private var vm: TimeslotViewModel? = null
        private var type: DType? = null
        private var timeslotId: Int? = null

        private lateinit var d: DatePickerDialog

        constructor(_tiDate: TextView?, _timeslotId: Int, _vm: TimeslotViewModel, _type: DType): this(){
            tiDate = _tiDate
            vm = _vm
            type = _type
            timeslotId = _timeslotId
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val id = requireArguments().getInt("id")

            // Create a new instance of DatePickerDialog and return it
            if(id!= -1 ) {
                //timeslot already exists
                d = if(type == DType.END && vm?.timeslots?.value?.get(id)?.repetition!= ""){
                    DatePickerDialog(requireContext(), this,
                        vm?.timeslots?.value?.get(id)?.endRepetitionDate!!.get(Calendar.YEAR),
                        vm?.timeslots?.value?.get(id)?.endRepetitionDate!!.get(Calendar.MONTH),
                        vm?.timeslots?.value?.get(id)?.endRepetitionDate!!.get(Calendar.DAY_OF_MONTH))
                } else{
                    DatePickerDialog(requireContext(), this,
                        vm?.timeslots?.value?.get(id)?.date!!.get(Calendar.YEAR),
                        vm?.timeslots?.value?.get(id)?.date!!.get(Calendar.MONTH),
                        vm?.timeslots?.value?.get(id)?.date!!.get(Calendar.DAY_OF_MONTH))
                }
                if(type == DType.END) {
                    //it MUST have a date
                    d.datePicker.minDate =
                        vm?.timeslots?.value?.get(id)?.date?.timeInMillis!!
                }else{
                    d.datePicker.minDate = c.timeInMillis
                }
            } else {
                d = DatePickerDialog( requireContext(), this, year, month, day)
                //the timeslot is new!
                if(type == DType.START){
                    d.datePicker.minDate = c.timeInMillis
                    //saving the global variable to check the end date
                    CHOSEN_DATE = c.clone() as Calendar
                } else{
                    d.datePicker.minDate = CHOSEN_DATE!!.timeInMillis
                }
            }
            return d
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val old = tiDate?.text
            val new = "${day}/${(month + 1)}/${year}"
            // if the timeslot is being created
            if(requireArguments().getInt("id") == -1 && type == DType.START){
                CHOSEN_DATE = GregorianCalendar(year, month, day)
            }
            val date: Calendar = GregorianCalendar(year, month, day)
            if(type == DType.START && old != new){
                val oldTimeslots = vm?.timeslots?.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(date = date) else ts}
                vm?.setTimeslots(newTimeslots)
            }
            else if (type == DType.END && old != new){
                val oldTimeslots = vm?.timeslots?.value
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(endRepetitionDate = date) else ts}
                vm?.setTimeslots(newTimeslots)
            }
            tiDate?.text = new
        }
    }

    // START DATE
    private fun showDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiStartDate, timeslotId, vm, DatePickerFragment.DType.START)
        val bundle = Bundle()
        bundle.putInt("id", requireArguments().getInt("id"))
        dateFragment.arguments = bundle
        dateFragment.show(requireActivity().supportFragmentManager, "startDatePicker")
    }

    // END DATE
    private fun showEndDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiEndDate, timeslotId, vm, DatePickerFragment.DType.END)
        val bundle = Bundle()
        bundle.putInt("id", requireArguments().getInt("id"))
        dateFragment.arguments = bundle
        dateFragment.show(requireActivity().supportFragmentManager,"endDatePicker")
    }

    // REPETITION
    private fun showRepetitionDialog() {

        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.modal_repetition, null)

        val chips: MutableList<Chip> = mutableListOf(
            mDialogView.findViewById(R.id.S0),
            mDialogView.findViewById(R.id.M1),
            mDialogView.findViewById(R.id.T2),
            mDialogView.findViewById(R.id.W3),
            mDialogView.findViewById(R.id.T4),
            mDialogView.findViewById(R.id.F5),
            mDialogView.findViewById(R.id.S6),
        )

        cgWeekDays = mDialogView.findViewById(R.id.cgDays)
        tiEndDate = mDialogView?.findViewById(R.id.tv_timeslotEdit_endDate)
        val autoCompleteTextView =
            mDialogView?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        val repetitions = resources.getStringArray(R.array.repetition_mw)
        val repeatOn = mDialogView?.findViewById<TextView>(R.id.repeat_on)

        // required to put weekly / monthly
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, repetitions)
        autoCompleteTextView?.setAdapter(arrayAdapter)
        autoCompleteTextView?.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                if(position == 0){ //weekly
                    repeatOn?.visibility = View.VISIBLE
                    cgWeekDays?.visibility= View.VISIBLE
                } else{
                    repeatOn?.visibility = View.GONE
                    cgWeekDays?.visibility= View.GONE
                }
            }

        // select right value inside dropdown (monthly/weekly)
        if(arguments?.getInt("timeslotId")!= -1){
            if(vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition == "weekly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(0).toString(), false)
            }
            if(vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition == "monthly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(1).toString(), false)
                repeatOn?.visibility = View.GONE
                cgWeekDays?.visibility= View.GONE
            }
            if (vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition != "") {
                days?.forEach {
                    val chip = cgWeekDays?.getChildAt(it - 1) as Chip
                    cgWeekDays?.check(chip.id)
                }

                tiEndDate?.text = vm.timeslots.value?.get(requireArguments().getInt("id"))?.dateFormat?.format(vm.timeslots.value?.get(requireArguments().getInt("id"))?.endRepetitionDate!!.time)
            }
        }

        tiEndDate?.setOnClickListener{ showEndDatePickerDialog() }

        //AlertDialogBuilder + show
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)

        mAlertDialog = mBuilder.show()

        //Attaching the corresponding functions to close and save buttons
        val closeButton = mDialogView.findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener {
            mAlertDialog?.dismiss()
        }

        val saveButton = mDialogView.findViewById<Button>(R.id.save_button)

        saveButton.setOnClickListener {
            // check selected days chips
            val selectedDays: MutableList<Int> = mutableListOf()
            chips.forEachIndexed{ idx, c -> if(c.isChecked) selectedDays.add(idx + 1) }
            val repetitionType = autoCompleteTextView?.text.toString()
            val timeslot = vm.timeslots.value?.get(timeslotId)
            val oldTimeslots = vm.timeslots.value
            if(timeslot?.repetition != repetitionType || !timeslot.days.containsAll(selectedDays) || !selectedDays.containsAll(timeslot.days)){
                val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(repetition = repetitionType, days = selectedDays) else ts}
                vm.setTimeslots(newTimeslots)
            }
            //TODO CHECK THAT END DATE IS PRESENT
            //TODO CHECK THAT ONE CHIP HAS BEEN SELECTED
            mAlertDialog?.dismiss() //close dialog
        }
    }

}