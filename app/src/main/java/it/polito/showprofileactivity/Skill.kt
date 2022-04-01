package it.polito.showprofileactivity

class Skill (var title:String){

    public var description: String = ""
    public var active: Boolean = false

    constructor(title:String, desc:String): this(title) {
        this.description = desc
    }
}