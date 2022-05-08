package it.polito.mainactivity.ui.timeslot.timeslot_edit

import android.annotation.SuppressLint
import android.app.*
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.model.Timeslot
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
    private var tiCategory: ChipGroup? = null
    private var tiDays: ChipGroup? = null
    private var mAlertDialog: AlertDialog? = null

    private var timeslotId: Int = -1

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var days: List<Int>? = null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        timeslotId = arguments?.getInt("id") ?: -1
        // edit existing timeslot
        if(timeslotId != -1){
            vm.timeslots.observe(viewLifecycleOwner) {
                val timeslot: Timeslot = it.elementAt(timeslotId)
                tiTitle?.editText?.setText(timeslot.title)
                tiDescription?.editText?.setText(timeslot.description)
                tiStartDate?.text = timeslot.dateFormat.format(timeslot.date.time)
                tiStartTime?.text = timeslot.startHour
                tiEndTime?.text = timeslot.endHour
                tiLocation?.editText?.setText(timeslot.location)

                val categories: List<String> = resources.getStringArray(R.array.skills_array).toList()
                val index = categories.indexOf(timeslot.category)
                val chip : Chip = tiCategory?.getChildAt(index) as Chip
                tiCategory?.check(chip.id)

                days = timeslot.days
            }
        } else {
        }

        addFocusChangeListeners()

        vm.updated.observe(viewLifecycleOwner){
            val timeslots = vm.timeslots.value
            if(it != null){
                val updatedTimeslots = timeslots?.mapIndexed{ idx, ts -> if(idx == timeslotId) it else ts}
                vm.setTimeslots(updatedTimeslots)
                vm.resetUpdated()
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tiTitle = view.findViewById(R.id.TitleTextField)
        tiDescription = view.findViewById(R.id.DescriptionTextField)
        tiStartDate = view.findViewById(R.id.tv_timeslotEdit_date)
        tiStartTime = view.findViewById(R.id.tv_timeslotEdit_startTime)
        tiEndTime = view.findViewById(R.id.tv_timeslotEdit_endTime)
        tiLocation = view.findViewById(R.id.LocationTextField)
        tiCategory = view.findViewById(R.id.chips_group)
        tiDays = view.findViewById(R.id.days)
        val btnDate = view.findViewById<MaterialButton>(R.id.edit_startDate)
        btnDate.setOnClickListener { showDatePickerDialog() }
        val btnStartTime = view.findViewById<MaterialButton>(R.id.edit_startTime)
        btnStartTime.setOnClickListener { showStartTimePickerDialog() }
        val btnEndTime = view.findViewById<MaterialButton>(R.id.edit_endTime)
        btnEndTime.setOnClickListener { showEndTimePickerDialog() }
        val btnRepetition = view.findViewById<Button>(R.id.edit_repetition)
        btnRepetition.setOnClickListener { showRepetitionDialog() }
        val btnEndDate = view.findViewById<MaterialCardView>(R.id.end_rep_date)
        btnEndDate?.setOnClickListener { showEndDatePickerDialog() }
        val id: Int = arguments?.getInt("id") ?: -1
        if(id == -1){
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

    // Extending DialogFragment for a date picker
    class DatePickerFragment() : DialogFragment(),DatePickerDialog.OnDateSetListener {

        private var tiDate: TextView? = null
        private val timeSlotListViewModel:TimeslotViewModel by activityViewModels()

        private lateinit var d:DatePickerDialog

        constructor(_tiDate: TextView?):this(){
            tiDate=_tiDate
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
                d = if(this.tag.toString() == "endDatePicker" && timeSlotListViewModel.timeslots.value?.get(id)?.repetition!= ""){
                    DatePickerDialog(requireContext(), this,
                        timeSlotListViewModel.timeslots.value?.get(id)?.endRepetitionDate!!.get(Calendar.YEAR),
                        timeSlotListViewModel.timeslots.value?.get(id)?.endRepetitionDate!!.get(Calendar.MONTH),
                        timeSlotListViewModel.timeslots.value?.get(id)?.endRepetitionDate!!.get(Calendar.DAY_OF_MONTH))
                }else{
                    DatePickerDialog(requireContext(), this,
                        timeSlotListViewModel.timeslots.value?.get(id)?.date!!.get(Calendar.YEAR),
                        timeSlotListViewModel.timeslots.value?.get(id)?.date!!.get(Calendar.MONTH),
                        timeSlotListViewModel.timeslots.value?.get(id)?.date!!.get(Calendar.DAY_OF_MONTH))
                }
                if( this.tag.toString() == "endDatePicker") {
                    //it MUST have a date
                    d.datePicker.minDate =
                        timeSlotListViewModel.timeslots.value?.get(id)?.date?.timeInMillis!!
                }else{
                    d.datePicker.minDate = c.timeInMillis
                }
            } else {
                d = DatePickerDialog( requireContext(), this, year, month, day)
                //the timeslot is new!
                if(this.tag.toString() == "datePicker"){
                    d.datePicker.minDate = c.timeInMillis
                    //saving the global variable to check the end date
                    CHOSEN_DATE = c.clone() as Calendar
                }else{
                    d.datePicker.minDate = CHOSEN_DATE!!.timeInMillis
                }
            }
            return d
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            tiDate?.text = "${day}/${(month + 1)}/${year}"
           if(requireArguments().getInt("id")== -1 &&  this.tag.toString() == "datePicker"){
               CHOSEN_DATE = GregorianCalendar(year, month, day)
         }
        }

    }

    // START DATE
    private fun showDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiStartDate)
        val bundle = Bundle()
        bundle.putInt("id", requireArguments().getInt("id"))
        dateFragment.arguments = bundle
        dateFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    // END DATE
    private fun showEndDatePickerDialog() {
       val dateFragment = DatePickerFragment(tiEndDate)
       val bundle = Bundle()
       bundle.putInt("id", requireArguments().getInt("id"))
        dateFragment.arguments = bundle
        dateFragment.show(requireActivity().supportFragmentManager, "endDatePicker")
    }

    // Extending DialogFragment for a time picker
    class TimePickerFragment(private val tiTime: TextView?) : DialogFragment(),TimePickerDialog.OnTimeSetListener {

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
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            if (minute < 10) {
                tiTime?.text = "$hourOfDay:0$minute"
            } else {
                tiTime?.text = "$hourOfDay:$minute"
            }
        }
    }

    // START TIME
    private fun showStartTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiStartTime)
        timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
    }

    // END TIME
    private fun showEndTimePickerDialog() {
        val timeFragment = TimePickerFragment(tiEndTime)
        timeFragment.show(requireActivity().supportFragmentManager, "timePicker")
    }

    // REPETITION
    private fun showRepetitionDialog() {
        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.modal_repetition, null)

        tiDays = mDialogView.findViewById(R.id.days)
        tiEndDate = mDialogView?.findViewById(R.id.tv_timeslotEdit_endDate)

        val repetitions = resources.getStringArray(R.array.repetition_mw)
        val repeatOn = mDialogView?.findViewById<TextView>(R.id.repeat_on)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, repetitions)
        val autoCompleteTextView =
            mDialogView?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView?.setAdapter(arrayAdapter)
        autoCompleteTextView?.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                if(position == 0){ //weekly
                    repeatOn?.visibility = View.VISIBLE
                    tiDays?.visibility= View.VISIBLE
                }else{
                    repeatOn?.visibility = View.GONE
                    tiDays?.visibility= View.GONE
                }
            }

        if(arguments?.getInt("timeslotId")!= -1){
            if(vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition == "weekly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(0).toString(), false)
            }
            if(vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition == "monthly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(1).toString(), false)
                repeatOn?.visibility = View.GONE
                tiDays?.visibility= View.GONE
            }
            if (vm.timeslots.value?.get(requireArguments().getInt("timeslotId"))?.repetition != "") {
                days?.forEach {
                    val chip = tiDays?.getChildAt(it - 1) as Chip
                    tiDays?.check(chip.id)
                }

                tiEndDate?.text = timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.dateFormat?.format(timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.endRepetitionDate!!.time)
            }
        }
        tiEndDate?.setOnClickListener{ showEndDatePickerDialog()}

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
            /*
            //get the current state of radioButtonYES. then update the skill's status and description
            s.active = radioButtonYES.isChecked
            s.description = description.text.toString()
            val desc = card.findViewById<TextView>(R.timeslotId.skillDescription)
            desc.text = s.description
            //update skills
            refreshSkills()*/
            //TODO CHECK THAT END DATE IS PRESENT
            //TODO CHECK THAT ONE CHIP HAS BEEN SELECTED
            mAlertDialog?.dismiss() //close dialog
        }
    }


}