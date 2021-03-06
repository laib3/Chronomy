package it.polito.mainactivity.ui.timeslot.timeslot_list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.viewModel.TimeslotViewModel

class TimeslotAdapter(
    private val myTimeslots: List<Timeslot>,
    private val parentFragment: Fragment,
    private val vm: TimeslotViewModel
) :
    RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>() {

    class TimeslotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvUserNickname)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvHour: TextView = v.findViewById(R.id.tvHour)
        val ivCategory: ImageView = v.findViewById(R.id.iUserPic)
        val cvTimeslotCard: MaterialCardView = v.findViewById(R.id.cvRatingCard)
        val ibEdit: ImageButton = v.findViewById(R.id.ibEdit)
        val ibDelete: ImageButton = v.findViewById(R.id.ibDelete)
        val tvCost: TextView = v.findViewById(R.id.tvCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return TimeslotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        val ts = myTimeslots[position]
        holder.tvTitle.text = ts.title
        holder.tvLocation.text = ts.location
        holder.tvDate.text = Utils.formatDateToString(ts.date)
        holder.tvCost.text = String.format(parentFragment.resources.getString(R.string.cost_placeholder), Utils.tcuFromStartEndHour(ts.startHour, ts.endHour).toString())
        holder.tvHour.text =
            parentFragment.activity?.getString(
                R.string.starting_hour_dash_ending_hour,
                ts.startHour,
                ts.endHour,
                Utils.getDuration(ts.startHour, ts.endHour)
            )

        // Change the image icon of the skill with the correct one
        Utils.getSkillImgRes(ts.category)
            ?.also { it -> holder.ivCategory.setImageResource(it) }

        // Pass through bundle the id of the item in the list
        val bundle = Bundle()
        bundle.putString("id", ts.timeslotId)
        bundle.putBoolean("showOnly", false)
        bundle.putBoolean("startChat", false)

        // click on card, show details of that item
        holder.cvTimeslotCard.setOnClickListener {
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_details, bundle)
        }
        // click on edit button, edit details of that item
        holder.ibEdit.setOnClickListener {
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_edit, bundle)
        }
        // click on delete button, show modal to confirm the deletion of that item
        holder.ibDelete.setOnClickListener {
            android.app.AlertDialog.Builder(parentFragment.requireContext())
                .setTitle("Delete Timeslot")
                .setMessage(
                    "Are you sure you want to delete this timeslot?\n\n" +
                            "\'${ts.title}\'\n"
                )
                .setPositiveButton("Delete", DialogInterface.OnClickListener(
                    fun(_, _) {
                        vm.deleteTimeslot(ts.timeslotId)
                    }

                ))
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_baseline_report_problem_24)
                .show()
        }
    }

    override fun getItemCount(): Int = myTimeslots.size

}
