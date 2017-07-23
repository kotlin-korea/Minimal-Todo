package com.example.avjindersinghsekhon.minimaltodo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.avjindersinghsekhon.minimaltodo.contract.Contract;
import com.example.avjindersinghsekhon.minimaltodo.data.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.data.source.todo.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.view.main.MainActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ReminderActivity extends AppCompatActivity{
    private TextView mtoDoTextTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionsArray;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;
    public static final String EXIT = "com.avjindersekhon.exit";
    private TextView mSnoozeTextView;
    String theme;

    // todo 제거 필요
    // kotlin 변환으로 인해 해당 부분 로직 변경
    public ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<ToDoItem> items = null;

        try {
            items  = storeRetrieveData.loadFromFile();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if(items == null){
            items = new ArrayList<>();
        }
        return items;

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        theme = getSharedPreferences(Contract.THEME_PREFERENCES, MODE_PRIVATE).getString(Contract.THEME_SAVED, Contract.LIGHT_THEME);
        if(theme.equals(Contract.LIGHT_THEME)){
            setTheme(R.style.CustomStyle_LightTheme);
        }
        else{
            setTheme(R.style.CustomStyle_DarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_layout);
        storeRetrieveData = new StoreRetrieveData(this, Contract.FILENAME);
        mToDoItems = getLocallyStoredData(storeRetrieveData);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));



        Intent i = getIntent();
        UUID id = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);
        mItem = null;
        for(ToDoItem toDoItem : mToDoItems){
            if (toDoItem.getIdentifier().equals(id)){
                mItem = toDoItem;
                break;
            }
        }

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);

        mRemoveToDoButton = (Button)findViewById(R.id.toDoReminderRemoveButton);
        mtoDoTextTextView = (TextView)findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = (TextView)findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = (MaterialSpinner)findViewById(R.id.todoReminderSnoozeSpinner);

//        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView.setText(mItem.getToDoText());

        if(theme.equals(Contract.LIGHT_THEME)){
            mSnoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        }
        else{
            mSnoozeTextView.setTextColor(Color.WHITE);
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp,0,0,0
            );
        }

        mRemoveToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToDoItems.remove(mItem);
                changeOccurred();
                saveData();
                closeApp();
//                finish();
            }
        });


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptionsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text_view, snoozeOptionsArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mSnoozeSpinner.setAdapter(adapter);
//        mSnoozeSpinner.setSelection(0);

    }

    private void closeApp(){
        Intent i = new Intent(ReminderActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.putExtra(EXIT, true);
        SharedPreferences sharedPreferences = getSharedPreferences(Contract.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(EXIT, true);
        editor.apply();
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }
    private void changeOccurred(){
        SharedPreferences sharedPreferences = getSharedPreferences(Contract.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Contract.CHANGE_OCCURED, true);
//        editor.commit();
        editor.apply();
    }

    private Date addTimeToDate(int mins){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }
    private int valueFromSpinner(){
        switch (mSnoozeSpinner.getSelectedItemPosition()){
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toDoReminderDoneMenuItem:
                Date date = addTimeToDate(valueFromSpinner());
                mItem.setToDoDate(date);
                mItem.setHasReminder(true);
                Log.d("OskarSchindler", "Date Changed to: " + date);
                changeOccurred();
                saveData();
                closeApp();
                //foo
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try{
//            storeRetrieveData.saveToFile(mToDoItems);
//        }
//        catch (JSONException | IOException e){
//            e.printStackTrace();
//        }
//    }

    private void saveData(){
        try{
            storeRetrieveData.saveToFile(mToDoItems);
        }
        catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try{
//            storeRetrieveData.saveToFile(mToDoItems);
//        }
//        catch (JSONException | IOException e){
//            e.printStackTrace();
//        }
    }
}
