package com.example.bright.RescueHelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.example.bright.RescueHelper.MapHelper.createHole;
import static com.example.bright.RescueHelper.MapHelper.createOuterBounds;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "MapsActivity";

    private static final int TRANSPARENCY_MAX = 100;
    private static Context context;
    public GoogleMap mMap, nMaptwo;
    LocationManager locationManager;
    public EditText mSearchtext;
    private Toolbar toolbar;
    public EditText edit; // zmienna tymczasowa, do pobierania miasta z places autocomplete
    private DrawerLayout mDrawerLayout;
    public ArrayList<Marker> markers = new ArrayList<Marker>();
    public HashMap <Marker, LatLng> markersInMap = new HashMap<>();
    public ArrayList<LatLng> lat = new ArrayList<LatLng>();
    public ArrayList<Integer> markers_id_list = new ArrayList<Integer>();
    public ArrayList<LatLng> lista_polozen = new ArrayList<LatLng>();
    public static int marker_id_counter = 0;
    public ArrayList<LatLng> temp_loc = new ArrayList<LatLng>();
    public PolygonOptions options;
    public static final int POLYGON_POINTS = 5;
    public static int markers_list_size =0;
    Polygon shape;
    public Polygon polygon;
    public Circle circle;
    public LatLng latt;
    public int ile_polygon=0;
    public List<List<LatLng>> holes = new ArrayList<>();
    public List<LatLng> hole = new ArrayList<>();

    public double szerokosc;
    public double dlugosc;

    private static final String MOON_MAP_URL_FORMAT =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Flag_of_Afghanistan_%281880%E2%80%931901%29.svg/2000px-Flag_of_Afghanistan_%281880%E2%80%931901%29.svg.png";

    private TileOverlay mMoonTiles;
    private SeekBar mTransparencyBar;

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String current_user_id;
    private DatabaseReference UsersRef, FriendsRef, LocRef;

    private String online_user_id;
    public double wartosc_lat;
    public double wartosc_lng;

    PersonProfileActivity PersonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_maps);
        edit = findViewById(R.id.editText);
        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);
        //------ Fragment mapy do wyszukiwania lokalizacji
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //SupportMapFragment mapFragmenttwo = (SupportMapFragment) getSupportFragmentManager()
                //.findFragmentById(R.id.maptwo);
        mapFragment.getMapAsync(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
       //mapFragmenttwo.getMapAsync(this);
        //---------------------------------------------------------------

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        online_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        LocRef = FirebaseDatabase.getInstance().getReference().child("Friends_lozalization").child(online_user_id);
        wartosc_lat =getIntent().getDoubleExtra("latitude",0);//0 is default value
        wartosc_lng =getIntent().getDoubleExtra("longitude",0);//0 is default value
        Log.d("Wziete poloenie lat", "" +wartosc_lat);
        Log.d("Wziete poloenie lat", "" +wartosc_lng);




        //-----------------------------------------------------
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //toastMessage("Successfully signed out.");
                }
                // ...
            }
        };




        mDrawerLayout = findViewById(R.id.drawer_layout);
        //NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_camera) {
                            if(markers.size() == POLYGON_POINTS) {
                                removeEverything();
                                shape = null;
                                holes = new ArrayList<>();
                                markers.clear();
                                lat.clear();
                                Log.e(">>>>>>>>>>>>>>>>>>>>> CZYSZCZE <<<<<<<<<<<<<<<<<<<", "");
                            }
                            else Toast.makeText(MapsActivity.this, "Nie ma czego usuwac", Toast.LENGTH_SHORT).show();

                        } else if (id == R.id.nav_gallery) {
                            startActivity(new Intent(getApplicationContext(),FindAllUsers.class));


                        } else if (id == R.id.nav_slideshow) {
                            startActivity(new Intent(getApplicationContext(),FriendsActivity.class));


                        } else if (id == R.id.nav_manage) {

                        }
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        return true;/*if((menuItem.getTitle()).toString()=="import")
                        {
                            setContentView(R.layout.activity_options);
                        }*/
                        // set item as selected to persist highlight
                        // close drawer when item is tapped

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                    }
                });


        // sprawdzanie pozwoleń
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int minutes = 2000;
        int distance = 0;

        // wrzucanie na liste moich poprzednich lokacji
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //int ile = 0;
               // ile = ile + 1;
                // pobieram szerokosc
                mMap.setMyLocationEnabled(true); // od Marka xd
                szerokosc = location.getLatitude();

                // pobieram dlugosc
                dlugosc = location.getLongitude();
                SendLocationDataToFirebase();
                latt = new LatLng(szerokosc, dlugosc);
                lista_polozen.add(latt);

                for(int x=0; x<lista_polozen.size(); x++){
                    Log.d("Polozenie: " + x ," Szerokość: " + lista_polozen.get(x).latitude + " Długość: " + lista_polozen.get(x).longitude);
                }
                //temp_loc.add(latLng);
                List<Address> address = null;
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                   address = geocoder.getFromLocation(szerokosc, dlugosc,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // dodaje pina i przyblizam kamere
                MapsActivity.context = getApplicationContext();
                mMap.addMarker(new MarkerOptions()
                        .position(latt)
                        .draggable(true)
                        .title(address.get(0).getLocality())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        ));
                mMap.addCircle(new CircleOptions()
                        .center(latt)
                        .radius(5.0)
                        .strokeWidth(3f)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(70,150,50,50)));

               // if(ile > 2)
               // {

                 //   mMap.addPolygon(MapHelper.createPolygonWithCircle(MapsActivity.this, temp_loc.get(0), 10f));

                //}



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
        //mMap.addPolygon(MapHelper.createPolygonWithCircle(MapsActivity.this, latt, 10f));

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyleone));

            if (!success) {
                Log.e("Fail", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Fail", "Can't find style. Error: ", e);
        }


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this,"YOU CLICKED ON "+marker.getTitle(),Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),MarkersInfoList.class));

            }
        });
       // mMap.setOnMarkerDragListener(MapsActivity.this);

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };

        mMoonTiles = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        mTransparencyBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        /*try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = nMaptwo.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyletwo));

            if (!success) {
                Log.e("Fail", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Fail", "Can't find style. Error: ", e);
        }*/
        //View v = getSupportFragmentManager().findFragmentById(R.id.maptwo).getView();
        //v.setAlpha(0.5f); // Change this value to set the desired alpha

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                /*if(markers.size() == POLYGON_POINTS){
                    removeEverything();
                }*/
                //mMap.setOnMarkerDragListener(MapsActivity.this);
                marker_id_counter = marker_id_counter + 1;
                Log.e("Dodano marker o id: ", ""+marker_id_counter);
                markers_id_list.add(marker_id_counter);
                holes = new ArrayList<>();
                hole = new ArrayList<>();
                /*if(markers.size()==POLYGON_POINTS)
                {
                    removeEverything();
                    shape = null;
                    holes = new ArrayList<>();
                    markers.clear();
                    lat.clear();
                    Log.e(">>>>>>>>>>>>>>>>>>>>> CZYSZCZE <<<<<<<<<<<<<<<<<<<", "");

                }*/
                 markers.add(mMap.addMarker(new MarkerOptions().position(latLng)
                        .draggable(true)
                        .title(""+marker_id_counter)));

                //Log.e("Tytul markera numer "+marker_id_counter, " to: " +markers.get(marker_id_counter-1).getTitle());
                //if(markers.get(marker_id_counter-1).getTitle().equals("1"))
                //{
                 ///   Log.e("BAJABONGO HEJ! "+marker_id_counter, " to: " +markers.get(marker_id_counter-1).getTitle());

                //}

                if(markers.size() == POLYGON_POINTS) {
                    /*options = new PolygonOptions()
                            .fillColor(0x330000FF)
                            .strokeWidth(3)
                            .strokeColor(Color.RED);
*/
                    for (int i = 0; i < POLYGON_POINTS; i++) {
                        //options.add(markers.get(i).getPosition());
                        lat.add(markers.get(i).getPosition());
                        Log.e("Lista", " " +lat.get(i));

                    }
                    hole.add(new LatLng(51.509869, -0.191208));
                    hole.add(new LatLng(51.513287, -0.158464));
                    hole.add(new LatLng(51.505540, -0.151769));
                    hole.add(new LatLng(51.502178, -0.174471));
                    hole.add(new LatLng(51.502444, -0.187989));
                    holes.add(hole);
                    //shape = mMap.addPolygon(createPolygonWithHoles(holes));

                    if(markers.size() == 5)
                    {
                        lat.add(markers.get(0).getPosition());
                        //polygon.setPoints(lat);
                        hole = new ArrayList<>();
                        for(int j = 0; j<markers.size(); j++)
                        {
                            hole.add(lat.get(j));

                        }
                        holes = new ArrayList<>();
                        holes.add(hole);
                        shape = mMap.addPolygon(createPolygonWithHoles(holes));
                       // markers_list_size = markers.size();
                        Log.e("Rozmiar listy markerow ", " " +markers.size());
                       // onMarkerDragStart(markers.get(0));


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
                boolean contains = PolyUtil.containsLocation(latLng, lat, false);
                Log.e("Lezy: ", "" + contains);
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


    public void setFadeIn(View v) {
        if (mMoonTiles == null) {
            return;
        }
        mMoonTiles.setFadeIn(((CheckBox) v).isChecked());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mMoonTiles != null) {
            mMoonTiles.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private static List<LatLng> createBoundsOfEntireMap() {
        final float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }

    static PolygonOptions createPolygonWithHoles(List<List<LatLng>> holes) {
        PolygonOptions polyOptions = new PolygonOptions()
                .fillColor(0x33000000)
                .addAll(createBoundsOfEntireMap())
                .strokeColor(0xFF000000)
                .strokeWidth(5);

        for (List<LatLng> hole : holes) {
            polyOptions.addHole(hole);
        }

        return polyOptions;
    }

   /* @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d("Id wieksze od 5", "");
        if((marker.getTitle()).equals("1")){
            Log.d("Przenoszę marker numer: ", ""+1);
            markers.get(0).setPosition(marker.getPosition());
        }

        else if((marker.getTitle()).equals("2")){
            Log.d("Przenoszę marker numer: ", ""+2);
            markers.get(1).setPosition(marker.getPosition());
            Log.d("Nowa lokalizacja markera numer 2: ", ""+markers.get(1).getPosition());

        }

        else if((marker.getTitle()).equals("" + 3)){
            Log.d("Przenoszę marker numer: ", ""+3);
            markers.get(2).setPosition(marker.getPosition());
        }

        else if((marker.getTitle()).equals("" + 4)){
            Log.d("Przenoszę marker numer: ", ""+4);
            markers.get(3).setPosition(marker.getPosition());
        }

        else if((marker.getTitle()).equals("" + 5)){
            Log.d("Przenoszę marker numer: ", ""+5);
            markers.get(4).setPosition(marker.getPosition());
        }
        Log.d("Przenoszę marker numer: ", ""+marker.getPosition());

    }*/
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void SendLocationDataToFirebase(){
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myRef.child("users").child(current_user_id).child("latitude").setValue(szerokosc);
                myRef.child("users").child(current_user_id).child("longitude").setValue(dlugosc);

                Log.d("Wyslano do bazy polozenie o dlugosci i szerokosci: ", "" + dlugosc + " oraz " + szerokosc);
                Log.d("Dlugosc: ", "" + dlugosc);
                Log.d("Szerokosc: ", "" + szerokosc);
                Double szerokosc_firebase = (dataSnapshot.child("latitude").getValue(Double.class));
                Double dlugosc_firebase = (dataSnapshot.child("longitude").getValue(Double.class));

                Log.d("Dlugosc z firebase: ", "" + dlugosc_firebase);
                Log.d("Szerokosc z firebase: ", "" + szerokosc_firebase);
                }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFriendLocation(){
        LocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG,""+ childDataSnapshot.getKey()); //displays the key for the node
                    Log.v(TAG,""+ childDataSnapshot.getValue());   //gives the value for given keyname
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
