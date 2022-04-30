package it.polito.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeslotAdapter(val data: List<Timeslot>): RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>() {
    class TimeslotViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val location: TextView = v.findViewById(R.id.item_location)
        val availability: TextView = v.findViewById(R.id.item_availability)
        val category: TextView = v.findViewById(R.id.item_category)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return TimeslotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeslotViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.location.text = data[position].location
        //holder.availability.text = data[position].availability
        holder.category.text = data[position].category
    }

    override fun getItemCount(): Int = data.size

}


