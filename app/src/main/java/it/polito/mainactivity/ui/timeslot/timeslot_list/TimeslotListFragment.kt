package it.polito.mainactivity.ui.timeslot.timeslot_list

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel

class TimeslotListFragment : Fragment() {

    private val vm: TimeslotViewModel by activityViewModels()
    //private val vm: MainViewModel by activityViewModels()
    private var _binding: FragmentTimeslotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        (activity as MainActivity).onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_nav_list_to_nav_home)
        }

        vm.timeslots.observe(viewLifecycleOwner) {
            val timeslots = vm.timeslots.value!!.filter{ t -> t.user.userId == FirebaseAuth.getInstance().currentUser!!.uid }
            val adapter = TimeslotAdapter(timeslots, this, vm)
            rv.adapter = adapter
            // If the list of timeslots is empty, show a message
            val tv: TextView = binding.emptyTimeslotListMessage
            tv.visibility = if (adapter.itemCount == 0)
                View.VISIBLE
            else
                View.INVISIBLE
        }

        // If click on fab, go to Edit timeslot
        val fab: FloatingActionButton = binding.fab
        val bundle = Bundle()

        fab.setOnClickListener {
            // TODO:FIX THIS
            // vm.resetSubmitTimeslot()
            findNavController().navigate(R.id.action_nav_list_to_nav_edit)
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume(){
        super.onResume()
        // if snackbar message is set: display it as snackbar
        (activity as MainActivity).snackBarMessage?.run {
            Snackbar.make(binding.root, this, Snackbar.LENGTH_SHORT)
                .setTextColor(Utils.getSnackbarColor(this))
                .show()
            (activity as MainActivity).snackBarMessage = null
        }
    }
}