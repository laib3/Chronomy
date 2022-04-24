package it.polito.mainactivity.ui.timeslot_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding

class TimeSlotDetailsFragment : Fragment() {

    private var _binding: FragmentTimeslotDetailsBinding? = null

    private lateinit var tiTitle : TextInputLayout
    private lateinit var tiDescription : TextInputLayout
    private lateinit var tiStartDate : TextInputLayout
    private lateinit var tiEndDate : TextInputLayout
    private lateinit var tiDuration : TextInputLayout
    private lateinit var tiLocation : TextInputLayout
    private lateinit var tiCategory : TextInputLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(TimeSlotDetailsViewModel::class.java)

        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTimeslotDetails
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: populate the fields with something!
        tiTitle = view.findViewById(R.id.TitleTextField)
        tiTitle.editText?.setText("Il titolo non è molto lungo")

        tiDescription= view.findViewById(R.id.DescriptionTextField)
        tiDescription.editText?.setText("La descrizione è ancora più super lunga,Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis.")

        tiStartDate = view.findViewById(R.id.StartDateTextField)
        tiStartDate.editText?.setText("22/04/2022 09:00 AM")

        tiEndDate = view.findViewById(R.id.EndDateTextField)
        tiEndDate.editText?.setText("22/05/2022 09:00 AM")

        tiDuration = view.findViewById(R.id.DurationTextField)
        tiDuration.editText?.setText("2h30m")

        tiLocation = view.findViewById(R.id.LocationTextField)
        tiLocation.editText?.setText("Via Vincenzo Vela 49, 10128, Torino")

        tiCategory = view.findViewById(R.id.CategoryTextField)
        tiCategory.editText?.setText("Gardening")


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}