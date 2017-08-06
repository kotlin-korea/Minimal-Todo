package com.example.avjindersinghsekhon.minimaltodo

/**
 * Created by kgj11 on 2017-07-30.
 */

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.Date
import java.util.UUID

class ToDoItem @Throws(JSONException::class) constructor(jsonObject: JSONObject) : Serializable {
    var toDoText: String = "Clean my room"
    var hasReminder: Boolean=false
    var todoColor: Int = 1677725
    var toDoDate: Date=Date()
    var identifier: UUID= UUID.randomUUID()
        private set

    init {
        toDoText = jsonObject.getString(TODOTEXT)
        hasReminder = jsonObject.getBoolean(TODOREMINDER)
        todoColor = jsonObject.getInt(TODOCOLOR)
        identifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER))
        toDoDate = if(jsonObject.has(TODODATE)) Date(jsonObject.getLong(TODODATE)) else this.toDoDate
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(TODOTEXT, toDoText)
        jsonObject.put(TODOREMINDER, hasReminder)
        jsonObject.put(TODODATE, toDoDate.time)
        jsonObject.put(TODOCOLOR, todoColor)
        jsonObject.put(TODOIDENTIFIER, identifier.toString())
        return jsonObject
    }

    companion object {
        private val TODOTEXT = "todotext"
        private val TODOREMINDER = "todoreminder"
        private val TODOCOLOR = "todocolor"
        private val TODODATE = "tododate"
        private val TODOIDENTIFIER = "todoidentifier"
    }

}

