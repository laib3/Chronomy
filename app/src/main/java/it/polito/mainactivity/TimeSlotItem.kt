package it.polito.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController

data class TimeSlotItem(val title: String, val location: String, val availability: String, val category:String)


class TimeSlotItemAdapter(val data: List<TimeSlotItem>, val parentFragment: Fragment): RecyclerView.Adapter<TimeSlotItemAdapter.TimeSlotItemViewHolder>() {
    class TimeSlotItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val location: TextView = v.findViewById(R.id.item_location)
        val availability: TextView = v.findViewById(R.id.item_availability)
        val category: TextView = v.findViewById(R.id.item_category)
        val card: CardView = v.findViewById(R.id.item_card)
        val editButton: Button = v.findViewById(R.id.item_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return TimeSlotItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeSlotItemViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.location.text = data[position].location
        holder.availability.text = data[position].availability
        holder.category.text = data[position].category

        var bundle = Bundle();
        bundle.putInt("id", position)

        // click on card, show details of that item
        holder.card.setOnClickListener{
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_details, bundle)
        }
        // click on edit button, edit details of that item
        holder.editButton.setOnClickListener{
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_edit, bundle)
        }


    }

    override fun getItemCount(): Int = data.size

}


