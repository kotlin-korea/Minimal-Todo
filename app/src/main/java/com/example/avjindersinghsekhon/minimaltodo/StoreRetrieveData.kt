package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException 
import java.util.ArrayList

class StoreRetrieveData(private val mContext: Context, private val mFileName: String) {

    @Throws(JSONException::class, IOException::class)
    fun saveToFile(items: ArrayList<ToDoItem>) {
        val fileOutputStream: FileOutputStream
        fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE)
        fileOutputStream.use {
            it.writer().use {
                it.write(toJSONArray(items).toString())
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun loadFromFile(): ArrayList<ToDoItem> {
        val items = ArrayList<ToDoItem>()
        val builder = StringBuilder()
        mContext.openFileInput(mFileName)?.use {
            it.bufferedReader().use {
                it.readLines().forEach {
                    builder.append(it)
                }
            }
        }

        val jsonArray = JSONTokener(builder.toString()).nextValue() as JSONArray
        (0..jsonArray.length() - 1).mapTo(items) {ToDoItem(jsonArray.getJSONObject(it))}

        return items
    }

    companion object {
        @Throws(JSONException::class)
        fun toJSONArray(items: ArrayList<ToDoItem>): JSONArray {
            val jsonArray = JSONArray()
            items.forEach {
                jsonArray.put(it.toJSON())
            }
            return jsonArray
        }
    }
}