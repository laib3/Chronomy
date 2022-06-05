package it.polito.mainactivity.ui.userprofile.showprofile

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileInfoBinding
import it.polito.mainactivity.databinding.FragmentShowProfileRatingsBinding
import it.polito.mainactivity.databinding.FragmentShowProfileSkillsBinding
import it.polito.mainactivity.model.Rating
import it.polito.mainactivity.model.Timeslot

import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.TimeslotViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShowProfileRatingsFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileRatingsBinding? = null
    private val binding get() = _binding!!
    private val vmTimeslots: TimeslotViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileRatingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // If show profile of other users
        if (timeslotId != null) {

        } else { // if show profile of the current publisher

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RatingAdapter(
    private val ratingsList: List<Rating>,
    private val parentFragment: Fragment,
) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        //val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        //val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        //val tvDate: TextView = v.findViewById(R.id.tvDate)
        //val tvHour: TextView = v.findViewById(R.id.tvHour)
        //val ivCategory: ImageView = v.findViewById(R.id.lCategory)
        //val cvTimeslotCard: MaterialCardView = v.findViewById(R.id.cvTimeslotCard)
        //val ibEdit: ImageButton = v.findViewById(R.id.ibEdit)
        //val ibDelete: ImageButton = v.findViewById(R.id.ibDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return RatingViewHolder(vg)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        //val ts = myTimeslots[position]
        //holder.tvTitle.text = ts.title
        //holder.tvLocation.text = ts.location
        //holder.tvDate.text = Utils.formatDateToString(ts.date)

        //holder.tvHour.text =
        //    parentFragment.activity?.getString(
        //        R.string.starting_hour_dash_ending_hour,
        //        ts.startHour,
        //        ts.endHour,
        //        Utils.getDuration(ts.startHour, ts.endHour)
        //    )

        // Change the image icon of the skill with the correct one
        //Utils.getSkillImgRes(ts.category)
        //    ?.also { it -> holder.ivCategory.setImageResource(it) }

        //// Pass through bundle the id of the item in the list
        //val bundle = Bundle()
        //bundle.putString("id", ts.timeslotId)
        //bundle.putBoolean("showOnly", false)
        //bundle.putBoolean("startChat", false)

        //// click on card, show details of that item
        //holder.cvTimeslotCard.setOnClickListener {
        //    parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_details, bundle)
        //}
        //// click on edit button, edit details of that item
        //holder.ibEdit.setOnClickListener {
        //    parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_edit, bundle)
        //}
        //// click on delete button, show modal to confirm the deletion of that item
        //holder.ibDelete.setOnClickListener {
        //    android.app.AlertDialog.Builder(parentFragment.requireContext())
        //        .setTitle("Delete Timeslot")
        //        .setMessage(
        //            "Are you sure you want to delete this timeslot?\n\n" +
        //                    "\'${ts.title}\'\n"
        //        )
        //        .setPositiveButton("Delete", DialogInterface.OnClickListener(
        //            fun(_, _) {
        //                vm.deleteTimeslot(ts.timeslotId)
        //            }

        //        ))
        //        .setNegativeButton("Cancel", null)
        //        .setIcon(R.drawable.ic_baseline_report_problem_24)
        //        .show()
        //}
    }

    override fun getItemCount(): Int = ratingsList.size

}