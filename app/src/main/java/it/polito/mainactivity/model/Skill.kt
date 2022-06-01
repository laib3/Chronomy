package it.polito.mainactivity.model

data class Skill(val category: String) {
    var description: String = ""
    var active: Boolean = false

    constructor(category: String, description: String, active: Boolean) : this(category) {
        this.description = description
        this.active = active
    }

    /** create Skill object from HashMap **/
    constructor(skillMap: Map<String, String>): this(skillMap["category"] ?: "category"){
        this.description = skillMap["description"] ?: "description"
        this.active = (skillMap["active"] as String) == "true"
    }

    override fun toString() =
        """{ "category": "$category", "description": "$description", "active": $active }"""

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "category" to category,
            "description" to description,
            "active" to active
        )
    }
}