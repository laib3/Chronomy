package it.polito.mainactivity.ui.request

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.firebase.database.collection.LLRBNode
import com.squareup.picasso.Picasso
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Message
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import java.text.SimpleDateFormat
import java.util.*

class RequestRecyclerViewAdapter(
    private var values: List<Timeslot>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvUserNickname)
        val ivCategory: ImageView = v.findViewById(R.id.iUserPic)
        val btnArrow: ImageButton = v.findViewById(R.id.arrow_button)
        val hiddenView: LinearLayout = v.findViewById(R.id.hidden_view)
        val baseCardView: CardView = v.findViewById(R.id.base_cardview)
        val chipCount: Chip = v.findViewById(R.id.chip)
        val tvDate: TextView = v.findViewById(R.id.tvRequestTimeslotDate)
        val fixedLayout: ConstraintLayout = v.findViewById(R.id.fixed_layout)
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

        holder.hiddenView.removeAllViews()
        val inflater = LayoutInflater.from(parentFragment.context)

        for (chat: it.polito.mainactivity.model.Chat in ts.chats) {
            if(chat.client.isNotEmpty()){
                chat.messages.sortBy{ msg -> msg.timestamp }
                val chatCard = inflater.inflate(R.layout.chat_card, null, false) as ConstraintLayout
                chatCard.findViewById<TextView>(R.id.tvNickname).apply {
                    this.text = chat.client["nickname"] as String
                }
                chatCard.findViewById<TextView>(R.id.tvDate).apply {
                    val c = Calendar.getInstance()
                    if (chat.messages.isNotEmpty()) {
                        c.time = chat.messages.last().timestamp.toDate()
                        this.text = SimpleDateFormat("dd/MM/yy - HH:mm", Locale.ITALY).format(c.time)
                    } else{
                        this.visibility = View.INVISIBLE
                    }
                }
                chatCard.findViewById<TextView>(R.id.tvMsg).apply {
                    this.text =
                        if (chat.messages.isNotEmpty()) chat.messages.last().text
                    else
                        ""
                }
                val ivProfilePic = chatCard.findViewById<ImageView>(R.id.ivProfilePic)
                ivProfilePic.apply{
                    this.clipToOutline = true
                    if(chat.client["profilePictureUrl"]!= null){
                        Picasso.get().load(chat.client["profilePictureUrl"] as String).into(this)
                    }
                }
                chatCard.findViewById<TextView>(R.id.tvStatus).apply{
                    if(chat.assigned){
                        if(ts.status == Timeslot.Status.COMPLETED){
                            this.text = "Completed"
                            this.setTextColor(ColorStateList.valueOf(parentFragment.resources.getColor(R.color.purple_500)))
                        } else if(ts.status == Timeslot.Status.ASSIGNED) {
                            this.text = "Assigned"
                            this.setTextColor(ColorStateList.valueOf(parentFragment.resources.getColor(R.color.dark_green)))
                        }
                    } else {
                        this.text = "Not assigned"
                        this.setTextColor(ColorStateList.valueOf(parentFragment.resources.getColor(R.color.medium_grey)))
                    }
                }

                // Pass through bundle the id of the item in the list
                chatCard.setOnClickListener {
                    val action =
                        RequestsFragmentDirections.actionNavRequestsToChatFragment(
                            chat.chatId,
                            ts.timeslotId,
                            ts.title
                        )
                    parentFragment.findNavController().navigate(action)
                }

                holder.hiddenView.addView(chatCard)
            }
        }

        holder.chipCount.text = holder.hiddenView.childCount.toString()
        holder.chipCount.chipBackgroundColor =
            when(ts.status){
                Timeslot.Status.ASSIGNED -> ColorStateList.valueOf(parentFragment.resources.getColor(R.color.light_green))
                Timeslot.Status.COMPLETED -> ColorStateList.valueOf(parentFragment.resources.getColor(R.color.purple_200))
                else -> ColorStateList.valueOf(parentFragment.resources.getColor(R.color.light_grey))
            }

        holder.tvDate.text = Utils.formatDateToString(ts.date)

        //TODO: navigate to timeslot details
        holder.fixedLayout.setOnClickListener { showHiddenLayout(holder) }

        holder.btnArrow.setOnClickListener { showHiddenLayout(holder) }
    }

    override fun getItemCount(): Int = values.size

    fun filterList(filteredList: List<Timeslot>) {
        if(filteredList == null)
            return
        val diffUtil = RequestDiffUtil(values, filteredList)
        val diffResult = calculateDiff(diffUtil)
        values = filteredList
        diffResult.dispatchUpdatesTo(this)
    }

    fun showHiddenLayout(holder: RequestViewHolder) {

        // If the CardView is already expanded, set its visibility
        //  to gone and change the expand less icon to expand more.
        if (holder.hiddenView.visibility == View.VISIBLE) {
            val t: Transition = Fade()
            t.duration = 600
            t.addTarget(holder.hiddenView.id)

            TransitionManager.beginDelayedTransition(
                holder.baseCardView,
                t
            )
            holder.hiddenView.visibility = View.GONE;
            holder.btnArrow.setImageResource(R.drawable.ic_arrow_down);
        } else {
            val t: Transition = Fade()
            t.duration = 600
            t.addTarget(holder.hiddenView.id)

            TransitionManager.beginDelayedTransition(
                holder.baseCardView,
                t
            )
            holder.hiddenView.visibility = View.VISIBLE;
            holder.btnArrow.setImageResource(R.drawable.ic_arrow_up);
        }
    }
}