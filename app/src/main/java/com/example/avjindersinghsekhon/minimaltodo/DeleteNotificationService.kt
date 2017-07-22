package com.example.avjindersinghsekhon.minimaltodo

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by patternoid on 2017. 7. 21..
 */
class DeleteNotificationService : IntentService {

    private var storeRetrieveData : StoreRetrieveData? = null
    private var mToDoItems : ArrayList<ToDoItem?>? = null
    private var mItem : ToDoItem? = null

    constructor() : super("DeleteNotificationService")


    override fun onHandleIntent(intent : Intent?) {
        storeRetrieveData = StoreRetrieveData(this, MainActivity.FILENAME)
        val todoID: UUID = intent?.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID

        mToDoItems = loadData()

        if (mToDoItems != null) {
            for (item: ToDoItem? in mToDoItems.orEmpty()) {
                if( item?.identifier!!.equals(todoID)){
                    mItem = item
                    break
                }
            }

            if( mItem != null ){
                mToDoItems?.remove(mItem)
                dataChanged()
                saveData()
            }
        }

    }

    private fun dataChanged(){
        val sharedPreferences : SharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(MainActivity.CHANGE_OCCURED, true)
        editor.apply()
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





    private fun loadData() : ArrayList<ToDoItem?>?{
        try{
            return storeRetrieveData?.loadFromFile()
        }
        catch( e : Exception){
            e.printStackTrace()
        }

        return null
    }
}




/*
public class DeleteNotificationService extends IntentService {

    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;

    public DeleteNotificationService(){
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        storeRetrieveData = new StoreRetrieveData(this, MainActivity.FILENAME);
        UUID todoID = (UUID)intent.getSerializableExtra(TodoNotificationService.TODOUUID);

        mToDoItems = loadData();
        if(mToDoItems!=null){
            for(ToDoItem item : mToDoItems){
                if(item.getIdentifier().equals(todoID)){
                    mItem = item;
                    break;
                }
            }

            if(mItem!=null){
                mToDoItems.remove(mItem);
                dataChanged();
                saveData();
            }

        }

    }

    private void dataChanged(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData(){
        try{
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private ArrayList<ToDoItem> loadData(){
        try{
            return storeRetrieveData.loadFromFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }
}

 */