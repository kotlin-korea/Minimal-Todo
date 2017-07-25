package com.example.avjindersinghsekhon.minimaltodo

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import org.json.JSONException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by patternoid on 2017. 7. 21..
 */
class DeleteNotificationService : IntentService {

    private var storeRetrieveData   : StoreRetrieveData?    = null
    private var mToDoItems          : ArrayList<ToDoItem?>? = null
    private var mItem               : ToDoItem?             = null


    constructor() : super("DeleteNotificationService")



    override fun onHandleIntent(intent : Intent?) {

        val todoID: UUID    = intent?.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID

        storeRetrieveData   = StoreRetrieveData(this, MainActivity.FILENAME)
        mToDoItems          = loadData()
        mItem               = mToDoItems!!.filter { item -> item?.identifier!! == todoID }.singleOrNull()

        mItem?.let {
            mToDoItems!!.remove(mItem)
            dataChanged()
            saveData()
        }
    }



    private fun dataChanged(){

        baseContext.getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE).edit().run {
            putBoolean(MainActivity.CHANGE_OCCURED, true)
        }.apply()

    }



    private fun saveData(){
        try{
            storeRetrieveData?.saveToFile(mToDoItems)
        }
        catch( e : Exception ){
            e.printStackTrace()
        }
    }



    override fun onDestroy(){
        super.onDestroy()
        saveData()
    }



    private fun loadData() : ArrayList<ToDoItem?>{

        var loadData : ArrayList<ToDoItem?> = ArrayList<ToDoItem?>(0)

        try{
            loadData = storeRetrieveData!!.loadFromFile()

            if( loadData == null )
                throw KotlinNullPointerException()
        }

        catch( e : Exception){

            when(e){
                is KotlinNullPointerException,
                is JSONException,
                is IOException -> {
                    e.printStackTrace()
                }
                else -> throw e
            }
        }

        return loadData
    }
}