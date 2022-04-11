package it.polito.showprofileactivity

import android.view.LayoutInflater
import android.widget.*
import androidx.cardview.widget.CardView
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context as Context


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

class Skill (var title:String, var src:String){

    var description: String = ""
    var active: Boolean = false

    constructor(title:String, src:String, desc:String): this(title, src) {
        this.description = desc
    }
    constructor(title:String, src:String, active:Boolean, desc:String): this(title, src) {
        this.description = desc
        this.src = src
        this.active = active
    }

    fun toJSON():String{
        return """"title":"$title", "description":"$description", "active":$active, "src":"$src""""
    }
}

fun createSkills (c:Context) : List<Skill>{
    // return a list of skills that contains all the titles present in skill.xml file
    // src of a skill is the name with spaces replaced with underscore and lowercase
    val skills: MutableList<Skill> = mutableListOf()
    c.resources.getStringArray(R.array.skills_array).forEach{ s -> (skills).add(Skill(s, s.lowercase().replace(" ", "_"))) }
    // TODO: remove
    // just static adding for testing
    // we populate skills just to display something, but we will remove this part in future
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
        // without description, since skills can also have no description
    }
    return skills
}

fun jsonToSkills(jsonArray: JSONArray): List<Skill>{
    val jsonObjects:MutableList<JSONObject> = mutableListOf()
    for(i in 0 until jsonArray.length()){
        val jo = jsonArray.getJSONObject(i)
        jsonObjects.add(jo)
    }
    return jsonObjects.map{ jo -> Skill(jo.getString("title"), jo.getString("src"), jo.getBoolean("active"), jo.getString("description")) }
}

fun skillsToJsonString(skills: List<Skill>):String {
    return if (skills.isEmpty())
        "[]"
    else
        skills.joinToString(separator = "},{", prefix = "[{", postfix = "}]") { s -> s.toJSON() }
}