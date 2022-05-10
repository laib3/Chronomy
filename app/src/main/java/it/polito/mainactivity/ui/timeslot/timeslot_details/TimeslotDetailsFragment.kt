package it.polito.mainactivity.ui.timeslot.timeslot_details

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import androidx.core.text.bold
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import java.util.*

class TimeslotDetailsFragment : Fragment() {

    private val vm: TimeslotViewModel by activityViewModels()

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

        setHasOptionsMenu(true)

        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // TODO remove in favour of null
        val id = arguments?.getInt("id") ?: -1

        vm.timeslots.observe(viewLifecycleOwner) {

            val ts = it.elementAt(id)

            tiTitle?.editText?.setText(ts.title)
            tiDescription?.editText?.setText(ts.description)

            val dateString = SpannableStringBuilder()

            if(ts.repetition == ""){
                dateString
                    .italic{append(Utils.formatDateToString(ts.startDate))}
                    .append(" from ")
                    .italic{append(ts.startHour)}
                    .append(" to ")
                    .italic{append(ts.endHour)}

            }
            else if(ts.repetition?.lowercase() == "weekly"){
                dateString
                    .append("This timeslots repeats ")
                    .bold{append("weekly")}
                    .append(".\n\nStarting on ")
                    .italic{append(Utils.formatDateToString(ts.startDate))}
                    .append(" until ")
                    .italic{append(Utils.formatDateToString(ts.endRepetitionDate))}
                    .append("\nevery ")
                    .italic{append("${Utils.getDaysOfRepetition(ts.days)}\n")}
                    .append("from ")
                    .italic{append(ts.startHour)}
                    .append(" to ")
                    .italic{append(ts.endHour)}
            }
            else { //monthly
                dateString
                    .append("This timeslots repeats ")
                    .bold{append("monthly")}
                    .append(".\n\nStarting on ")
                    .italic{append(Utils.formatDateToString(ts.startDate))}
                    .append(" until ")
                    .italic{append(Utils.formatDateToString(ts.endRepetitionDate))}
                    .append("\nevery ")
                    .italic{append("${ts.startDate.get(Calendar.DAY_OF_MONTH)}")}
                    .append(" of the month\n")
                    .append("from ")
                    .italic{append(ts.startHour)}
                    .append(" to ")
                    .italic{append(ts.endHour)}
            }

            tiAvailability?.editText?.text = dateString
            tiLocation?.editText?.setText(ts.location)
            tiCategory?.editText?.setText(ts.category)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tiTitle = view.findViewById(R.id.tilTitle)
        tiDescription = view.findViewById(R.id.tilDescription)
        tiAvailability = view.findViewById(R.id.AvailabilityTextField)
        tiLocation = view.findViewById(R.id.tilLocation)
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
        if(item.itemId == R.id.nav_edit) {
            val bundle = Bundle();
            arguments?.getInt("id")?.let{ bundle.putInt("id", it) }
            findNavController().navigate(R.id.action_nav_details_to_nav_edit, bundle)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        // If snackbar message is set: display it as snackbar
        (activity as MainActivity).snackBarMessage?.run {
            Snackbar.make(binding.root, this, Snackbar.LENGTH_SHORT)
                .setTextColor(Utils.getSnackbarColor(this))
                .show()
            (activity as MainActivity).snackBarMessage = null
        }
    }
}