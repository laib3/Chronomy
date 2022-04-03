package it.polito.showprofileactivity

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.*
import androidx.cardview.widget.CardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar.getInstance
import android.content.Context as Context


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

        /*this.setOnClickListener {
            //inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(c).inflate(R.layout.skill_edit_modal, null)

            val title = mDialogView.findViewById<TextView>(R.id.modalTitle)
            title.text = "Edit ${s.title}"
            val question = mDialogView.findViewById<TextView>(R.id.question)
            question.text = "Is ${s.title.toString().lowercase()} one of your skills?"

            val radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
            val radioButtonYES = radioGroup.findViewById<RadioButton>(R.id.radioButtonYES)
            val radioButtonNO = radioGroup.findViewById<RadioButton>(R.id.radioButtonNO)
            radioButtonYES.isChecked = s.active
            radioButtonNO.isChecked = !s.active

            val description = mDialogView.findViewById<EditText>(R.id.editDescription)
            description.setText(s.description)

            //AlertDialogBuilder + show
            val mBuilder = AlertDialog.Builder(c)
                .setView(mDialogView)
            //.setTitle("Edit Skill")
            val mAlertDialog = mBuilder.show()
            //dismiss button
            val closeButton = mDialogView.findViewById<ImageView>(R.id.close_button)
            closeButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val saveButton = mDialogView.findViewById<Button>(R.id.save_button)
            saveButton.setOnClickListener {
                val radioId = radioGroup.checkedRadioButtonId;
                val radioButton = mDialogView.findViewById<RadioButton>(radioId)
                //TODO: actual save
                s.active = radioButtonYES.isChecked
                s.description = description.text.toString()
                desc.text = s.description
                Toast.makeText(c, "Selected ${radioButton.text}", Toast.LENGTH_LONG).show()
                mAlertDialog.dismiss()
            }
        }
*/
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
        this.active = active
    }

    public fun toJSON():String{
        return """"title":"$title", "description":"$description", "active":$active, "src":"$src""""
    }
}

fun createSkills (c:Context) : List<Skill>{
    // return a list of skills that contains all the titles present in skill.xml file
    // src of a skill is the name with spaces replaced with underscore and lowercase
    val skills: MutableList<Skill> = mutableListOf()
    c.resources.getStringArray(R.array.skills_array).forEach{ s -> (skills as MutableList<Skill>).add(Skill(s, s.lowercase().replace(" ", "_"))) }
    // TODO: remove
    // just static adding for testing
    skills.find { s -> s.title == "Gardening" }.apply {
        this?.active = true
        this?.description ="I can mow the lawn, trim bushes, rake and pick up leaves in the garden. I can also take care of watering the flowers and plants and putting fertilizer"
    }
    skills.find { s -> s.title == "Home Repair" }.apply {
        this?.active = true
        this?.description ="I can fix your home appliance"
    }
    skills.find { s -> s.title == "Child Care" }.apply {
        this?.active = true
        this?.description ="Babysit your kids"
    }
    skills.find { s -> s.title == "Transportation" }.apply {
        this?.active = true
    }
    return skills
}

fun jsonToSkills(jsonArray: JSONArray): List<Skill>{
    val jobjects:MutableList<JSONObject> = mutableListOf()
    for(i in 0 until jsonArray.length()){
        val jo = jsonArray.getJSONObject(i)
        jobjects.add(jo)
    }
    return jobjects.map{ jo -> Skill(jo.getString("title"), jo.getString("src"), jo.getBoolean("active"), jo.getString("description")) }
}

fun skillsToJsonString(skills: List<Skill>):String {
    return skills.map{s -> s.toJSON()}.joinToString(separator = "},{", prefix ="[{", postfix = "}]")
}