package it.polito.mainactivity.model

class Skill(val title: String) {
    var description: String = ""
    var active: Boolean = false
    override fun toString() = """{ "title": "$title", "description": "$description", "active": $active }"""
}