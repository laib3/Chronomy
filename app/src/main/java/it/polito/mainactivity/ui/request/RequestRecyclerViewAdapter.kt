package it.polito.mainactivity.ui.request

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import com.google.android.material.card.MaterialCardView
import it.polito.mainactivity.R

import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils

class RequestRecyclerViewAdapter(
    private var values: List<Timeslot>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder>() {

   inner class RequestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvLocation: TextView = v.findViewById(R.id.tvLocation)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvHour: TextView = v.findViewById(R.id.tvHour)
        val ivCategory: ImageView = v.findViewById(R.id.lCategory)
        val cvTimeslotCard: MaterialCardView = v.findViewById(R.id.cvTimeslotCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return RequestViewHolder(vg)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val ts = values[position]
        holder.tvTitle.text = ts.title
        holder.tvLocation.text = ts.location
        holder.tvDate.text = when (ts.repetition) {
            "Weekly" -> "from " + Utils.formatDateToString(ts.startDate) +
                    " until " + Utils.formatDateToString(ts.endRepetitionDate) +
                    "\nevery week"
            "Monthly" -> "from " + Utils.formatDateToString(ts.startDate) +
                    " until " + Utils.formatDateToString(ts.endRepetitionDate) +
                    "\nevery month"
            else -> Utils.formatDateToString(ts.startDate)
        }

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
        
        // TODO: click on card, show chats preview
        /*
        holder.cvTimeslotCard.setOnClickListener {
            parentFragment.findNavController().navigate(R.id.action_nav_list_to_nav_details, bundle)
        }
         */
    }
    
    override fun getItemCount(): Int = values.size

    fun filterList(filteredList: List<Timeslot>) {
        val diffUtil = RequestDiffUtil(values, filteredList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        values = filteredList
        diffResult.dispatchUpdatesTo(this)

    }
}