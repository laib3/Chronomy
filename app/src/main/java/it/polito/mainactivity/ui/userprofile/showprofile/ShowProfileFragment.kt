package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileBinding
import it.polito.mainactivity.model.Utils

class ShowProfileFragment : Fragment() {
    private var _binding: FragmentShowProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val showOnly: Boolean = arguments?.getBoolean("showOnly") ?: false
        setHasOptionsMenu(!showOnly)

        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.nav_home)
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.showprofile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_nav_show_profile_to_nav_edit_profile) {
            findNavController().navigate(R.id.action_nav_show_profile_to_nav_edit_profile)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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