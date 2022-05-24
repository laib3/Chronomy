package it.polito.mainactivity.model

data class Skill(val category: String) {
    var description: String = ""
    var active: Boolean = false

    constructor(category: String, description: String, active: Boolean) : this(category) {
        this.description = description
        this.active = active
    }

    override fun toString() =
        """{ "category": "$category", "description": "$description", "active": $active }"""
}