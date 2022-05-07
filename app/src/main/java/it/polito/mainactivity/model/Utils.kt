package it.polito.mainactivity.model

import org.json.JSONArray
import org.json.JSONObject

class Utils {

    companion object {

        fun JSONArrayToList(ja: JSONArray): MutableList<JSONObject>{
            val list: MutableList<JSONObject> = mutableListOf()
            for(i in 0 until ja.length()){
                list.add(ja.getJSONObject(i))
            }
            return list
        }

    }

}