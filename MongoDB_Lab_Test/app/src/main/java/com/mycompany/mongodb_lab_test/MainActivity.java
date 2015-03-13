package com.mycompany.mongodb_lab_test;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    protected GoogleApiClient mGoogleApiClient;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected Location mLastLocation;
    private ShareActionProvider mShareActionProvider;
    protected String mShare;

    protected static final String TAG = "Finding Location!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeText = (TextView) findViewById(R.id.Latitude_text);
        mLongitudeText = (TextView) findViewById(R.id.Longitude_text);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mShare = "I'm at " + String.valueOf(mLastLocation.getLatitude()) + " degrees Latitude and " + String.valueOf(mLastLocation.getLongitude()) + " degrees Longitude.";
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        else{
            mShare = "No location detected!";
            Toast.makeText(this, "No location detected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
        return true;
    }

    public void onPostClick(View view){
        PostLocation postlocation = new PostLocation();
        postlocation.execute();
        Toast.makeText(this, "Coordinates Submitted", Toast.LENGTH_SHORT).show();
    }

    private class PostLocation extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            try {
                //Let's create our connection
                MongoClientURI uri = new MongoClientURI("mongodb://sbuck10:ShawnBuck8!@ds043981.mongolab.com:43981/locations");
                MongoClient client = new MongoClient(uri);
                DB db = client.getDB(uri.getDatabase());

                DBCollection MyLatLng = db.getCollection("MyLatLng");

                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                String now = time.format(new Date());

                if (mLastLocation != null) {
                    BasicDBObject LastLocation = new BasicDBObject();
                    LastLocation.put("Latitude", String.valueOf(mLastLocation.getLatitude()));
                    LastLocation.put("Longitude", String.valueOf(mLastLocation.getLongitude()));
                    LastLocation.put("Time", String.valueOf(now));

                    MyLatLng.insert(LastLocation);
                    client.close();
                    //publishProgress();

                    return "Coordinates submitted!";

                } else {
                    client.close();
                    return "No location detected, no location submitted!";
                }
            } catch (UnknownHostException e) {
                return "Unknown Host Exception, bro!!";
            }

        }

    }


    private Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mShare);
        return intent;
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

    public void onQueryClick(View view) {
        Intent intent = new Intent(this, QueryActivity.class);
        startActivity(intent);
    }
}
