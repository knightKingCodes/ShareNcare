package com.example.sharencare.threads;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.sharencare.Interfaces.TaskDelegate;
import com.example.sharencare.R;
import com.example.sharencare.ui.DriverActivity;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class DirectionsThreads extends  AsyncTask<Void,Void,DirectionsResult>{
    private static final String TAG = "DirectionsThreads";
    String tripFrom;
    String tripTo;
    Context mContext;
    GeoApiContext mGeoApiContext;
    DirectionsResult mResult;
    boolean rootfound=false;
    WeakReference<TaskDelegate> mTaskDelegate;

    public DirectionsThreads(String tripFrom, String tripTo, Context mContext,TaskDelegate taskDelegate) {
        this.tripFrom = tripFrom;
        this.tripTo = tripTo;
        this.mContext = mContext;
        mTaskDelegate= new WeakReference<>(taskDelegate);
        mGeoApiContext=new GeoApiContext.Builder().apiKey("AIzaSyC5GBW_utsccSxqqvyOawamVmacuNohEdY").build();
    }
    @Override
    protected DirectionsResult doInBackground(Void... voids) {
        return  calculateDirection();
    }

    public DirectionsResult calculateDirection() {
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.region("IN");
        directions.origin(tripFrom);
        directions.mode(TravelMode.DRIVING);
        directions.alternatives(true);
        directions.destination(tripTo);
        Log.d(TAG, "calculateDirections: calculating directions.");
           directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
               @Override
               public void onResult(DirectionsResult result) {
                   mResult = result;
                   rootfound=true;
               }
               @Override
               public void onFailure(Throwable e) {
                   rootfound=true;
                   mResult = null;
                   Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
               }
           });
           while(rootfound!=true){
               Log.d(TAG, "calculateDirection: Still calculating route to destination");
           };

     return  mResult;
    }
    @Override
    protected void onPostExecute(DirectionsResult result) {
        try {
            mTaskDelegate.get().onDirectionsRetrived(result);
        } catch (Exception e) {
            Log.d(TAG, "onPostExecute:" + e.getMessage());
        }
   }


}
