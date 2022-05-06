package it.polito.mainactivity.ui.timeslot_details

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding
import it.polito.mainactivity.ui.timeslot_list.TimeSlotListViewModel
import java.util.*

class TimeSlotDetailsFragment : Fragment() {

    private var _binding: FragmentTimeslotDetailsBinding? = null

    private var tiTitle: TextInputLayout? = null
    private var tiDescription: TextInputLayout? = null
    private var tiAvailability: TextInputLayout? = null
    private var tiLocation: TextInputLayout? = null
    private var tiCategory: TextInputLayout? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val timeSlotListViewModel =
            ViewModelProvider(this).get(TimeSlotListViewModel::class.java)

        setHasOptionsMenu(true)

        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id = arguments?.getInt("id") ?: -1

        //val textView: TextView = binding.textTimeslotDetails
        //val timeSlotDetailsViewModel = ViewModelProvider(this)[TimeSlotDetailsViewModel::class.java]
        //timeSlotDetailsViewModel.text.observe(viewLifecycleOwner) {
          //  textView.text = it
        //}

        timeSlotListViewModel.timeslots.observe(viewLifecycleOwner) {
            tiTitle?.editText?.setText(it.elementAt(id).title)
            tiDescription?.editText?.setText(it.elementAt(id).description)

            val dateString = SpannableStringBuilder()

            if(it.elementAt(id).repetition == ""){
                dateString
                    .italic{append(it.elementAt(id).dateFormat.format(it.elementAt(id).date.time))}
                    .append(" from ")
                    .italic{append(it.elementAt(id).startHour)}
                    .append(" to ")
                    .italic{append(it.elementAt(id).endHour)}
            }else if(it.elementAt(id).repetition.lowercase() == "weekly"){
                dateString
                    .append("This timeslots repeats ")
                    .bold{append("weekly")}
                    .append(".\n\nStarting on ")
                    .italic{append(it.elementAt(id).dateFormat.format(it.elementAt(id).date.time))}
                    .append(" until ")
                    .italic{append(it.elementAt(id).dateFormat.format(it.elementAt(id).endRepetitionDate?.time))}
                    .append("\nevery ")
                    .italic{append("${it.elementAt(id).getDaysOfRepetition()}\n")}
                    .append("from ")
                    .italic{append(it.elementAt(id).startHour)}
                    .append(" to ")
                    .italic{append(it.elementAt(id).endHour)}
            }else { //monthly
                dateString
                    .append("This timeslots repeats ")
                    .bold{append("monthly")}
                    .append(".\n\nStarting on ")
                    .italic{append(it.elementAt(id).dateFormat.format(it.elementAt(id).date.time))}
                    .append(" until ")
                    .italic{append(it.elementAt(id).dateFormat.format(it.elementAt(id).endRepetitionDate?.time))}
                    .append("\nevery ")
                    .italic{append("${it.elementAt(id).date.get(Calendar.DAY_OF_MONTH)}")}
                    .append(" of the month\n")
                    .append("from ")
                    .italic{append(it.elementAt(id).startHour)}
                    .append(" to ")
                    .italic{append(it.elementAt(id).endHour)}
            }

            tiAvailability?.editText?.text = dateString
            tiLocation?.editText?.setText(it.elementAt(id).location)
            tiCategory?.editText?.setText(it.elementAt(id).category)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiTitle = view.findViewById(R.id.TitleTextField)
        tiDescription = view.findViewById(R.id.DescriptionTextField)
        tiAvailability = view.findViewById(R.id.AvailabilityTextField)
        tiLocation = view.findViewById(R.id.LocationTextField)
        tiCategory = view.findViewById(R.id.CategoryTextField)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.timeslot_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        var bundle = Bundle();
        bundle.putInt("id", arguments?.getInt("id") ?: -1)
        findNavController().navigate(R.id.action_nav_details_to_nav_edit, bundle)
        return super.onOptionsItemSelected(item)
    }

}