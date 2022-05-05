package it.polito.mainactivity.ui.userprofile

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Skill

class TextSkill(val c: Context, val skill: Skill, val vm: UserProfileViewModel): AppCompatTextView(c) {

    init {
        this.text = skill.title
        if(!skill.active){
            this.setTextColor(resources.getColor(R.color.purple_200))
        }
        // update the Skill to communicate a change
        this.setOnClickListener {
            val s = skill.copy().apply{ active = !skill.active };
            vm.setUpdated(s) // propagate change
        }
    }

}
