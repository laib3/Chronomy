package it.polito.mainactivity.model

data class Skill(val category: String) {
    var description: String = ""
    var active: Boolean = false
    override fun toString() = """{ "category": "$category", "description": "$description", "active": $active }"""
}