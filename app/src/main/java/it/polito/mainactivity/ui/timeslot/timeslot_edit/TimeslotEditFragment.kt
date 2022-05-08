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
import android.widget.AdapterView.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
    private var timeslotId: Int? = null
    private var submitTimeslot: Timeslot? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var days: List<Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        timeslotId = arguments?.getInt("id", -1)
        timeslotId = if(timeslotId == -1) null else timeslotId

        // edit existing timeslot
        if(timeslotId != null){
            vm.timeslots.observe(viewLifecycleOwner) {
                val timeslot: Timeslot = it.elementAt(timeslotId!!)
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
            addFocusChangeListeners()
        } else {
            // populate fields of timeslot to be submitted
            submitTimeslot =
                Timeslot("Submit!", "", GregorianCalendar.getInstance(), "", "", "", "", null, listOf(), null)
        }
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
        if(timeslotId == null){
            // populate fields
            tiTitle?.editText?.setText(submitTimeslot?.title)
            tiDescription?.editText?.setText(submitTimeslot?.description)
            tiStartDate?.text = Utils.formatDateToString(submitTimeslot?.date)
            tiStartTime?.text = submitTimeslot?.startHour
            tiEndTime?.text = submitTimeslot?.endHour
            tiLocation?.editText?.setText(submitTimeslot?.location)
            // TODO check category set to dropdown menu
            binding.bSubmit
                .apply{ visibility = View.VISIBLE }
                .setOnClickListener{ submit() }
        }
        val categories = resources.getStringArray(R.array.skills_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
        binding.dmCategory.setAdapter(arrayAdapter)
        binding.dmCategory.onItemClickListener = OnItemClickListener{ _, _, idx, _ -> submitTimeslot?.category =
            categories[idx]
        }
    }

    private fun submit(){
        submitTimeslot?.title = binding.tilTitle.editText?.text.toString()
        submitTimeslot?.description = binding.tilDescription.editText?.text.toString()
        submitTimeslot?.location = binding.tilLocation.editText?.text.toString()
        // add new timeslot
        vm.addTimeslot(submitTimeslot)
        // navigate back
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // use only when the timeslot exists
    private fun addFocusChangeListeners() {
        binding.TextInputEditTitle.setOnFocusChangeListener{ _, focused ->
                                                       val old = vm.timeslots.value?.elementAt(timeslotId!!)?.title.toString()
                                                       val new = binding.TextInputEditTitle.text.toString()
                                                       if(!focused && old != new){
                                                       val oldTimeslots = vm.timeslots.value
                                                       val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(title = new) else ts}
                                                       vm.setTimeslots(newTimeslots)
                                                       }
                                                       }
        binding.TextInputEditDescription.setOnFocusChangeListener{ _, focused ->
                                                             val old = vm.timeslots.value?.elementAt(timeslotId!!)?.description.toString()
                                                             val new = binding.TextInputEditDescription.text.toString()
                                                             if(!focused && old != new){
                                                             val oldTimeslots = vm.timeslots.value
                                                             val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(description = new) else ts}
                                                             vm.setTimeslots(newTimeslots)
                                                             }
                                                             }
        binding.TextInputEditLocation.setOnFocusChangeListener{ _, focused ->
                                                          val old = vm.timeslots.value?.elementAt(timeslotId!!)?.location.toString()
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
                             private val vm: TimeslotViewModel,
                             private val type: Type,
                             private val timeslotId: Int? = null,
                             private val ts: Timeslot? = null) : DialogFragment(),TimePickerDialog.OnTimeSetListener {

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
            if(timeslotId != null){
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
            }
            else {
                if(ts != null){
                    if(type == Type.START){
                        ts.startHour = timeText
                    }
                    else if(type == Type.END){
                        ts.endHour = timeText
                    }
                }
            }
            tiTime?.text = timeText
        }
    }

    // START TIME
    private fun showStartTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiStartTime, vm, TimePickerFragment.Type.START, timeslotId, submitTimeslot)
        timeFragment.show(requireActivity().supportFragmentManager, "startTimePicker")
    }

    // END TIME
    private fun showEndTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiEndTime, vm, TimePickerFragment.Type.END, timeslotId, submitTimeslot)
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
        private var ts: Timeslot? = null

        private lateinit var d: DatePickerDialog

        constructor(_tiDate: TextView?, _vm: TimeslotViewModel, _type: DType, _timeslotId: Int? = null, _timeslot: Timeslot? = null): this(){
            tiDate = _tiDate
            vm = _vm
            type = _type
            timeslotId = _timeslotId
            ts = _timeslot
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            if(timeslotId != null) {
                //timeslot already exists
                d = if(type == DType.END && vm?.timeslots?.value?.get(timeslotId!!)?.repetition != ""){
                    DatePickerDialog(requireContext(), this,
                        vm?.timeslots?.value?.get(timeslotId!!)?.endRepetitionDate!!.get(Calendar.YEAR),
                        vm?.timeslots?.value?.get(timeslotId!!)?.endRepetitionDate!!.get(Calendar.MONTH),
                        vm?.timeslots?.value?.get(timeslotId!!)?.endRepetitionDate!!.get(Calendar.DAY_OF_MONTH))
                } else {
                    DatePickerDialog(requireContext(), this,
                        vm?.timeslots?.value?.get(timeslotId!!)?.date!!.get(Calendar.YEAR),
                        vm?.timeslots?.value?.get(timeslotId!!)?.date!!.get(Calendar.MONTH),
                        vm?.timeslots?.value?.get(timeslotId!!)?.date!!.get(Calendar.DAY_OF_MONTH))
                }
                if(type == DType.END) {
                    //it MUST have a date
                    d.datePicker.minDate =
                        vm?.timeslots?.value?.get(id)?.date?.timeInMillis!!
                } else{
                    d.datePicker.minDate = c.timeInMillis
                }
            } else {
                d = DatePickerDialog(requireContext(), this, year, month, day)
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
            val date: Calendar = GregorianCalendar(year, month, day)
            if(timeslotId != null){
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
            }
            else {
                if(type == DType.START){
                    CHOSEN_DATE = GregorianCalendar(year, month, day)
                    ts?.date = date
                }
                else if(type == DType.END) {
                    ts?.endRepetitionDate = date
                }
            }
            tiDate?.text = new
        }
    }

    // START DATE
    private fun showDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiStartDate, vm, DatePickerFragment.DType.START, timeslotId, submitTimeslot)
        val bundle = Bundle()
        bundle.putInt("id", requireArguments().getInt("id"))
        dateFragment.arguments = bundle
        dateFragment.show(requireActivity().supportFragmentManager, "startDatePicker")
    }

    // END DATE
    private fun showEndDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiEndDate, vm, DatePickerFragment.DType.END, timeslotId, submitTimeslot)
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
        if(timeslotId != null){
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
            val selectedDays: MutableList<Int> = mutableListOf()
            chips.forEachIndexed{ idx, c -> if(c.isChecked) selectedDays.add(idx + 1) }
            val repetitionType = autoCompleteTextView?.text.toString()
            if(timeslotId != null){
                // check selected days chips
                val timeslot = vm.timeslots.value?.get(timeslotId!!)
                val oldTimeslots = vm.timeslots.value
                if(timeslot?.repetition != repetitionType || !timeslot.days.containsAll(selectedDays) || !selectedDays.containsAll(timeslot.days)){
                    val newTimeslots = oldTimeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) ts.copy(repetition = repetitionType, days = selectedDays) else ts}
                    vm.setTimeslots(newTimeslots)
                }
                //TODO CHECK THAT END DATE IS PRESENT
                //TODO CHECK THAT ONE CHIP HAS BEEN SELECTED
                mAlertDialog?.dismiss() //close dialog
            }
            else {
                submitTimeslot?.repetition = repetitionType
                submitTimeslot?.days = selectedDays
                // TODO category
            }
        }
    }

}