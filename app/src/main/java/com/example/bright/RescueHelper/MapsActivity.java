package com.example.bright.RescueHelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    LocationManager locationManager;
    public EditText mSearchtext;
    private Toolbar toolbar;
    public EditText edit; // zmienna tymczasowa, do pobierania miasta z places autocomplete
    private DrawerLayout mDrawerLayout;
    public ArrayList<Marker> markers = new ArrayList<Marker>();
    public ArrayList<LatLng> lat = new ArrayList<LatLng>();
    public PolygonOptions options;
    public static final int POLYGON_POINTS = 5;
    Polygon shape;
    public Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_maps);
        edit = findViewById(R.id.editText);

        //------ Fragment mapy do wyszukiwania lokalizacji
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //---------------------------------------------------------------

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        /*if((menuItem.getTitle()).toString()=="import")
                        {
                            setContentView(R.layout.activity_options);
                        }*/
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        // sprawdzanie pozwoleń
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int minutes = 2000;
        int distance = 0;
        // wrzucanie na liste moich poprzednich lokacji
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minutes, distance, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // pobieram szerokosc
                double szerokosc = location.getLatitude();

                // pobieram dlugosc
                double dlugosc = location.getLongitude();

                LatLng latLng = new LatLng(szerokosc, dlugosc);
                List<Address> address = null;
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                   address = geocoder.getFromLocation(szerokosc, dlugosc,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // dodaje pina i przyblizam kamere
                mMap.addMarker(new MarkerOptions().position(latLng).title(address.get(0).getLocality()));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.5f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        // kontener na pobrane miasto
        // fragment mapy do autocomplete places
        final TextView txtVw = findViewById(R.id.placeName);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // ustawiam filtr krajów - tu PL, czyli Polska
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("PL")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        // ustawiam co ma się stać po kliknięciu w wyszukane miasto
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                txtVw.setText(place.getName());
                edit.setText(place.getName());

            }
            @Override
            public void onError(Status status) {
                txtVw.setText(status.toString());
                edit.setText(status.toString());
            }
        });
    }

    // obsluga buttonu Search, który niedługo trzeba zamienić na coś bardziej przystępnego i nowoczesnego


    // jesli mapka jest juz gotowa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                /*if(markers.size() == POLYGON_POINTS){
                    removeEverything();
                }*/
                markers.add(mMap.addMarker(new MarkerOptions().position(latLng)));
                if(markers.size() == POLYGON_POINTS) {
                    options = new PolygonOptions()
                            .fillColor(0x330000FF)
                            .strokeWidth(3)
                            .strokeColor(Color.RED);

                    for (int i = 0; i < POLYGON_POINTS; i++) {
                        options.add(markers.get(i).getPosition());
                        lat.add(markers.get(i).getPosition());
                        Log.e("Lista", " " +lat.get(i));

                    }
                    polygon = mMap.addPolygon(new PolygonOptions()
                            .add(new LatLng(0, 0), new LatLng(0, 5), new LatLng(3, 5), new LatLng(0, 0))
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                    if(markers.size() == 5)
                    {
                        lat.add(markers.get(0).getPosition());
                        polygon.setPoints(lat);

                    }
                        //shape = mMap.addPolygon(options);
                }

            }

        });






        if(OptionsActivity.stanSwitch2==true)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        if (OptionsActivity.stanSwitch1==true)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        if (OptionsActivity.stanSwitch3==true)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        if  (OptionsActivity.stanSwitch4==true) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onSearch (View view)
    {
        if(edit.getText().toString()!="")
        {
            String location = edit.getText().toString();
            List<Address> addressList = null;
            if(location != null || !location.equals("")) {

                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
        else Toast.makeText(this, "Podaj dane", Toast.LENGTH_SHORT).show();

            /*lat.add(lat.get(0));
            polygon.setPoints(lat);*/
    }


    private void removeEverything(){
        for(Marker marker :markers){
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape = null;
    }
}
