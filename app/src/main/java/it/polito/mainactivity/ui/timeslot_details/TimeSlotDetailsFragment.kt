package it.polito.mainactivity.ui.timeslot_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding

class TimeSlotDetailsFragment : Fragment() {

    private var _binding: FragmentTimeslotDetailsBinding? = null

    private var tiTitle : TextInputLayout? = null
    private var tiDescription : TextInputLayout? = null
    private var tiAvailability : TextInputLayout? = null
    private var tiLocation : TextInputLayout? = null
    private var tiCategory : TextInputLayout? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        val timeSlotDetailsViewModel =
            ViewModelProvider(this).get(TimeSlotDetailsViewModel::class.java)

        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTimeslotDetails
        timeSlotDetailsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        timeSlotDetailsViewModel.timeslot.observe(viewLifecycleOwner){
            tiTitle?.editText?.setText(it.title)
            tiDescription?.editText?.setText(it.description)

            var dateString = ""
            for (date in it.days){
                dateString += it.days[0].date.toString()+"/"+it.days[0].month.toString()+ " from "+it.startHour+":"+it.startMinute+" to "+it.endHour+":"+it.endMinute+"\n"
            }
            dateString = dateString.substring(0, dateString.length - 1);
            tiAvailability?.editText?.setText(dateString)
            tiLocation?.editText?.setText(it.location)
            tiCategory?.editText?.setText(it.category)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiTitle = view?.findViewById(R.id.TitleTextField)
        tiDescription= view?.findViewById(R.id.DescriptionTextField)
        tiAvailability = view?.findViewById(R.id.AvailabilityTextField)
        tiLocation = view?.findViewById(R.id.LocationTextField)
        tiCategory = view?.findViewById(R.id.CategoryTextField)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}