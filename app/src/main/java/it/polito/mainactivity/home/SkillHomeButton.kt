package it.polito.mainactivity.home

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Utils

internal class SkillHomeButton (private val c: Context, private val category:String): CardView(c){

    init {
        LayoutInflater.from(c).inflate(R.layout.skill_home_button, this, true)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)

        // set the icon
        val imgRes = Utils.getSkillImgRes(category)
        if(imgRes != null)
            ivSkillIcon.setImageResource(imgRes)
        //set title
        tvTitle.text = category
     }
}