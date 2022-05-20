package it.polito.mainactivity.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.User
import it.polito.mainactivity.model.Utils

import it.polito.mainactivity.placeholder.PlaceholderContent.PlaceholderItem

class TimeslotsRecyclerViewAdapter(
    private val values: List<Pair <Timeslot, User>>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<TimeslotsRecyclerViewAdapter.TimeslotViewHolder>() {

    inner class TimeslotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvHour: TextView = v.findViewById(R.id.tvHour)
        val ivProfilePic: ImageView = v.findViewById(R.id.ivProfilePic)
        val tvNickname : TextView = v.findViewById(R.id.nickname)

        val cvTimeslotCard: MaterialCardView = v.findViewById(R.id.cvTimeslotCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.global_timeslot_item, parent, false)
        return TimeslotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        val item = values[position]
        //NB first is the timeslot!
        holder.tvTitle.text = item.first.title
        holder.tvLocation.text = item.first.location
        holder.tvDate.text = when (item.first.repetition) {
            "Weekly" -> "from " + Utils.formatDateToString(item.first.startDate) +
                    " until " + Utils.formatDateToString(item.first.endRepetitionDate) +
                    "\nevery week"
            "Monthly" -> "from " + Utils.formatDateToString(item.first.startDate) +
                    " until " + Utils.formatDateToString(item.first.endRepetitionDate) +
                    "\nevery month"
            else -> Utils.formatDateToString(item.first.startDate)
        }
        holder.tvHour.text =
            parentFragment.activity?.getString(
                R.string.starting_hour_dash_ending_hour,
                item.first.startHour,
                item.first.endHour,
                Utils.getDuration(item.first.startHour?:"0:0", item.first.endHour?:"0:0")
            )

        //holder.ivProfilePic.setImageBitmap(item.second.profilePicture)

        holder.tvNickname.text = item.second.nickname

        /* FIXME NOT WORKING
        // Pass through bundle the id of the item in the list
        val bundle = Bundle()
        bundle.putInt("id", position)

        // click on card, show details of that item
        holder.cvTimeslotCard.setOnClickListener {
            parentFragment.findNavController().navigate(R.id.action_nav_home_to_nav_details, bundle)
        }
         */
    }

    override fun getItemCount(): Int = values.size
}