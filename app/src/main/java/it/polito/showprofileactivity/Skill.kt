package it.polito.showprofileactivity

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

class SkillCard(c: Context, s:Skill): CardView(c){
    init {
        LayoutInflater.from(c).inflate(R.layout.skill_card, this, true)
        val cardTitle = findViewById<TextView>(R.id.skillTitle)
        cardTitle.text = s.title
        val desc = findViewById<TextView>(R.id.skillDescription)
        desc.text = s.description
        val icon = findViewById<ImageView>(R.id.skillIcon)
        icon.contentDescription = s.title + " icon"
        icon.setImageResource(resources.getIdentifier(s.src, "drawable", "it.polito.showprofileactivity" ))
    }
}

class Skill (var title:String, var src:String){

    public var description: String = ""
    public var active: Boolean = false

    constructor(title:String, src:String, desc:String): this(title, src) {
        this.description = desc
    }
    constructor(title:String, src:String, active:Boolean, desc:String): this(title, src) {
        this.description = desc
        this.src = src
        this. active = active
    }
}

