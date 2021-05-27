package com.example.attendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    ListView lstPlaces;
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(12.918118, 77.650339);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    Button submitButton;
    SeekBar simpleSeekBar;
    LatLng defaultlatlong=new LatLng(12.918118, 77.650339);
    int radvalue=50;
    Button mapDonebt;
//    private static final double DEFAULT_RADIUS_METERS = 1000000;
//    private static final double RADIUS_OF_EARTH_METERS = 6371009;
//
//    private static final int MAX_WIDTH_PX = 50;
//    private static final int MAX_HUE_DEGREES = 360;
//    private static final int MAX_ALPHA = 255;
//
//    private static final int PATTERN_DASH_LENGTH_PX = 100;
//    private static final int PATTERN_GAP_LENGTH_PX = 200;
//    private static final Dot DOT = new Dot();
//    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
//    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
//    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
//    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
//    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);
//
//    private List<DraggableCircle> circles = new ArrayList<>(1);
//
//    private int fillColorArgb;
//    private int strokeColorArgb;
//
//    private SeekBar fillHueBar;
//    private SeekBar fillAlphaBar;
//    private SeekBar strokeWidthBar;
//    private SeekBar strokeHueBar;
//    private SeekBar strokeAlphaBar;
//    private Spinner strokePatternSpinner;
//    private CheckBox clickabilityCheckbox;
//
//    // These are the options for stroke patterns. We use their
//    // string resource IDs as identifiers.
//
//    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
//            R.string.pattern_solid, // Default
//            R.string.pattern_dashed,
//            R.string.pattern_dotted,
//            R.string.pattern_mixed,
//    };
//
//    private class DraggableCircle {
//        private final Marker centerMarker;
//        private final Marker radiusMarker;
//        private final Circle circle;
//        private double radiusMeters;
//
//        public DraggableCircle(LatLng center, double radiusMeters) {
//            this.radiusMeters = radiusMeters;
//            centerMarker = mMap.addMarker(new MarkerOptions()
//                    .position(center)
//                    .draggable(true));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(toRadiusLatLng(center, radiusMeters))
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
//            circle = mMap.addCircle(new CircleOptions()
//                    .center(center)
//                    .radius(radiusMeters)
//                    .strokeWidth(strokeWidthBar.getProgress())
//                    .strokeColor(strokeColorArgb)
//                    .fillColor(fillColorArgb)
//                    .clickable(clickabilityCheckbox.isChecked()));
//        }
//
//        public boolean onMarkerMoved(Marker marker) {
//            if (marker.equals(centerMarker)) {
//                circle.setCenter(marker.getPosition());
//                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radiusMeters));
//                return true;
//            }
//            if (marker.equals(radiusMarker)) {
//                radiusMeters =
//                        toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
//                circle.setRadius(radiusMeters);
//                return true;
//            }
//            return false;
//        }
//
//        public void onStyleChange() {
//            circle.setStrokeWidth(strokeWidthBar.getProgress());
//            circle.setStrokeColor(strokeColorArgb);
//            circle.setFillColor(fillColorArgb);
//        }
//
//        public void setStrokePattern(List<PatternItem> pattern) {
//            circle.setStrokePattern(pattern);
//        }
//
//        public void setClickable(boolean clickable) {
//            circle.setClickable(clickable);
//        }
//    }
//
//    /** Generate LatLng of radius marker */
//    private static LatLng toRadiusLatLng(LatLng center, double radiusMeters) {
//        double radiusAngle = Math.toDegrees(radiusMeters / RADIUS_OF_EARTH_METERS) /
//                Math.cos(Math.toRadians(center.latitude));
//        return new LatLng(center.latitude, center.longitude + radiusAngle);
//    }
//
//    private static double toRadiusMeters(LatLng center, LatLng radius) {
//        float[] result = new float[1];
//        Location.distanceBetween(center.latitude, center.longitude,
//                radius.latitude, radius.longitude, result);
//        return result[0];
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapDonebt=findViewById(R.id.mapDone);

        mapDonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                String url = "http://192.140.229.103:5000/addNewGeoLocation/";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", "1");
                    jsonObject.put("latitude",defaultlatlong.latitude);
                    jsonObject.put("longitude",defaultlatlong.longitude);
                    jsonObject.put("radius",radvalue);
                    jsonObject.put("geolocation_name","Test");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //      Request a string response from the provided URL.
                System.out.println("Response"+ jsonObject);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String res = response.getString("comment");
                                    //notificationHelper.sendHighPriorityNotification("Success","",detectface.class);
                                    Toast.makeText(MapsActivity.this, res.toString(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonRequest);
            }
        });

        simpleSeekBar=(SeekBar)findViewById(R.id.simpleSeekBar);
        // perform seek bar change listener event used for getting the progress value

        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                mMap.clear();
                //radvalue=progressChangedValue;
                //drawCircle(defaultlatlong,radvalue);
                mMap.addMarker(new MarkerOptions().position(defaultlatlong).draggable(true));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MapsActivity.this, "Radius :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                radvalue=progressChangedValue;
                drawCircle(defaultlatlong,radvalue);
            }
        });

    }

    private void valchange(int progressChangedValue) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_geolocate:
                pickCurrentPlace();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    private void getLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bengaluru = new LatLng(12.918118, 77.650339);
        mMap.addMarker(new MarkerOptions().position(bengaluru).draggable(true));
        drawCircle(bengaluru,50);


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(bengaluru));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                //defaultlatlong=new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
                //mMap.clear();

                //drawCircle(new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude),radvalue);
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                mMap.clear();
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                //drawCircle(new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude),50);
                defaultlatlong=new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
                mMap.addMarker(new MarkerOptions().position(defaultlatlong).draggable(true));
                drawCircle(new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude),radvalue);



                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                //mMap.clear();

                Log.i("System out", "onMarkerDrag...");
            }
        });

//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(bengaluru);
//        circleOptions.radius(20);
//        circleOptions.fillColor(Color.TRANSPARENT);
//        circleOptions.strokeWidth(6);
//        mMap.addCircle(circleOptions);


//        DraggableCircle circle = new DraggableCircle(mDefaultLocation, 10);
//        circles.add(circle);

//        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
//            @Override
//            public void onCircleClick(Circle circle) {
//                // Flip the red, green and blue components of the circle's stroke color.
//                circle.setStrokeColor(circle.getStrokeColor() ^ 0x00ffffff);
//            }
//        });

//        List<PatternItem> pattern = getSelectedPattern(strokePatternSpinner.getSelectedItemPosition());
//        for (DraggableCircle draggableCircle : circles) {
//            draggableCircle.setStrokePattern(pattern);


//Don't forget to Set draggable(true) to marker, if this not set marker does not drag.

        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Prompt the user for permission.
        getLocationPermission();

    }

    private void drawCircle(LatLng point,int radvalue){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(radvalue);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }


    private void getCurrentPlaceLikelihoods() {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(this,
                new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Place currPlace = placeLikelihood.getPlace();
                                mLikelyPlaceNames[i] = currPlace.getName();
                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
                                        null : TextUtils.join(" ", currPlace.getAttributions());
                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();

                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
                                        "" : mLikelyPlaceLatLngs[i].toString();

                                Log.i(TAG, String.format("Place " + currPlace.getName()
                                        + " has likelihood: " + placeLikelihood.getLikelihood()
                                        + " at " + currLatLng));

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }


                            // COMMENTED OUT UNTIL WE DEFINE THE METHOD
                            // Populate the ListView
                            fillPlacesList();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }
                });
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = location;
                            Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                            Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }

                        getCurrentPlaceLikelihoods();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void pickCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();
        } else {
            Log.i(TAG, "The user did not grant location permission.");

            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            getLocationPermission();
        }
    }

    private AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // position will give us the index of which place was selected in the array
            LatLng markerLatLng = mLikelyPlaceLatLngs[position];
            String markerSnippet = mLikelyPlaceAddresses[position];
            if (mLikelyPlaceAttributions[position] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[position];
            }
            mMap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        }
    };

    private void fillPlacesList() {
        // Set up an ArrayAdapter to convert likely places into TextViews to populate the ListView
        ArrayAdapter<String> placesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mLikelyPlaceNames);
        lstPlaces.setAdapter(placesAdapter);
        lstPlaces.setOnItemClickListener(listClickedHandler);
    }

}