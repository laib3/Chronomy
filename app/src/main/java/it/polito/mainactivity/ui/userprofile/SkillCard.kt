package it.polito.mainactivity.ui.userprofile

import android.content.Context
import android.view.FocusFinder
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Skill
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.userprofile.editprofile.EditProfileFieldsFragment
import it.polito.mainactivity.viewModel.UserProfileViewModel

class SkillCard(
    private val c: Context,
    private val skill: Skill,
    val vm: UserProfileViewModel,
    private val editable: Boolean,
    private val editTextList: MutableList<EditText>
) : CardView(c) {

    init {
        LayoutInflater.from(c).inflate(R.layout.skillcard, this, true)
        val tvDescription = findViewById<TextView>(R.id.skillDescription)
        val tvTitle = findViewById<TextView>(R.id.skillTitle)
        val ivSkillIcon = findViewById<ImageView>(R.id.skillIcon)
        // set the icon
        val imgRes = Utils.getSkillImgRes(skill.category)
        if (imgRes != null)
            ivSkillIcon.setImageResource(imgRes)
        tvTitle.text = skill.category
        tvDescription.text = skill.description
        val iconEye = findViewById<ImageView>(R.id.eyeSlashedIcon)

        // hide if not active
        if (!editable && !skill.active)
            this.visibility = GONE

        if (editable) {
            enableEdit()
            if (!skill.active) {
                iconEye.visibility = VISIBLE
                findViewById<ConstraintLayout>(R.id.skillCardInnerLayout).setBackgroundColor(
                    resources.getColor(R.color.light_grey)
                )
            }
        }
    }

    private fun enableEdit() {
        this.setOnClickListener {
            val modalView =
                LayoutInflater.from(this.context).inflate(R.layout.skill_edit_modal, null)
            val modalTitle = modalView.findViewById<TextView>(R.id.modalTitle)
            val modalChecked = modalView.findViewById<SwitchCompat>(R.id.skillActiveSwitch)
            val modalDescription =
                modalView.findViewById<TextInputEditText>(R.id.textInputEditTextSkillDescription)
            val mBuilder = android.app.AlertDialog.Builder(this.context).setView(modalView)
            val alertDialog = mBuilder.show()
            val closeButton = modalView.findViewById<ImageView>(R.id.modalCloseButton)
            val saveButton = modalView.findViewById<Button>(R.id.btnApplyFilters)


            editTextList.forEach { if (it.isFocused) it.clearFocus() }
            // populate fields
            modalTitle.text = skill.category
            modalDescription.setText(skill.description)
            modalChecked.isChecked = skill.active
            // add click listeners
            closeButton.setOnClickListener {
                alertDialog.dismiss()
            }
            /* submit only when you click on save */
            saveButton.setOnClickListener {
                alertDialog.dismiss()
                val checked: Boolean = modalChecked.isChecked
                val desc: String = modalDescription.text.toString()

                if (skill.description != desc || skill.active != checked) {
                    vm.updateUserSkill(skill.category, checked, desc)
                }
            }
        }
    }


}
