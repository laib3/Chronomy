package it.polito.mainactivity.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentHomeBinding
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel

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

        vm.timeslots.observe(viewLifecycleOwner) {
            val presentSkills = it.map{el-> el.category}
            gridLayout.removeAllViews()
            //remove duplicates and show the buttons
            presentSkills.toSet()
                .map{s -> SkillHomeButton(requireContext(), s)}
                .forEach { shb: SkillHomeButton ->
                    gridLayout.addView(shb)
                     shb.setOnClickListener{
                         val action = HomeFragmentDirections.actionNavHomeToFilteredTimeslotListFragment( shb.category.lowercase())
                         parentFragment?.findNavController()?.navigate(action)
                    }}
        }
        return root
    }
}