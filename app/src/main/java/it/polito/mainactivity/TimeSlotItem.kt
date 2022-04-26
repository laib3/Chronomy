package it.polito.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TimeSlotItem(val name: String, val role: String)

class TimeSlotItemAdapter(val data: List<TimeSlotItem>): RecyclerView.Adapter<TimeSlotItemAdapter.TimeSlotItemViewHolder>() {
    class TimeSlotItemViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.name)
        val role: TextView = v.findViewById(R.id.role)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotItemViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return TimeSlotItemViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeSlotItemViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.role.text = data[position].role
    }

    override fun getItemCount(): Int = data.size

}


