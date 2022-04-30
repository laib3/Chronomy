package it.polito.mainactivity.ui.timeslot_details

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.Timeslot
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

    //private var t: Timeslot? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val timeSlotListViewModel =
            ViewModelProvider(this).get(TimeSlotListViewModel::class.java)

        setHasOptionsMenu(true);

        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id: Int = arguments?.getInt("id") ?: -1

        val textView: TextView = binding.textTimeslotDetails
        val timeSlotDetailsViewModel = ViewModelProvider(this)[TimeSlotDetailsViewModel::class.java]
        timeSlotDetailsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        timeSlotListViewModel.timeslots.observe(viewLifecycleOwner) {

            tiTitle?.editText?.setText(it.elementAt(id).title)
            tiDescription?.editText?.setText(it.elementAt(id).description)

            var dateString = ""
            dateString += it.elementAt(id).date.toString() + " from " + it.elementAt(id).startHour + " to " + it.elementAt(
                id
            ).endHour + "\n"
            dateString = dateString.substring(0, dateString.length - 1);
            tiAvailability?.editText?.setText(dateString)
            tiLocation?.editText?.setText(it.elementAt(id).location)
            tiCategory?.editText?.setText(it.elementAt(id).category)

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tiTitle = view?.findViewById(R.id.TitleTextField)
        tiDescription = view?.findViewById(R.id.DescriptionTextField)
        tiAvailability = view?.findViewById(R.id.AvailabilityTextField)
        tiLocation = view?.findViewById(R.id.LocationTextField)
        tiCategory = view?.findViewById(R.id.CategoryTextField)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.timeslot_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item!!,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}