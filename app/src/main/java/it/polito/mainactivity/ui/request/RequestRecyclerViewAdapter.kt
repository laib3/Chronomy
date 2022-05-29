package it.polito.mainactivity.ui.request

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.skydoves.expandablelayout.ExpandableLayout
import it.polito.mainactivity.R

import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils

class RequestRecyclerViewAdapter(
    private var values: List<Timeslot>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder>() {

   inner class RequestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
       val tvTitle: TextView = v.findViewById(R.id.tvTitle)
       val ivCategory: ImageView = v.findViewById(R.id.lCategory)
       val btnArrow:ImageButton = v.findViewById(R.id.arrow_button)
       val insideContent:ConstraintLayout = v.findViewById(R.id.hidden_view)
       val baseCardView : CardView = v.findViewById(R.id.base_cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.request_item, parent, false)
        return RequestViewHolder(vg)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val ts = values[position]
        holder.tvTitle.text = ts.title
       Utils.getSkillImgRes(ts.category)
            ?.also { it -> holder.ivCategory.setImageResource(it) }


       holder.btnArrow.setOnClickListener {
           // If the CardView is already expanded, set its visibility
           //  to gone and change the expand less icon to expand more.
           if (holder.insideContent.getVisibility() == View.VISIBLE) {

               // The transition of the hiddenView is carried out
               //  by the TransitionManager class.
               // Here we use an object of the AutoTransition
               // Class to create a default transition.
               TransitionManager.beginDelayedTransition(
                   holder.baseCardView,
                   AutoTransition()
               );

               holder.insideContent.visibility = View.GONE;
               holder.btnArrow.setImageResource(R.drawable.ic_arrow_down);
           }

           // If the CardView is not expanded, set its visibility
           // to visible and change the expand more icon to expand less.
           else {

               TransitionManager.beginDelayedTransition(
                   holder.baseCardView,
                   AutoTransition());
               holder.insideContent.visibility = View.VISIBLE;
               holder.btnArrow.setImageResource(R.drawable.ic_arrow_up);
           }
       }

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
        val diffResult = calculateDiff(diffUtil)
        values = filteredList
        diffResult.dispatchUpdatesTo(this)

    }
}