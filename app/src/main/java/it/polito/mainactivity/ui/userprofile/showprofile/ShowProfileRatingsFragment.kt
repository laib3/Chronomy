package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileRatingsBinding
import it.polito.mainactivity.model.*

import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.viewModel.UserProfileViewModel

class ShowProfileRatingsFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileRatingsBinding? = null
    private val binding get() = _binding!!
    private val vmTimeslots: TimeslotViewModel by activityViewModels()
    private val vmUser: UserProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileRatingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // If show profile of other users
        val selectedUserId = if (timeslotId != null) {
            val ts = vmTimeslots.timeslots.value?.find { t -> t.timeslotId == timeslotId }
            ts?.publisher?.get("userId") as String
        } else { // if show profile of the current publisher
            vmUser.user.value?.userId!!
        }

        val rbAvgRatingPublisher: RatingBar = binding.rbAvgRatingPublisher
        val rbAvgRatingClient: RatingBar = binding.rbAvgRatingClient
        val rvPublisher: RecyclerView = binding.rvCommentsPublisher
        val rvClient: RecyclerView = binding.rvCommentsClient
        rvPublisher.layoutManager = LinearLayoutManager(root.context)
        rvClient.layoutManager = LinearLayoutManager(root.context)

        vmTimeslots.timeslots.observe(viewLifecycleOwner) {

            val ratingsAsPublisher: MutableList<RatingWithUserInfo> =
                it.filter { timeslot ->
                    (timeslot.publisher["userId"] == selectedUserId) &&
                            (timeslot.status == Timeslot.Status.COMPLETED) &&
                            (timeslot.ratings.find { rating -> rating.by == Message.Sender.CLIENT }?.rating != -1)
                }
                    .map { timeslot ->
                        RatingWithUserInfo(timeslot.ratings.first { rating -> rating.by == Message.Sender.CLIENT },
                            timeslot.chats.first { c -> c.assigned }.client["nickname"] as String,
                            timeslot.chats.first { c -> c.assigned }.client["profilePictureUrl"] as String
                        )
                    }.toMutableList()

            val ratingsAsClient: MutableList<RatingWithUserInfo> =
                it.filter { timeslot ->
                    timeslot.publisher["userId"] != selectedUserId &&
                            timeslot.chats.filter { chat -> chat.assigned }
                                .map { chat -> chat.client["userId"] }
                                .firstOrNull() == selectedUserId &&
                            timeslot.status == Timeslot.Status.COMPLETED &&
                            timeslot.ratings.find { rating -> rating.by == Message.Sender.PUBLISHER }?.rating != -1
                }
                    .map { timeslot ->
                        RatingWithUserInfo(
                            timeslot.ratings.first { rating -> rating.by == Message.Sender.PUBLISHER },
                            timeslot.publisher["nickname"] as String,
                            timeslot.publisher["profilePictureUrl"] as String
                        )
                    }.toMutableList()

            val publisherListAdapter = RatingAdapter(ratingsAsPublisher, this)
            rvPublisher.adapter = publisherListAdapter

            val clientListAdapter = RatingAdapter(ratingsAsClient, this)
            rvClient.adapter = clientListAdapter

            rbAvgRatingPublisher.rating =
                ratingsAsPublisher.map { r -> r.rating.rating }.average().toFloat()
            rbAvgRatingClient.rating =
                ratingsAsClient.map { r -> r.rating.rating }.average().toFloat()

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RatingAdapter(
    private val ratingsList: List<RatingWithUserInfo>,
    private val parentFragment: Fragment,
) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.rating_item, parent, false)
        return RatingViewHolder(vg)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = ratingsList[position]

        val tvUserNickname: TextView = holder.itemView.findViewById(R.id.tvUserNickname)
        val tvComment: TextView = holder.itemView.findViewById(R.id.tvComment)
        val ratingBar: RatingBar = holder.itemView.findViewById(R.id.rbRating)
        val userPic: ImageView = holder.itemView.findViewById(R.id.iUserPic)
        tvUserNickname.text = String.format(
            parentFragment.requireActivity().getString(R.string.user_profile_nickname_placeholder),
            rating.nickname
        )
        if (rating.rating.comment.isBlank()) {
            tvComment.visibility = View.GONE
            holder.itemView.findViewById<View>(R.id.divider).visibility = View.GONE
        } else {
            tvComment.text = String.format(
                parentFragment.requireActivity().getString(R.string.rating_comment_placeholder),
                rating.rating.comment
            )
        }
        ratingBar.rating = rating.rating.rating.toFloat()
        Picasso.get().load(rating.profilePictureUrl).into(userPic)
        userPic.clipToOutline = true
    }

    override fun getItemCount(): Int = ratingsList.size

}