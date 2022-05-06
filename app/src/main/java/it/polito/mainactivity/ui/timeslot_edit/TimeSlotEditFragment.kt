package it.polito.mainactivity.ui.timeslot_edit

import android.annotation.SuppressLint
import android.app.*
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.ui.timeslot_details.TimeSlotDetailsViewModel
import it.polito.mainactivity.ui.timeslot_list.TimeSlotListFragment
import it.polito.mainactivity.ui.timeslot_list.TimeSlotListViewModel


class TimeSlotEditFragment : Fragment() {

    private val timeSlotDetailsViewModel: TimeSlotDetailsViewModel by activityViewModels()

    private var _binding: FragmentTimeslotEditBinding? = null

    private var tiTitle : TextInputLayout? = null
    private var tiDescription : TextInputLayout? = null
    private var tiAvailability : TextInputLayout? = null
    private var tiStartDate : TextView? = null
    private var tiEndDate : TextView? = null
    private var tiStartTime : TextView? = null
    private var tiEndTime : TextView? = null
    private var tiLocation : TextInputLayout? = null
    private var tiCategory : ChipGroup? = null
    private var typeRepetition : EditText? = null

    private var mAlertDialog : AlertDialog?=null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val timeSlotListViewModel =
            ViewModelProvider(this).get(TimeSlotListViewModel::class.java)

        _binding = FragmentTimeslotEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id") ?: -1

        //i don't need to observe anything, it will be a new timeslot
        if(id!= -1){
            timeSlotListViewModel.timeslots.observe(viewLifecycleOwner) {
                tiTitle?.editText?.setText(it.elementAt(id).title)
                tiDescription?.editText?.setText(it.elementAt(id).description)
                tiStartDate?.text = it.elementAt(id).dateFormat.format(it.elementAt(id).date.time)
                tiStartTime?.text = it.elementAt(id).startHour
                tiEndTime?.text = it.elementAt(id).endHour
                tiEndDate?.text = it.elementAt(id).dateFormat.format(it.elementAt(id).endRepetitionDate?.time)
                tiLocation?.editText?.setText(it.elementAt(id).location)
                //tiCategory?.editText?.setText(it.elementAt(id).category)
            }
        }else {
            /*val actionBar: ActionBar? = requireActivity().actionBar
            actionBar?.title = "New Timeslot"*/
           // activity?.title = "New Timeslot"
            //requireActivity().actionBar?.title="New Timeslot"
            
        }

        addFocusChangeListeners()

        /*
        val repetitions = resources.getStringArray(R.array.repetition_mw)
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.list_item, repetitions)
        //binding.setAdapter(arrayAdapter)

        //val items = listOf("Material", "Design", "Components", "Android")
        //val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)

        var autoCompleteTextView = mAlertDialog?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        //val textField = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
       // (textField?.text as? AutoCompleteTextView)?.setAdapter(adapter)

        autoCompleteTextView?.setAdapter(arrayAdapter)

       /* val typeRepetition = resources.getStringArray(R.array.repetition_mw)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.modal_repetition, typeRepetition)
        arrayAdapter.
        _binding.autoCompleteTextView.setAdapter(arrayAdapter)*/

        /*val textView: TextView = binding.textTimeslotDetails
        timeSlotDetailsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        */

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiTitle = view?.findViewById(R.id.TitleTextField)
        tiDescription= view?.findViewById(R.id.DescriptionTextField)
        tiAvailability = view?.findViewById(R.id.AvailabilityTextField)
        tiStartDate= view?.findViewById(R.id.tv_timeslotEdit_date)
        tiEndDate= view?.findViewById(R.id.tv_timeslotEdit_endDate)
        tiStartTime= view?.findViewById(R.id.tv_timeslotEdit_startTime)
        tiEndTime= view?.findViewById(R.id.tv_timeslotEdit_endTime)
        tiLocation = view?.findViewById(R.id.LocationTextField)
        tiCategory = view?.findViewById(R.id.chips_group)

        val btnDate = view.findViewById<MaterialButton>(R.id.edit_startDate)
        btnDate.setOnClickListener { showDatePickerDialog() }

        val btnStartTime = view.findViewById<MaterialButton>(R.id.edit_startTime)
        btnStartTime.setOnClickListener { showStartTimePickerDialog() }

        val btnEndTime = view.findViewById<MaterialButton>(R.id.edit_endTime)
        btnEndTime.setOnClickListener { showEndTimePickerDialog() }

        tiCategory?.setOnCheckedChangeListener { chipGroup, checkedId ->
            val titleOrNull = chipGroup.findViewById<Chip>(checkedId)?.text
            Toast.makeText(chipGroup.context, titleOrNull ?: "No Choice", Toast.LENGTH_LONG).show()
        }

        val btnRepetition = view.findViewById<Button>(R.id.edit_repetition)
        btnRepetition.setOnClickListener { showRepetitionDialog() }

        val btnEndDate = view.findViewById<MaterialCardView>(R.id.end_rep_date)
        btnEndDate?.setOnClickListener { showEndDatePickerDialog() }

        /*val myAutoComplete: AutoCompleteTextView
        val typeRepetition = resources.getStringArray(R.array.repetition_mw)


        val view: View = inflater.inflate(R.layout.modal_repetition, container, false)
        myAutoComplete = view.findViewById<View>(R.id.autoCompleteTextView) as AutoCompleteTextView

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.modal_repetition, typeRepetition)

        myAutoComplete.setAdapter(adapter)*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun addFocusChangeListeners(){
       // binding.TextInputEditTitle.setOnFocusChangeListener{_, focused -> if(!focused) timeSlotDetailsViewModel.apply{timeslot?.apply { binding.TextInputEditTitle.text.toString(); ""; }}}
    } //miss other fields

    // Extending DialogFragment for a date picker
    class DatePickerFragment(tiDate: TextView?) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        private val tiDate = tiDate

        @RequiresApi(Build.VERSION_CODES.N)
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
            tiDate?.text = "${day}/${(month + 1)}/${year}"
        }

    }

    // START DATE
    private fun showDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiStartDate)
        dateFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    // END DATE
    private fun showEndDatePickerDialog() {
        val dateFragment = DatePickerFragment(tiEndDate)
        dateFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    // Extending DialogFragment for a time picker
    class TimePickerFragment(tiTime: TextView?) : DialogFragment(),
        TimePickerDialog.OnTimeSetListener {

        private val tiTime = tiTime

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
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_repetition, null)
        val repetitions = resources.getStringArray(R.array.repetition_mw)
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.list_item, repetitions)
        var autoCompleteTextView = mDialogView?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView?.setAdapter(arrayAdapter)

        /*val title = mDialogView.findViewById<TextView>(R.id.modalTitle)
        title.text = String.format(getString(R.string.edit_skill_dialog_text), s.title)
        val question = mDialogView.findViewById<TextView>(R.id.question)
        question.text = String.format(getString(R.string.edit_skill_dialog_question), s.title.lowercase())

        //set the radio state starting from skill.active
        val radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val radioButtonYES = radioGroup.findViewById<RadioButton>(R.id.radioButtonYES)
        val radioButtonNO = radioGroup.findViewById<RadioButton>(R.id.radioButtonNO)
        radioButtonYES.isChecked = s.active
        radioButtonNO.isChecked = !s.active

        //populate the description
        val description = mDialogView.findViewById<EditText>(R.id.editDescription)
        description.setText(s.description)

        //Creation and showing the modal
        // set description hint max char length
        mDialogView.findViewById<TextView>(R.id.description_hint).text =
            String.format(getString(R.string.skill_description_helper),
                this.resources.getInteger(R.integer.maxInputLength))
*/
        //AlertDialogBuilder + show
        val mBuilder = android.app.AlertDialog.Builder(requireContext())
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
            val desc = card.findViewById<TextView>(R.id.skillDescription)
            desc.text = s.description
            //update skills
            refreshSkills()*/
            mAlertDialog?.dismiss() //close dialog
        }
    }


}