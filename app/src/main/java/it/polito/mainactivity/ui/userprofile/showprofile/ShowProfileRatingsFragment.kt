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
import kotlin.math.roundToInt

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
        var currentUserId: String? = null
        var currentUserNickname: String? = null
        var currentUserProfilePictureUrl: String? = null
        if (timeslotId != null) {
            vmTimeslots.timeslots.value?.find { t -> t.timeslotId == timeslotId }
                ?.also {
                    currentUserId = it.publisher["userId"] as String
                    currentUserNickname = it.publisher["nickname"] as String
                    currentUserProfilePictureUrl = it.publisher["profilePictureUrl"] as String
                }
        } else { // if show profile of the current publisher
            currentUserId = vmUser.user.value?.userId!!
            currentUserNickname = vmUser.user.value?.nickname!!
            currentUserProfilePictureUrl = vmUser.user.value?.profilePictureUrl!!
        }


        val rbAvgRatingPublisher: RatingBar = binding.rbAvgRatingPublisher
        val rbAvgRatingClient: RatingBar = binding.rbAvgRatingClient
        val rvPublisher: RecyclerView = binding.rvCommentsPublisher
        val rvClient: RecyclerView = binding.rvCommentsClient
        rvPublisher.layoutManager = LinearLayoutManager(root.context)
        rvClient.layoutManager = LinearLayoutManager(root.context)

        vmTimeslots.timeslots.observe(viewLifecycleOwner) {

            val ratingsAsPublisher: MutableList<RatingWithUserInfo> = mutableListOf()
            it.filter { timeslot ->
                (timeslot.publisher["userId"] == currentUserId) &&
                        (timeslot.status == Timeslot.Status.COMPLETED) &&
                        (timeslot.ratings.find { rating -> rating.by == Message.Sender.CLIENT }?.rating != -1)
            }
                .map { timeslot ->
                    RatingWithUserInfo(timeslot.ratings.filter { rating -> rating.by == Message.Sender.CLIENT }
                        .first(), currentUserNickname, currentUserProfilePictureUrl)
                }

            val ratingsAsClient: MutableList<RatingWithUserInfo> = mutableListOf()
            it.filter { timeslot ->
                timeslot.publisher["userId"] != currentUserId &&
                        timeslot.chats.filter { chat -> chat.assigned }
                            .map { chat -> chat.client["userId"] }[0] == currentUserId &&
                        timeslot.status == Timeslot.Status.COMPLETED &&
                        timeslot.ratings.find { rating -> rating.by == Message.Sender.PUBLISHER }?.rating != -1
            }
                .map { timeslot ->
                    RatingWithUserInfo(timeslot.ratings.filter { rating -> rating.by == Message.Sender.PUBLISHER }
                        .first(), currentUserNickname, currentUserProfilePictureUrl)
                }

            val publisherListAdapter = RatingAdapter(ratingsAsPublisher, this)
            rvPublisher.adapter = publisherListAdapter

            val clientListAdapter = RatingAdapter(ratingsAsClient, this)
            rvClient.adapter = clientListAdapter

            rbAvgRatingPublisher.numStars =
                ratingsAsPublisher.map { r -> r.rating.rating }.average().roundToInt()
            rbAvgRatingClient.numStars =
                ratingsAsClient.map { r -> r.rating.rating }.average().roundToInt()

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
        val tvUserNickname: TextView = v.findViewById(R.id.tvUserNickname)
        val tvComment: TextView = v.findViewById(R.id.tvComment)
        val ratingBar: RatingBar = v.findViewById(R.id.rbRating)
        val userPic: ImageView = v.findViewById(R.id.iUserPic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false)
        return RatingViewHolder(vg)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = ratingsList[position]

        holder.tvUserNickname.text = rating.nickname
        holder.tvComment.text = rating.rating.comment
        holder.ratingBar.numStars = rating.rating.rating
        Picasso.get().load(rating.profilePictureUrl).into(holder.userPic)
    }

    override fun getItemCount(): Int = ratingsList.size

}