package it.polito.mainactivity.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentFilteredTimeslotListBinding
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.User
import it.polito.mainactivity.placeholder.PlaceholderContent
import it.polito.mainactivity.ui.timeslot.timeslot_list.TimeslotAdapter
import java.util.*


class FilteredTimeslotListFragment : Fragment() {

    private val values : List<Pair<Timeslot, User>> = listOf(
        Pair(
            Timeslot("Bring grocery shopping to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you. I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to, but please don't choose those outside of the neighbourhood.",
                GregorianCalendar(2022, 5, 25),
                "09:00", "10:00",
                "New Neighbourhood, Street 10, Sydney",
                "Delivery",
                null,
                listOf(GregorianCalendar(2022, 5, 25).get(Calendar.DAY_OF_WEEK)),
                GregorianCalendar(2022, 5, 25)
            ),
            User(
                1, "Mario", "Rossi", "@marione", "Lorem ipsum sit nunquam",
                "mario.rossi@gmail.com", "Crocetta, Torino", "+391234567890",
                listOf("Gardening", "Wellness", "Tutoring"), 0,
            )
        ),
        Pair(
            Timeslot("Walk your dog",
                "Donec eu dui nec nisl egestas tristique suscipit non mauris. Nullam nec magna neque. Quisque a quam sodales quam dapibus euismod non et diam. Nulla molestie ex orci, vitae suscipit velit viverra non. Phasellus diam massa, sollicitudin ac interdum commodo.",
                GregorianCalendar(2022, 5, 20),
                "11:30", "14:00",
                "New City, Street 10, Anastasia",
                "Other",
                null,
                listOf(GregorianCalendar(2022, 5, 20).get(Calendar.DAY_OF_WEEK)),
                GregorianCalendar(2022, 5, 20)),
            User(
                1, "Mario", "Rossi", "@linda", "Lorem ipsum sit nunquam",
                "mario.rossi@gmail.com", "Crocetta, Torino", "+391234567890",
                listOf("Gardening", "Wellness", "Tutoring"), 0,

            )
        ),
        Pair(
            Timeslot("Teach to your kid",
                "Praesent euismod est ac dictum gravida. Praesent nulla metus, ultrices eu tempor ac, pretium viverra nisi. Morbi odio urna, ornare sit amet dictum in, commodo vel mauris. Vivamus et massa quis lorem iaculis laoreet. Vestibulum eros diam, condimentum ac libero eget, lobortis fringilla orci.",
                GregorianCalendar(2022, 5, 20),
                "14:00", "16:00",
                "New Neighbourhood, Street 10, Sydney",
                "Tutoring",
                null,
                listOf(GregorianCalendar(2022, 5, 20).get(Calendar.DAY_OF_WEEK)),
                GregorianCalendar(2022, 5, 20)),
            User(
                1, "Mario", "Rossi", "@toska", "Lorem ipsum sit nunquam",
                "mario.rossi@gmail.com", "Crocetta, Torino", "+391234567890",
                listOf("Gardening", "Wellness", "Tutoring"), 0,

            )
        ),
        Pair(
            Timeslot("Personal YOGA teacher",
                "Phasellus fermentum sagittis leo finibus fringilla. Proin est magna, varius ut arcu lobortis, imperdiet facilisis ex. Mauris varius at metus nec faucibus. Fusce et dapibus ipsum. In hac habitasse platea dictumst. Duis arcu nulla, imperdiet quis placerat eget.",
                GregorianCalendar(2022, 5, 30),
                "09:00", "10:00",
                "New Neighbourhood, Street 10, Sydney",
                "Wellness",
                "Monthly",
                listOf(GregorianCalendar(2022, 5, 30).get(Calendar.DAY_OF_WEEK)),
                GregorianCalendar(2022, 8, 30)
            ),

            User(
                1, "Mario", "Rossi", "@simorobu", "Lorem ipsum sit nunquam",
                "mario.rossi@gmail.com", "Crocetta, Torino", "+391234567890",
                listOf("Gardening", "Wellness", "Tutoring"), 0,

            )
        ),
        Pair(
            Timeslot("Bring SMTH to your door",
                "Curabitur eu accumsan massa, sed molestie magna. Sed pharetra arcu in leo vulputate feugiat. Nulla finibus dolor non maximus efficitur. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; In nibh mi, posuere vel mi id, pretium consectetur velit.",
                GregorianCalendar(2022, 6, 1),
                "16:10", "17:00",
                "Via Vincenzo Vela, 49, Torino, To, 10128",
                "Delivery",
                "Weekly",
                listOf(1, 2, 3),
                GregorianCalendar(2022, 8, 5)),

            User(
                1, "Mario", "Rossi", "@unika", "Lorem ipsum sit nunquam",
                "mario.rossi@gmail.com", "Crocetta, Torino", "+391234567890",
                listOf("Gardening", "Wellness", "Tutoring"), 0,

            )
        )
        )
    private var _binding: FragmentFilteredTimeslotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFilteredTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        val adapter = TimeslotsRecyclerViewAdapter(values, this)
        rv.adapter = adapter

        /* val view = inflater.inflate(R.layout.fragment_filtered_timeslot_list_list, container, false)

         // Set the adapter
         if (view is RecyclerView) {
             with(view) {
                 layoutManager = when {
                     columnCount <= 1 -> LinearLayoutManager(context)
                     else -> GridLayoutManager(context, columnCount)
                 }
                 adapter = TimeslotsRecyclerViewAdapter(values, this)
             }
         }
         return view

         */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}