package it.polito.mainactivity.ui.timeslot.timeslot_edit

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
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotEditBinding
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel


//TODO: CHECK BEFORE SAVE, BLOCK BACK NAVIGATION

class TimeslotEditFragment : Fragment() {

    private var _binding: FragmentTimeslotEditBinding? = null

    private var tiTitle: TextInputLayout? = null
    private var tiDescription: TextInputLayout? = null
    private var tiAvailability: TextInputLayout? = null
    private var tiStartDate: TextView? = null
    private var tiEndDate: TextView? = null
    private var tiStartTime: TextView? = null
    private var tiEndTime: TextView? = null
    private var tiLocation: TextInputLayout? = null
    private var tiCategory: ChipGroup? = null
    private var tiDays: ChipGroup? = null
    private var mAlertDialog: AlertDialog? = null

    private val timeSlotListViewModel:TimeslotViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var days: List<Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                tiLocation?.editText?.setText(it.elementAt(id).location)

                val categories: List<String> = resources.getStringArray(R.array.skills_array).toList()
                val index = categories.indexOf(it.elementAt(id).category)
                val chip : Chip = tiCategory?.getChildAt(index) as Chip
                tiCategory?.check(chip.id)

                days = it.elementAt(id).days
            }
        }else {
            /*val actionBar: ActionBar? = requireActivity().actionBar
            actionBar?.title = "New Timeslot"*/
           // activity?.title = "New Timeslot"
            //requireActivity().actionBar?.title="New Timeslot"
            
        }

        addFocusChangeListeners()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiTitle = view.findViewById(R.id.TitleTextField)
        tiDescription = view.findViewById(R.id.DescriptionTextField)
        tiAvailability = view.findViewById(R.id.AvailabilityTextField)
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //TODO CHECK
    private fun addFocusChangeListeners() {
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
            /*val d = DatePickerDialog(requireContext(), this, year, month, day)
            d.datePicker.minDate = timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.date?.timeInMillis!!
            return d*/
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
       /* var bundle = Bundle();
       bundle.putInt("id", requireArguments()?.getInt("id") ?: -1)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(DatePickerFragment::class.java, bundle, "datePicker")
            .commit()
        //other operations: insertion,
        //removal, visibility change, …      .commit()*/
        dateFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    // Extending DialogFragment for a time picker
    class TimePickerFragment(tiTime: TextView?) : DialogFragment(),TimePickerDialog.OnTimeSetListener {

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
        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.modal_repetition, null)

        tiDays = mDialogView.findViewById(R.id.days)

        val repetitions = resources.getStringArray(R.array.repetition_mw)
        val repeatOn = mDialogView?.findViewById<TextView>(R.id.repeat_on)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item, repetitions)
        var autoCompleteTextView =
            mDialogView?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView?.setAdapter(arrayAdapter)
        autoCompleteTextView?.onItemClickListener =
            OnItemClickListener { parent, arg1, position, arg3 ->
                val item = parent.getItemAtPosition(position)
                if(position == 0){ //weekly
                    repeatOn?.visibility = View.VISIBLE
                    tiDays?.visibility= View.VISIBLE
                }else{
                    repeatOn?.visibility = View.GONE
                    tiDays?.visibility= View.GONE
                }
            }

        if(arguments?.getInt("id")!= -1){
            if(timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.repetition == "weekly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(0).toString(), false)
            }
            if(timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.repetition == "monthly"){
                autoCompleteTextView?.setText(arrayAdapter.getItem(1).toString(), false)
                repeatOn?.visibility = View.GONE
                tiDays?.visibility= View.GONE
            }
            if (timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.repetition != "") {
                days?.forEach {
                    val chip = tiDays?.getChildAt(it-1) as Chip
                    tiDays?.check(chip.id)
                }
                tiEndDate = mDialogView?.findViewById(R.id.tv_timeslotEdit_endDate)
                tiEndDate?.text = timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.dateFormat?.format(timeSlotListViewModel.timeslots.value?.get(requireArguments().getInt("id"))?.endRepetitionDate?.time)
                tiEndDate?.setOnClickListener{ showEndDatePickerDialog()}
            }
        }

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
            //TODO CHECK THAT END DATE IS PRESENT
            //TODO CHECK THAT ONE CHIP HAS BEEN SELECTED
            mAlertDialog?.dismiss() //close dialog
        }
    }


}