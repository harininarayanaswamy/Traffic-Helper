package com.example.arunkumar.traffichelper;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.arunkumar.traffichelper.R.id.map;
import static com.example.arunkumar.traffichelper.R.string.google_maps_key;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String getroutes= "http://192.168.1.6/getRoute.php";
    ArrayList<Location> route=new ArrayList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        Location point= new Location("test1");
//        Location point1= new Location("test2");
//        Location point2= new Location("test3");
//        Double[] lat;
//        Double[] longi;
//        lat= new Double[]{80.1923308, 80.1922163, 80.1922163, 80.1921943, 80.1921943, 80.1919946, 80.1919946, 80.191831, 80.191831, 80.1902221, 80.1902221, 80.1896171, 80.1880445, 80.1880445, 80.1863781, 80.1863781, 80.1860824};
//        longi=new Double[]{13.0873244,13.0873394,13.0873394,13.0873423,13.0873423,13.0873685,13.0873685,13.0873988,13.0873988,13.0876059,13.0876059,13.0876811,13.0877556,13.0877556,13.0878346,13.0878346,13.0878487};
//
//        point.setLatitude(13.0930304);
//        point.setLongitude(80.2558812);
//        point1.setLatitude(13.0929184);
//        point1.setLongitude(80.2554783);
//        point2.setLatitude(13.0929186);
//        point2.setLongitude(80.2554222);
//        int i;
//        for(i=0;i<17;i++){
//            Location point4= new Location("test3");
//            point4.setLatitude(longi[i]);
//            point4.setLongitude(lat[i]);
//            route.add(point4);
//
//        }

       // route.add(point);
       // route.add(point1);
       // route.add(point2);




        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(13.0930304, 80.2558812 );
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        drawPrimaryLinePath(route);
    }

    private void drawPrimaryLinePath( ArrayList<Location> listLocsToDraw )
    {
        if ( mMap == null )
        {
            return;
        }

        if ( listLocsToDraw.size() < 2 )
        {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );

        for ( Location locRecorded : listLocsToDraw )
        {
            options.add( new LatLng( locRecorded.getLatitude(),
                    locRecorded.getLongitude() ) );
        }

        mMap.addPolyline( options );

    }

    private class getroutes extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;
        //EditText temp;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... args){


            JSONObject jsonobject;
            final JSONParser jParser2 = new JSONParser();
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("slat", "" + latitude));
            params2.add(new BasicNameValuePair("slong", "" + longitude ));
            params2.add(new BasicNameValuePair("dlat", "" + latitude));
            params2.add(new BasicNameValuePair("dlong", "" + longitude ));

            jsonobject = jParser2.makeHttpRequest(getroutes, "GET", params2);

            try{

                String message = jsonobject.getString("success").toString();
                if( !(new String(message).equals("0"))){

                    JSONArray jsonarray = jsonobject.getJSONArray("result");
                    int len = jsonarray.length();

                    for(int i =0;i<len;i++) {
                        JSONObject locaiton = jsonarray.getJSONObject(i);

                        String lati = locaiton.getString("latitude");
                        String longi = locaiton.getString("longitude");
                        double val1 = Double.parseDouble(lati);
                        double val2 = Double.parseDouble(longi);
                        Location points= new Location("points");
                        points.setLatitude(val1);
                        points.setLongitude(val2);
                        route.add(points);


                    }

                    return true;
                }

            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == false){


                Toast.makeText(MapsActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MapsActivity.this,"success",Toast.LENGTH_SHORT).show();
                drawPrimaryLinePath(route);

            }
        }
    }


}

