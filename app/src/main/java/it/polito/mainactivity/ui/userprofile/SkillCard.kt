package it.polito.mainactivity.ui.userprofile

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import it.polito.mainactivity.R

//class SkillCard(val c:Context, val title:String, val description:String, var active:Boolean): CardView(c){
class SkillCard(val c:Context, val f: Fragment, val s:Skill): CardView(c){

    // val tvDescription = findViewById<TextView>(R.id.tmpTitle)

    init {
        LayoutInflater.from(c).inflate(R.layout.skillcard_tmp, this, true)
        // s.description.observe(f.viewLifecycleOwner){ tvDescription.text = it }
        val tvDescription = findViewById<TextView>(R.id.tmpTitle)
        s.description.observe(f.viewLifecycleOwner){ tvDescription.text = it }
    }
}

/*
class SkillCard(c: Context, s:Skill): CardView(c){

    init {
        LayoutInflater.from(c).inflate(R.layout.skill_card, this, true)
        this.setBackgroundDrawable(null)
        val cardTitle = findViewById<TextView>(R.id.skillTitle)
        cardTitle.text = s.title
        val desc = findViewById<TextView>(R.id.skillDescription)
        desc.text = s.description
        val icon = findViewById<ImageView>(R.id.skillIcon)
        icon.contentDescription = s.title + " icon"
        icon.setImageResource(resources.getIdentifier(s.src, "drawable", "it.polito.showprofileactivity" ))
    }
}
*/