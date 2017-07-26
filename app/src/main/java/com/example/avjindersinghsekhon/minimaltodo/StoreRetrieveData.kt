package com.example.avjindersinghsekhon.minimaltodo

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*


/**
 * Created by patternoid on 2017. 7. 26..
 */
class StoreRetrieveData ( var mContext : Context , var mFileName : String ) {

    companion object {

        @JvmStatic @Throws(JSONException::class)
        fun toJSONArray( items : ArrayList<ToDoItem> ) : JSONArray {

            var jsonArray = JSONArray()

            for( item : ToDoItem in items ){
                var jsonObject = item.toJSON()
                jsonArray.put(jsonObject)
            }

            return jsonArray
        }
    }


    @Throws(JSONException::class,IOException::class)
    fun saveToFile( items : ArrayList<ToDoItem> ) {

        val fileOutputStream    : FileOutputStream      =   mContext.openFileOutput(mFileName, Context.MODE_PRIVATE)
        val outputStreamWriter  : OutputStreamWriter    =   OutputStreamWriter(fileOutputStream)
        val jsonArrayConvertString : String             = toJSONArray(items).toString()

        outputStreamWriter.write( jsonArrayConvertString )
        outputStreamWriter.close()
        fileOutputStream.close()
    }



    @Throws(JSONException::class, IOException::class )
    fun loadFromFile() : ArrayList<ToDoItem> {

        var items : ArrayList<ToDoItem>         = arrayListOf<ToDoItem>()
        var bufferedReader  : BufferedReader?   = null
        var fileInputStream : FileInputStream?  = null

        try{
            var builder : StringBuilder = StringBuilder()
            var line    : String?       = null

            fileInputStream = mContext.openFileInput(mFileName)
            bufferedReader  = BufferedReader(InputStreamReader(fileInputStream))

            do {
                line = bufferedReader.readLine()
                line?.let {  builder.append(line) }

            }while( line != null )

            val jsonArray = JSONTokener(builder.toString()).nextValue() as JSONArray

            for( index in 0..jsonArray.length() )
            {
                var jsonObject  : JSONObject    = jsonArray.getJSONObject(index)
                val item        : ToDoItem      = ToDoItem(jsonObject)

                items.add(item)
            }

        } catch ( fnfe : FileNotFoundException ){
            //do nothing about it
            //file won't exist first time app is run
        } finally {
            bufferedReader?.let { bufferedReader!!.close() }
            fileInputStream?.let { fileInputStream!!.close() }
        }

        return items
    }
}