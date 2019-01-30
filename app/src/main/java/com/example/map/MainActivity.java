package com.example.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private Spinner spinnerActivite;
    private String activite = null;
    private  MapboxMap mapboxMap;

    private String[] colors = {"#ff0000","#ff0080","#ff00bf","#ff00ff","#bf00ff","#8000ff","#4000ff","#0000ff","#0040ff","#00ffff","#00ff80","#00ff40","#80ff00","#ffff00","#ff8000","#ff0000","#ff0080","#ff00bf","#ff00ff","#bf00ff","#8000ff","#4000ff","#0000ff","#0040ff","#00ffff","#00ff80","#00ff40","#80ff00","#ffff00","#ff8000","#ff0000","#ff0080","#ff00bf","#ff00ff","#bf00ff","#8000ff","#4000ff","#0000ff","#0040ff","#00ffff","#00ff80","#00ff40","#80ff00","#ffff00","#ff8000"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoib2RvciIsImEiOiJjam82eGtoOHQwbnMxM3FwcWxmM2ZtaW4zIn0.WVlYHUYvukEai_eSlvk38A");

        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        spinnerActivite = findViewById(R.id.spinnerActivite);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        makeRequest();
                    }
                });
            }
        });
    }

    public void makeRequest (){
        if (Network.isNetworkAvailable(MainActivity.this)) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            String url = Constant.URL;
            if(activite != null){
                url += "&refine.activite="+ activite;
            }

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response ", response);
                            final CommercesRoubaix commerces = new Gson().fromJson( response, CommercesRoubaix.class);
                            mapboxMap.clear();
                            for( int i=0; i<commerces.records.size(); i++){
                                addMarkerProf(mapboxMap,commerces.records.get(i));
                            }
                            //Activités
                            if ( activite == null){
                                ArrayAdapter<Facet> adapter = new ActiviteAdapterSpinner(
                                        MainActivity.this, R.layout.item_selector_activite, commerces.facet_groups.get(2).facets, colors);
                                //adapter.setDropDownViewResource(R.layout.item_selector_activite);
//
                                spinnerActivite.setAdapter(adapter);
                                spinnerActivite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        activite = commerces.facet_groups.get(2).facets.get(position).name;

                                        makeRequest();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

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
                PorterDuffColorFilter(Color.parseColor(colors[commerces.fields.activite_regroupee_code]), PorterDuff.Mode.MULTIPLY));
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

