package it.polito.mainactivity.ui.userprofile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import it.polito.mainactivity.R

class SkillCard(val c: Context, val f: Fragment, val s: SkillViewModel): CardView(c){

    init {
        LayoutInflater.from(c).inflate(R.layout.skillcard, this, true)
        val tvDescription = findViewById<TextView>(R.id.skillDescription)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)

        s.description.observe(f.viewLifecycleOwner){ tvDescription.text = it }
        // on title change also picture
        s.title.observe(f.viewLifecycleOwner){
            tvTitle.text = it
            val imgRes = getImgRes(it)
            if(imgRes != null)
                ivSkillIcon.setImageResource(imgRes)
        }
        s.active.observe(f.viewLifecycleOwner){ if(it != true) visibility = View.GONE else visibility = View.VISIBLE }
    }

    private fun getImgRes(title: String) : Int?{
        return when(title){
            "Gardening" -> R.drawable.ic_skill_gardening
            "Tutoring" -> R.drawable.ic_skill_tutoring
            "Child Care" -> R.drawable.ic_skill_child_care
            "Odd Jobs" -> R.drawable.ic_skill_odd_jobs
            "Home Repair" -> R.drawable.ic_skill_home_repair
            "Wellness" -> R.drawable.ic_skill_wellness
            "Delivery" -> R.drawable.ic_skill_delivery
            "Transportation" -> R.drawable.ic_skill_transportation
            "Companionship" -> R.drawable.ic_skill_companionship
            "Other" -> R.drawable.ic_skill_other
            else -> null
        }
    }

}
