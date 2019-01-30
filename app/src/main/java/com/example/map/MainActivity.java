package com.example.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.map.model.CommercesRoubaix;
import com.example.map.model.Facet;
import com.example.map.model.Records;
import com.example.map.utils.Constant;
import com.example.map.utils.FastDialog;
import com.example.map.utils.Network;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private int[] colors = {} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoib2RvciIsImEiOiJjam82eGtoOHQwbnMxM3FwcWxmM2ZtaW4zIn0.WVlYHUYvukEai_eSlvk38A");

        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        if (Network.isNetworkAvailable(MainActivity.this)) {
                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                            String url = Constant.URL;

                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e("response ", response);
                                            CommercesRoubaix commerces = new Gson().fromJson( response, CommercesRoubaix.class);
                                            for( int i=0; i<commerces.records.size(); i++){
                                                addMarkerProf(mapboxMap,commerces.records.get(i));
                                            }
                                            ArrayAdapter<Facet> adapter =
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("response ", "error");
                                }
                            });
                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);
                        } else {
                            FastDialog.showDialog(MainActivity.this, FastDialog.SIMPLE_DIALOG, "no connection");
                        }
                    }
                });
            }
        });
    }



    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    //version del profe
    private void addMarkerProf(MapboxMap mapboxMap, Records commerces){

        Drawable mDrawable = MainActivity.this.getResources().getDrawable(R.drawable.mapbox_marker_icon_default);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor("#FF1B80D8"), PorterDuff.Mode.MULTIPLY));
        Icon icon = IconFactory.getInstance(MainActivity.this).fromBitmap(convertToBitmap(mDrawable, mDrawable.getMinimumWidth(),mDrawable.getMinimumHeight()));

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.setPosition(new LatLng( commerces.fields.latitude, commerces.fields.longitude));
        markerOptions.setTitle(commerces.fields.enseigne);
        String textSnippet = "ouverture : %s \nactivite : %s \nadresse : %s \ndetail de l'activité : %s";
        markerOptions.setSnippet(String.format(textSnippet, commerces.fields.etat, commerces.fields.activite, commerces.fields.adresse, commerces.fields.detail_d_activite));
        markerOptions.setIcon(icon);
        mapboxMap.addMarker(markerOptions);

        mapboxMap.setOnInfoWindowClickListener( new  MapboxMap.OnInfoWindowClickListener(){
            @Override
            public boolean onInfoWindowClick(@NonNull Marker marker) {
                Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                return false;
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}

