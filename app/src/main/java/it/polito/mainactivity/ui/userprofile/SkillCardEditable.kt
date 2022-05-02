package it.polito.mainactivity.ui.userprofile

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import it.polito.mainactivity.R

class SkillCardEditable(val c: Context, val f: Fragment, val s: SkillViewModel): CardView(c){

    init {
        LayoutInflater.from(c).inflate(R.layout.skillcard, this, true)
        val tvDescription = findViewById<TextView>(R.id.skillDescription)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)

        s.description.observe(f.viewLifecycleOwner){ tvDescription.text = it }

        this.setOnClickListener {
            val modalView = LayoutInflater.from(this.context).inflate(R.layout.skill_edit_modal, null)
            val modalTitle = modalView.findViewById<TextView>(R.id.modalTitle)
            val modalChecked = modalView.findViewById<SwitchCompat>(R.id.skillActiveSwitch)
            val modalDescription = modalView.findViewById<TextInputEditText>(R.id.textInputEditTextSkillDescription)
            val mBuilder = android.app.AlertDialog.Builder(this.context).setView(modalView)
            val alertDialog = mBuilder.show()
            val closeButton = modalView.findViewById<ImageView>(R.id.modalCloseButton)
            val saveButton = modalView.findViewById<Button>(R.id.modalSaveButton)
            modalDescription.setText(s.description.value)
            modalTitle.text = s.title.value
            modalChecked.isChecked = s.active.value!!
            modalChecked.setOnCheckedChangeListener { _, checked -> s.setActive(checked) }
            closeButton.setOnClickListener{
                alertDialog.dismiss()
            }
            saveButton.setOnClickListener{
                s.setDescription(modalDescription.text.toString())
                alertDialog.dismiss()
            }
        }

        s.description.observe(f.viewLifecycleOwner){ tvDescription.text = it }
        // on title change also picture
        s.title.observe(f.viewLifecycleOwner){
            tvTitle.text = it
            val imgRes = getImgRes(it)
            if(imgRes != null)
                ivSkillIcon.setImageResource(imgRes)
        }
        s.active.observe(f.viewLifecycleOwner){
            // not active cards have grey background
            val bgColor =
                if(it != true)
                    resources.getColor(R.color.light_grey)
                else
                    resources.getColor(R.color.not_so_dark_slate_blue)
            findViewById<ConstraintLayout>(R.id.skillCardInnerLayout).setBackgroundColor(bgColor)
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
