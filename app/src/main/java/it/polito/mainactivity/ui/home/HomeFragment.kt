package it.polito.mainactivity.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentHomeBinding
import it.polito.mainactivity.viewModel.TimeslotViewModel

class HomeFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gridLayout: GridLayout = binding.gridLayout
        gridLayout.columnCount = 2;
        gridLayout.rowCount = 5;

        val tvSkillNumber = requireActivity().findViewById<TextView>(R.id.tvSkillNumber)

        vm.timeslots.observe(viewLifecycleOwner) {
            val categoryNumbers: MutableMap<String, Int> = mutableMapOf()
            it.filter { t -> t.user.userId != FirebaseAuth.getInstance().currentUser!!.uid }
                .forEach { t ->
                    categoryNumbers.set(
                        t.category,
                        categoryNumbers.get(t.category)?.plus(1) ?: 1
                    )
                }

            gridLayout.removeAllViews()
            val tvHello: TextView = binding.tvHello
            val tvMessage: TextView = binding.tvMessage
            val tvEmptyMessage: TextView = binding.tvEmptyMessage

            if (categoryNumbers.isEmpty()) {
                // Show the empty message
                tvEmptyMessage.visibility = View.VISIBLE

                // Don't show the other messages
                tvHello.visibility = View.INVISIBLE
                tvMessage.visibility = View.INVISIBLE
            } else {
                // Don't show the empty message
                tvEmptyMessage.visibility = View.INVISIBLE

                // Show the other messages
                tvHello.visibility = View.VISIBLE
                tvMessage.visibility = View.VISIBLE

                // Show the buttons
                categoryNumbers
                    .map { (cat, num) -> SkillHomeButton(requireContext(), cat, num) }
                    .forEach { button: SkillHomeButton ->
                        gridLayout.addView(button)
                        button.setOnClickListener {
                            val action =
                                HomeFragmentDirections.actionNavHomeToFilteredTimeslotListFragment(
                                    button.category
                                )
                            parentFragment?.findNavController()?.navigate(action)
                        }
                    }
            }
        }
        return root
    }
}