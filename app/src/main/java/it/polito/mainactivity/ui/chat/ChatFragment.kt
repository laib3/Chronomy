package it.polito.mainactivity.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentChatListBinding
import it.polito.mainactivity.model.*
import it.polito.mainactivity.viewModel.TimeslotViewModel


class ChatFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    var adapter: MessageRecyclerViewAdapter? = null

    var ts: Timeslot? = null
    var chat: Chat? = null

    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.list
        rv.layoutManager = LinearLayoutManager(root.context)

        vm.timeslots.observe(viewLifecycleOwner) { timeslots ->
            ts = timeslots?.firstOrNull { it.timeslotId == args.timeslotId }
            chat = ts?.chats?.firstOrNull { it.chatId == args.chatId }
            if (chat == null)
                findNavController().navigateUp()
            else {
                if (ts == null)
                    findNavController().navigateUp()
                else {
                    adapter = MessageRecyclerViewAdapter(
                        chat!!, ts!!, this
                    )
                    rv.adapter = adapter
                    rv.scrollToPosition(chat!!.messages.size - 1);


                    val userRole = Utils.getUserRole(ts!!, chat!!)
                    val userRating = ts!!.ratings.find { r -> r.by == userRole }?.rating
                    val userRatingComment = ts!!.ratings.find { r -> r.by == userRole }?.comment
                    if (userRating != null && userRating != -1) {
                        disableRatingArea(userRating, userRatingComment)

                    }

                    when (ts!!.status) {
                        Timeslot.Status.PUBLISHED -> {
                            binding.btnsManageReq.visibility = View.VISIBLE
                            binding.ratingZone.visibility = View.GONE
                            binding.clCompleted.visibility = View.GONE
                        }
                        Timeslot.Status.COMPLETED -> {
                            binding.ratingZone.visibility = View.VISIBLE
                            binding.textMsg.visibility = View.GONE
                            binding.bSendMsg.visibility = View.GONE
                            binding.btnsManageReq.visibility = View.GONE
                            binding.clCompleted.visibility = View.GONE
                        }
                        Timeslot.Status.ASSIGNED -> {
                            binding.btnsManageReq.visibility = View.GONE
                            binding.clCompleted.visibility = View.VISIBLE
                            binding.ratingZone.visibility = View.GONE
                            if (!(chat!!.assigned)) { // timeslot has been assigned to someone else
                                binding.cvAlreadyAssigned.visibility = View.VISIBLE
                                binding.textMsg.visibility = View.GONE
                                binding.bSendMsg.visibility = View.GONE
                            }
                        }
                        else -> {}
                    }
                    if (Utils.getUserRole(ts!!, chat!!) == Message.Sender.CLIENT)
                        binding.btnsManageReq.visibility = View.GONE
                        binding.clCompleted.visibility = View.GONE
                }

                binding.bSendMsg.setOnClickListener {
                    vm.addMessage(args.chatId, binding.textMsg.text.toString())
                    binding.textMsg.setText("")
                }

                binding.bReject.setOnClickListener {
                    vm.reject(chat?.chatId)
                    findNavController().navigateUp()
                }

                binding.bAccept.setOnClickListener {
                    vm.setChatAssigned(chat!!.chatId, true)
                }

                binding.bCompleted.setOnClickListener {
                    vm.updateTimeslotField(ts!!.timeslotId, "status", Timeslot.Status.COMPLETED)
                }

                binding.btnSendFeedback.setOnClickListener {
                    val userRole = Utils.getUserRole(ts!!, chat!!)
                    val userRating = ts!!.ratings.find { r -> r.by == userRole }?.rating
                    val userRatingComment = ts!!.ratings.find { r -> r.by == userRole }?.comment
                    disableRatingArea(userRating!!,userRatingComment)
                    vm.updateRating(
                        ts!!.timeslotId,
                        binding.ratingBar.rating.toInt(),
                        binding.textInputComment.editText?.text.toString()
                    )
                }

            }
        }
        return root
    }

    fun disableRatingArea(userRating: Int, userRatingComment: String?) {
        binding.ratingBar.rating = userRating.toFloat()
        binding.ratingBar.isEnabled = false
        userRatingComment?.let{binding.textInputEditTextSkillDescription.setText(userRatingComment)}
        binding.textInputEditTextSkillDescription.isEnabled = false
        binding.btnSendFeedback.isEnabled = false
        binding.btnSendFeedback.text = "Feedback sent"
        binding.btnSendFeedback.setBackgroundColor(resources.getColor(R.color.dark_grey))
        binding.btnSendFeedback.setTextColor(
            ColorStateList.valueOf(
                requireParentFragment().resources.getColor(R.color.white)
            )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}