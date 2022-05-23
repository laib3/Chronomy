package it.polito.mainactivity.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentHomeBinding
import it.polito.mainactivity.model.Skill
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import java.util.Map

class HomeFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gridLayout:GridLayout = binding.gridLayout
        gridLayout.columnCount = 2;
        gridLayout.rowCount = 5;

        val tvSkillNumber = requireActivity().findViewById<TextView>(R.id.tvSkillNumber)

        vm.timeslots.observe(viewLifecycleOwner) {
            val categoryNumbers: MutableMap<String, Int> = mutableMapOf()
            val presentSkills = it.filter{ t -> t.user.userId != FirebaseAuth.getInstance().currentUser!!.uid }
                .forEach{ t -> categoryNumbers.set(t.category, categoryNumbers.get(t.category)?.plus(1) ?: 1) }

            gridLayout.removeAllViews()
            //remove duplicates and show the buttons

            categoryNumbers
                .map{ (cat, num) -> SkillHomeButton(requireContext(), cat, num) }
                .forEach{ button: SkillHomeButton ->
                    gridLayout.addView(button)
                    button.setOnClickListener{
                        val action = HomeFragmentDirections.actionNavHomeToFilteredTimeslotListFragment(button.category.lowercase())
                        parentFragment?.findNavController()?.navigate(action)
                    }
                }
        }
        return root
    }
}