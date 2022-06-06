package it.polito.mainactivity.ui.timeslot.timeslot_details

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.model.*
import kotlinx.coroutines.launch

class TimeslotDetailsFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()

    private var _binding: FragmentTimeslotDetailsBinding? = null

    private var tiTitle: TextInputLayout? = null
    private var tiDescription: TextInputLayout? = null
    private var tiAvailability: TextInputLayout? = null
    private var tiLocation: TextInputLayout? = null
    private var tiCategory: TextInputLayout? = null

    private var startChat: Boolean = false

    private val binding get() = _binding!!

    private var ts: Timeslot? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentTimeslotDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id = arguments?.getString("id")
        val showOnly: Boolean = arguments?.getBoolean("showOnly") ?: false
        setHasOptionsMenu(!showOnly)

        startChat = arguments?.getBoolean("startChat") ?: false

        if (startChat) {
            binding.extendedFab.visibility = View.VISIBLE
        } else {
            binding.extendedFab.visibility = View.INVISIBLE
        }


        vm.timeslots.observe(viewLifecycleOwner) {
            ts = it.find { t -> t.timeslotId == id }

            tiTitle?.editText?.setText(ts?.title)
            tiDescription?.editText?.setText(ts?.description)

            val dateString = SpannableStringBuilder()

            if (ts != null) {
                dateString
                    .italic { append(Utils.formatDateToString(ts!!.date)) }
                    .append("\nfrom ")
                    .italic { append(ts!!.startHour) }
                    .append(" to ")
                    .italic { append(ts!!.endHour) }
                    .append("  (%s)".format(Utils.getDuration(ts!!.startHour, ts!!.endHour)))
                tiAvailability?.editText?.text = dateString
                tiLocation?.editText?.setText(ts!!.location)
                tiCategory?.editText?.setText(ts!!.category)
            }
        }

        binding.extendedFab.setOnClickListener {
            //create new chat, navigate to chat fragment
            vm.addChat(ts!!.timeslotId)

            val chat =
                ts!!.chats.firstOrNull { chat -> chat.client["userId"] == FirebaseAuth.getInstance().currentUser!!.uid }

            val action =
                TimeslotDetailsFragmentDirections.actionNavDetailsToChatFragment(
                    chat!!.chatId,
                    ts!!.timeslotId,
                    ts!!.title
                )
            parentFragment?.findNavController()?.navigate(action)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_edit) {
            val bundle = Bundle()
            arguments?.getString("id")?.let { bundle.putString("id", it) }
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