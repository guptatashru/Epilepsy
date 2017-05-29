package com.hp.epilepsy.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.hp.epilepsy.LocationServices.GPSTracker;
import com.hp.epilepsy.LocationServices.LocationAddress;
import com.hp.epilepsy.R;
import com.hp.epilepsy.utils.ServiceUtil;

import java.io.Serializable;
import java.util.List;

public class MapsActivity extends FragmentActivity implements PlaceSelectionListener, Serializable, OnMapReadyCallback,LocationListener{

    public final static String SER_LOCATION_KEY = "com.hp.epilepsy.widget.ser";
    public final static String LOCATION_LATITUDE = "LOCATION_LATITUDE";
    public final static String LOCATION_LONITUDE = "LOCATION_LONITUDE";
    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 100;


    String address = null;
    double latitude;
    double longtiude;
    double lat;
    double lang;
    private LocListener locListener;
    Bundle passedData;
    com.hp.epilepsy.widget.model.Location loc = new com.hp.epilepsy.widget.model.Location();
    private GoogleMap mMap;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_maps);
           /* mMap = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);*/
            //((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            ((SupportMapFragment)this.getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMapAsync(this);


            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.e("", "Place: " + place.getName());


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(place.getLatLng())            // Sets the center of the map to location user
                            .zoom(10)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f);
                    mMap.animateCamera(cameraUpdate);


                }
                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.e("", "An error occurred: " + status);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //this method to initialize the appointment location in case update exist appointment and to get user location in case the user will create new location for new appointment
    public void InitializeLocation(Bundle PassedData) {
        try {
            if (PassedData != null) {
                if (PassedData.containsKey(LOCATION_LATITUDE) && PassedData.containsKey(LOCATION_LONITUDE)) {
                    lat = (double) PassedData.get(LOCATION_LATITUDE);
                    lang = (double) PassedData.get(LOCATION_LONITUDE);
                    if (lat != 0.0 && lang != 0.0) {
                        getUserLocation(lat, lang, true);
                    } else {
                        getUserLocation(lat, lang, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap!=null) {
            //mapFragment.getMapAsync(this);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            locListener = new LocListener();
            passedData = this.getIntent().getExtras();
            InitializeLocation(passedData);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    latitude = latLng.latitude;
                    loc.setLatitude(latLng.latitude);
                    longtiude = latLng.longitude;
                    loc.setLongitude(latLng.longitude);
                    GetAddressfromLocation(latLng.latitude, latLng.longitude);
                }
            });
        }
    }

    public void getUserLocation(double lat, double lang, boolean update) {
        try {
            LatLng userLocation = null;
            GPSTracker gps = new GPSTracker(getApplicationContext());
            if (gps.canGetLocation()) {
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = service.getBestProvider(criteria, true);
                //Location location = service.getLastKnownLocation(provider);
                Location location = getLastKnownLocation();
                if (update) {
                    userLocation = new LatLng(lat, lang);

                } else {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(userLocation)            // Sets the center of the map to location user
                        .zoom(10)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 16.0f);
                mMap.animateCamera(cameraUpdate);
            } else {
                showSettingsAlert();
            }
        } catch (Exception ex) {
        }
    }


    private Location getLastKnownLocation() {
        try {
            LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            if (mLocationManager != null) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    {
                        mLocationManager.removeUpdates((android.location.LocationListener) MapsActivity.this);
                    }
                }
                for (String provider : providers) {
                    Location l = mLocationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
                return bestLocation;
            }} catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    public void LocationConfirmationAlert(final String Address, final double lat, final double lont) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
            alertDialog.setTitle("Is this the Address you mean for the appointment ?");
            alertDialog.setMessage(Address);
            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            intent.putExtra(SER_LOCATION_KEY, loc);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
            alertDialog.show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Something went wrong please contact your support.", Toast.LENGTH_LONG).show();
        }
    }


    public void GetAddressfromLocation(double latitude, double longitude) {
        try {
            GPSTracker gps = new GPSTracker(getApplicationContext());
            if (gps.canGetLocation()) {
                LocationAddress locationAddress = new LocationAddress();
                boolean isnetwork = ServiceUtil.isNetworkConnected(getApplicationContext());
                if (isnetwork) {

                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                }
            } else {
                showSettingsAlert();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS  settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapsActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        Toast.makeText(this, place.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }


    public class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location latestlocation) {
            // Called when a new location is found by the network location provider.

        }


    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if (locationAddress != null) {
                address = null;
                address = locationAddress;
                LocationConfirmationAlert(address, latitude, longtiude);
            }
        }
    }
}
