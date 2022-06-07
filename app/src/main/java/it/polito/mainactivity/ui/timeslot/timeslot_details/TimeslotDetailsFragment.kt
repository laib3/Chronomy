package it.polito.mainactivity.ui.timeslot.timeslot_details

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotDetailsBinding
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.model.*
import it.polito.mainactivity.viewModel.UserProfileViewModel
import kotlinx.coroutines.launch

class TimeslotDetailsFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private val userVm: UserProfileViewModel by activityViewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var _binding: FragmentTimeslotDetailsBinding? = null

    private var tiTitle: TextInputLayout? = null
    private var tiDescription: TextInputLayout? = null
    private var tiAvailability: TextInputLayout? = null
    private var tiLocation: TextInputLayout? = null
    private var tiCategory: TextInputLayout? = null
    private var tiCost: TextInputLayout? = null
    private var tiStatus: TextInputLayout? = null

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
            binding.bSendRequest.visibility = View.VISIBLE
        } else {
            binding.bSendRequest.visibility = View.INVISIBLE
        }

        vm.timeslots.observe(viewLifecycleOwner) {
            ts = it.find { t -> t.timeslotId == id }

            // check if timeslot is published or not - if yes enable button
            if (ts?.status == Timeslot.Status.PUBLISHED) {
                setFab(binding.bSendRequest, true)
                binding.bSendRequest.visibility = View.VISIBLE
            } else {
                setFab(binding.bSendRequest, false)
                binding.bSendRequest.visibility = View.INVISIBLE
            }

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
                tiStatus?.editText?.setText(ts!!.status.toString())
                tiCost?.editText?.setText(
                    String.format(
                        getString(R.string.user_profile_balance_placeholder),
                        Utils.tcuFromStartEndHour(
                            ts!!.startHour,
                            ts!!.endHour
                        ).toString()
                    )
                )

            }

            // if I am client of one of the chat of the timeslot disable button
            if (ts!!.chats.any { chat -> chat.client["userId"] == FirebaseAuth.getInstance().currentUser!!.uid }) {
                setFab(binding.bSendRequest, false)
                binding.bSendRequest.visibility = View.VISIBLE
            }
        }

        vm.newChatId.observe(viewLifecycleOwner) {
            if (it != null) {
                val action =
                    TimeslotDetailsFragmentDirections.actionNavDetailsToChatFragment(
                        it,
                        ts!!.timeslotId,
                        ts!!.title
                    )
                vm.resetNewChatId()
                parentFragment?.findNavController()?.navigate(action)
            }
        }

        binding.bSendRequest.setOnClickListener {
            //create new chat
            vm.addChat(ts!!.timeslotId, userVm.user.value!!)
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
        tiStatus = view.findViewById(R.id.tilStatus)
        tiCost = view.findViewById(R.id.tilCost)
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

    private fun setFab(f: ExtendedFloatingActionButton, enabled: Boolean) {
        if (enabled) {
            f.isEnabled = true
            f.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.purple_500))
            f.text = "Send request"
        } else {
            f.isEnabled = false
            f.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.dark_grey))
            f.text = "Request sent"
        }
    }
}