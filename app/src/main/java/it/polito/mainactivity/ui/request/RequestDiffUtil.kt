package it.polito.mainactivity.ui.request

import androidx.recyclerview.widget.DiffUtil
import it.polito.mainactivity.model.Timeslot

class RequestDiffUtil(
    private val oldList: List<Timeslot>,
    private val newList: List<Timeslot>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
       return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timeslotId == newList[newItemPosition].timeslotId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].timeslotId != newList[newItemPosition].timeslotId ->{
                false
            }
            oldList[oldItemPosition].title != newList[newItemPosition].title ->{
                false
            }
            oldList[oldItemPosition].description != newList[newItemPosition].description ->{
                false
            }
            oldList[oldItemPosition].date != newList[newItemPosition].date ->{
                false
            }
            oldList[oldItemPosition].startHour != newList[newItemPosition].startHour ->{
                false
            }
            oldList[oldItemPosition].endHour != newList[newItemPosition].endHour ->{
                false
            }
            oldList[oldItemPosition].location != newList[newItemPosition].location ->{
                false
            }
            oldList[oldItemPosition].category != newList[newItemPosition].category ->{
                false
            }
            /*
            oldList[oldItemPosition].days != newList[newItemPosition].days ->{
                false
            }*/
            else -> true
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}