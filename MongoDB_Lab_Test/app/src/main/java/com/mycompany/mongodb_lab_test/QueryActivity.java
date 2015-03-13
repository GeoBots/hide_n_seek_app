package com.mycompany.mongodb_lab_test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;


public class QueryActivity extends Activity {

    protected TextView queryLat;
    protected TextView queryLong;
    protected TextView queryTime;

    public String passlat;
    public String passlong;
    public String passtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        queryLat = (TextView) findViewById(R.id.latitude_query);
        queryLong = (TextView) findViewById(R.id.longitude_query);
        queryTime = (TextView) findViewById(R.id.time_query);

        QueryLocation querylocation = new QueryLocation();
        querylocation.execute();
    }

    private class QueryLocation extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            try {
                MongoClientURI uri = new MongoClientURI("mongodb://sbuck10:ShawnBuck8!@ds043981.mongolab.com:43981/locations");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());

                DBCollection MyLatLng = db.getCollection("MyLatLng");

                DBObject cursor = MyLatLng.findOne();

                passlat = String.valueOf(cursor.get("Latitude"));
                passlong = String.valueOf(cursor.get("Longitude"));
                passtime = String.valueOf(cursor.get("Time"));
                client.close();

                return "who cares";

            } catch (UnknownHostException e) {
                return "Unknown Host Exception";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            queryLat.setText(passlat);
            queryLong.setText(passlong);
            queryTime.setText(passtime);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_query, menu);
        return true;
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
