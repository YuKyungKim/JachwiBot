package com.soma.albert.jachwibot;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Date;

public class HouseworkActivity extends Activity {

    private int houseworkType = 0;
    DatePicker datePicker;
    HouseworkComponent houseworkComponent = new HouseworkComponent();

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housework_plus);
        setTitle("집안일 추가");

        dbManager = new DBManager(getApplicationContext(), "jachwibot.db", null, 1);

        long today = new Date().getTime();

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        houseworkComponent.setLastDay(datePicker.getYear()+"."+(datePicker.getMonth()+1)+"."+datePicker.getDayOfMonth());
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String lastDay = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);
                        houseworkComponent.setLastDay(lastDay);
                    }
                });
        datePicker.setMaxDate(today);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_housework, menu);
        return true;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.trashSelect:
                houseworkType = 1;
                houseworkComponent.setHouseworkType(1);
                break;
            case R.id.laundrySelect:
                houseworkType = 2;
                houseworkComponent.setHouseworkType(2);
                break;
            case R.id.datePicker:
                break;
            case R.id.houseworkAdd:
                if(houseworkComponent.getHouseworkType() != 0 && houseworkComponent.getLastDay() != null) {
                    insertHousework(houseworkComponent);
                    finish();
                } else {
                    Toast.makeText(HouseworkActivity.this, "항목과 날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.houseworkCancel:
                houseworkComponent = new HouseworkComponent();
                finish();
                break;

        }
    }

    private void insertHousework(HouseworkComponent newHouseworkComp) {
        dbManager.insert("INSERT INTO HOUSEWORK_LIST(housework_type, last_day) " +
                "VALUES("+newHouseworkComp.getHouseworkType()+", " +
                "'"+newHouseworkComp.getLastDay()+"'); '");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
