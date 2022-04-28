package it.polito.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TimeSlotItem(val title: String, val location: String, val availability: String, val category:String)

class TimeSlotItemAdapter(val data: List<TimeSlotItem>): RecyclerView.Adapter<TimeSlotItemAdapter.TimeSlotItemViewHolder>() {
    class TimeSlotItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val location: TextView = v.findViewById(R.id.item_location)
        val availability: TextView = v.findViewById(R.id.item_availability)
        val category: TextView = v.findViewById(R.id.item_category)

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
    }

    override fun getItemCount(): Int = data.size

}


