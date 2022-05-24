package it.polito.mainactivity.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Utils

internal class SkillHomeButton(
    private val c: Context,
    val category: String,
    private val number: Int
) :
    CardView(c) {

    init {
        LayoutInflater.from(c).inflate(R.layout.skill_home_button, this, true)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)
        val tvSkillNumber = findViewById<TextView>(R.id.tvSkillNumber)

        // set the icon
        val imgRes = Utils.getSkillImgRes(category)
        if (imgRes != null)
            ivSkillIcon.setImageResource(imgRes)
        //set title
        tvTitle.text = category
        tvSkillNumber.text = String.format(
            resources.getString(R.string.home_skill_number_placeholder),
            number.toString()
        )
    }
}