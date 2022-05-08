package it.polito.mainactivity.ui.timeslot.timeslot_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import java.text.DateFormat
import java.util.*

class TimeslotAdapter(val data: List<Timeslot>, val parentFragment: Fragment): RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>() {

    class TimeslotViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val location: TextView = v.findViewById(R.id.tvLocation)
        val date: TextView = v.findViewById(R.id.tvDate)
        val hour: TextView = v.findViewById(R.id.tvHour)
        val category: ImageView = v.findViewById(R.id.ivCategory)
        val card: MaterialCardView= v.findViewById(R.id.item_card)
        val editButton: ImageButton = v.findViewById(R.id.ibEdit)
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
        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY)
        holder.date.text = if(data[position].repetition != null){
            //"from ${dateFormat.format(data[position].date.getTime())} every ${data[position].repetition?.dropLast(2)}"
            Utils.formatDateToString(data[position].date)
        }
        else {
            Utils.formatDateToString(data[position].date)
            //dateFormat.format(data[position].date.getTime())
        }
        holder.hour.text =  "${data[position].startHour} - ${data[position].endHour}"
        Utils.getSkillImgRes(data[position].category)?.also{ it -> holder.category.setImageResource(it)}

        // pass through bundle the id of the item in the list
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
