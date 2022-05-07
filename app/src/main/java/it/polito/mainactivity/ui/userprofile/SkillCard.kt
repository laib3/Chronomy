package it.polito.mainactivity.ui.userprofile

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Skill

class SkillCard(val c: Context, val skill: Skill, val vm: UserProfileViewModel, val editable: Boolean): CardView(c){

    init {
        LayoutInflater.from(c).inflate(R.layout.skillcard, this, true)
        val tvDescription = findViewById<TextView>(R.id.skillDescription)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)
        // set the icon
        val imgRes = getImgRes(skill.title)
        if(imgRes != null)
            ivSkillIcon.setImageResource(imgRes)
        tvTitle.text = skill.title
        tvDescription.text = skill.description
        val iconEye = findViewById<ImageView>(R.id.eyeSlashedIcon)

        // hide if not active
        if(!editable && !skill.active)
            this.visibility = GONE

        if(editable){
            enableEdit()
            if(!skill.active){
                iconEye.visibility = VISIBLE
                findViewById<ConstraintLayout>(R.id.skillCardInnerLayout).setBackgroundColor(resources.getColor(R.color.light_grey))
            }
        }
    }

    private fun enableEdit(){
        this.setOnClickListener {
            val modalView = LayoutInflater.from(this.context).inflate(R.layout.skill_edit_modal, null)
            val modalTitle = modalView.findViewById<TextView>(R.id.modalTitle)
            val modalChecked = modalView.findViewById<SwitchCompat>(R.id.skillActiveSwitch)
            val modalDescription = modalView.findViewById<TextInputEditText>(R.id.textInputEditTextSkillDescription)
            val mBuilder = android.app.AlertDialog.Builder(this.context).setView(modalView)
            val alertDialog = mBuilder.show()
            val closeButton = modalView.findViewById<ImageView>(R.id.modalCloseButton)
            val saveButton = modalView.findViewById<Button>(R.id.modalSaveButton)
            modalDescription.setText(skill.description)
            modalTitle.text = skill.title
            modalChecked.isChecked = skill.active
            closeButton.setOnClickListener{
                alertDialog.dismiss()
            }
            /* submit only when you click on save */
            saveButton.setOnClickListener{
                alertDialog.dismiss()
                val checked: Boolean = modalChecked.isChecked
                val desc: String = modalDescription.text.toString()
                Log.d("DBG_SK_DESC",skill.description)
                Log.d("DBG_DESC", desc)
                Log.d("DBG_SK_ACTIVE", skill.active.toString())
                Log.d("DBG_ACTIVE", checked.toString())

                if(skill.description != desc || skill.active != checked){
                    val newSkill = skill.copy().apply { active = checked; description = desc }
                    vm.setUpdated(newSkill)
                }
            }
        }
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
