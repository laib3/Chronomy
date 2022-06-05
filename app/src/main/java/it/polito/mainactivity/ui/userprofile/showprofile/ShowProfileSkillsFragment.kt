package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileInfoBinding
import it.polito.mainactivity.databinding.FragmentShowProfileSkillsBinding

import it.polito.mainactivity.model.Utils

class ShowProfileSkillsFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileSkillsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileSkillsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}