package it.polito.mainactivity.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils

class TimeslotsRecyclerViewAdapter(
    private var values: List<Timeslot>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<TimeslotsRecyclerViewAdapter.TimeslotViewHolder>() {

    inner class TimeslotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvHour: TextView = v.findViewById(R.id.tvHour)
        val ivProfilePic: ImageView = v.findViewById(R.id.ivProfilePic)
        val tvNickname: TextView = v.findViewById(R.id.nickname)

        val cvTimeslotCard: MaterialCardView = v.findViewById(R.id.cvTimeslotCard)
        val clTimeslotInfo: ConstraintLayout = v.findViewById(R.id.clTimeslotInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.global_timeslot_item, parent, false)
        return TimeslotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        val ts = values[position]
        //NB first is the timeslot!
        holder.tvTitle.text = ts.title
        holder.tvLocation.text = ts.location
        holder.tvDate.text = Utils.formatDateToString(ts.date)
        holder.tvHour.text =
            parentFragment.activity?.getString(
                R.string.starting_hour_dash_ending_hour,
                ts.startHour,
                ts.endHour,
                Utils.getDuration(ts.startHour, ts.endHour)
            )

        holder.ivProfilePic.clipToOutline = true
        ts.user.profilePictureUrl?.apply { Picasso.get().load(this).into(holder.ivProfilePic) }
        val placeholder =
            parentFragment.resources.getString(R.string.user_profile_nickname_placeholder)
        holder.tvNickname.text = String.format(placeholder, ts.user.nickname)

        // Pass through bundle the id of the timeslot in the list
        val bundle = Bundle()
        bundle.putString("id", ts.timeslotId)
        bundle.putBoolean("showOnly", true)

        // click on card in the timeslot part, show details of that timeslot
        holder.clTimeslotInfo.setOnClickListener {
            parentFragment.findNavController()
                .navigate(R.id.action_nav_filtered_to_nav_details, bundle)
        }

        // click on card in the user profile part, show details of that profile
        holder.cvTimeslotCard.setOnClickListener {
            parentFragment.findNavController()
                .navigate(R.id.action_nav_filtered_to_nav_show_profile, bundle)
        }
    }

    override fun getItemCount(): Int = values.size

    fun filterList(filteredList: List<Timeslot>) {
        values = filteredList
        notifyDataSetChanged()
    }
}