package it.polito.mainactivity.ui.request

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.Utils
import java.util.*


class Chat(val nickname: String, val d: Calendar, val msg: String) {

}

class RequestRecyclerViewAdapter(
    private var values: List<Timeslot>,
    private val parentFragment: Fragment
) : RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val ivCategory: ImageView = v.findViewById(R.id.lCategory)
        val btnArrow: ImageButton = v.findViewById(R.id.arrow_button)
        val hiddenView: LinearLayout = v.findViewById(R.id.hidden_view)
        val baseCardView: CardView = v.findViewById(R.id.base_cardview)
        val chipCount: Chip = v.findViewById(R.id.chip)
        val fixedLayout:ConstraintLayout = v.findViewById(R.id.fixed_layout)
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
        val chat_infos = listOf(
            Chat(
                "@utente1",
                GregorianCalendar(2022, 4, 5),
                "Ciao! Non sono sicuo di aver capito che cosa intendi con questo."
            ),
            Chat(
                "@utente2",
                GregorianCalendar(2022, 4, 6),
                "Sei sicuro che la zona è quella giusta?"
            ),
            Chat("@utente3", GregorianCalendar(2022, 4, 7), "Sono molto interessato!")
        )
        val inflater = LayoutInflater.from(parentFragment.context)

        for (chat: Chat in chat_infos) {
            val chatCard = inflater.inflate(R.layout.chat_card, null, false) as ConstraintLayout
            chatCard.findViewById<TextView>(R.id.tvNickname).apply {
                this.text = chat.nickname
            }
            chatCard.findViewById<TextView>(R.id.tvDate).apply {
                this.text = Utils.formatDateToString(chat.d)
            }
            chatCard.findViewById<TextView>(R.id.tvMsg).apply {
                this.text = chat.msg
            }
            holder.hiddenView.addView(chatCard)
        }

        holder.chipCount.text = holder.hiddenView.childCount.toString()

        holder.fixedLayout.setOnClickListener { showHiddenLayout(holder)}
        holder.btnArrow.setOnClickListener { showHiddenLayout(holder) }
    }
    override fun getItemCount(): Int = values.size

    fun filterList(filteredList: List<Timeslot>) {
        val diffUtil = RequestDiffUtil(values, filteredList)
        val diffResult = calculateDiff(diffUtil)
        values = filteredList
        diffResult.dispatchUpdatesTo(this)

    }

    fun showHiddenLayout(holder : RequestViewHolder){

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