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
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import java.util.*


class TimeslotAdapter(val vm: TimeslotViewModel, val parentFragment: Fragment): RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>() {

    class TimeslotViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvHour: TextView = v.findViewById(R.id.tvHour)
        val ivCategory: ImageView = v.findViewById(R.id.ivCategory)
        val cvTimeslotCard: MaterialCardView= v.findViewById(R.id.cvTimeslotCard)
        val ibEdit: ImageButton = v.findViewById(R.id.ibEdit)
        val ibDelete: ImageButton = v.findViewById(R.id.ibDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return TimeslotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        holder.tvTitle.text = vm.timeslots.value?.get(position)?.title
        holder.tvLocation.text = vm.timeslots.value?.get(position)?.location

        // TODO: if repetition, decide how to display it and do it
        holder.tvDate.text = Utils.formatDateToString(vm.timeslots.value?.get(position)?.date)

        holder.tvHour.text =  "${vm.timeslots.value?.get(position)?.startHour} - ${vm.timeslots.value?.get(position)?.endHour}"

        // Change the image icon of the skill with the correct one
        Utils.getSkillImgRes(vm.timeslots.value?.get(position)!!.category)?.also{ it -> holder.ivCategory.setImageResource(it)}

        // Pass through bundle the id of the item in the list
        var bundle = Bundle();
        bundle.putInt("id", position)

        // click on card, show details of that item
        holder.cvTimeslotCard.setOnClickListener{
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_details, bundle)
        }
        // click on edit button, edit details of that item
        holder.ibEdit.setOnClickListener{
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_edit, bundle)
        }
        // click on delete button, show modal to confirm the deletion of that item
        holder.ibDelete.setOnClickListener{
            // TODO: open the modal
            android.app.AlertDialog.Builder(parentFragment.requireContext())
                .setTitle("Delete Timeslot")
                .setMessage("Are you sure you want to delete this timeslot?\n\n" +
                        "\'${vm.timeslots.value?.get(position)?.title}\'\n")
                .setPositiveButton("Delete", DialogInterface.OnClickListener(
                    fun (_, _){
                        vm.removeTimeslot(position)
                    }

                ))
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_baseline_report_problem_24)
                .show()
            }
            // TODO: if confirmed, delete the element at position from the list
        }

    override fun getItemCount(): Int = vm.timeslots.value?.size ?: 0

}
