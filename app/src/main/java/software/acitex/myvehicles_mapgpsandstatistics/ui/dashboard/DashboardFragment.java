package software.acitex.myvehicles_mapgpsandstatistics.ui.dashboard;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.unicodelabs.kdgaugeview.KdGaugeView;
import software.acitex.myvehicles_mapgpsandstatistics.R;
import software.acitex.myvehicles_mapgpsandstatistics.Tools.ModeItem;
import software.acitex.myvehicles_mapgpsandstatistics.activities.ModeAdapter;
import software.acitex.myvehicles_mapgpsandstatistics.utils.GPSCallback;
import software.acitex.myvehicles_mapgpsandstatistics.utils.GPSManager;

public class DashboardFragment extends Fragment implements GPSCallback, LocationListener, IOrientationConsumer {

    //Speedometer, GPS, Location
    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isGPSEnabled = false;
    LocationManager locationManager;
    double currentSpeed, kmphSpeed;
    View view;
    KdGaugeView tubeSpeedometer;
    protected ImageButton btCenterMap;
    protected ImageButton btFollowMe;
    private Location currentLocation = null;
    LinearLayout linearLayoutLocation;
    TextView textViewLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    boolean alreadyExecuted = false;

    //Butoane zoom si rotate
    ImageButton zoomInBtn, zoomOutBtn;
    ImageButton rotateLeftBtn, rotateRightBtn;

    //context buttons
    ImageView imgTakePhoto;
    Button btnCaptureImage;
    ImageView imageViewPhoto;
    AlertDialog.Builder alertDialog;
    AlertDialog alert;
    TextView locationInfo;
    TextView photoData;
    ImageView addPin;


    //Spinner Mode
    private ArrayList<ModeItem> modeList;
    private ModeAdapter modeAdapter;

    //realtime tasks
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 100;

    //pentru follow location
    boolean btnFollowPresed = false;
    int pressed = 0;

    //locatie map
    //Constante
    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";
    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id
    float zoomLevelMap = 19f;
    float zoomLevelMapChanged;

    //campuri
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //speedometer, map items
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tubeSpeedometer = view.findViewById(R.id.speedometer);
        linearLayoutLocation = view.findViewById(R.id.linearWeather);
        textViewLocation = view.findViewById(R.id.currentLocationTV);
        mMapView = view.findViewById(R.id.mapview);
        btFollowMe = view.findViewById(R.id.ic_follow_me);
        btCenterMap = view.findViewById(R.id.ic_center_map);
        zoomInBtn = view.findViewById(R.id.imageZoomIn);
        zoomOutBtn = view.findViewById(R.id.imageZoomOut);
        rotateLeftBtn = view.findViewById(R.id.imageRotateLeft);
        rotateRightBtn = view.findViewById(R.id.imageRotateRight);
        imgTakePhoto = view.findViewById(R.id.imgTakePhoto);
        addPin = view.findViewById(R.id.addPinImg);




        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView");


        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET
        });

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        mMapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {

            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_SCROLL:
                            if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                                mMapView.getController().zoomOut();
                            else {
                                //this part just centers the map on the current mouse location before the zoom action occurs
                                IGeoPoint iGeoPoint = mMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                                mMapView.getController().animateTo(iGeoPoint);
                                mMapView.getController().zoomIn();
                            }
                            return true;
                    }
                }
                return false;
            }
        });
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

//        //Mini map
//        mMinimapOverlay = new MinimapOverlay(context, mMapView.getTileRequestCompleteHandler());
//        mMinimapOverlay.setWidth(dm.widthPixels / 5);
//        mMinimapOverlay.setHeight(dm.heightPixels / 5);
//        mMapView.getOverlays().add(this.mMinimapOverlay);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);


        //Copyright overlay
        // mCopyrightOverlay = new CopyrightOverlay(context);
        //i hate this very much, but it seems as if certain versions of android and/or
        //device types handle screen offsets differently
        //mMapView.getOverlays().add(this.mCopyrightOverlay);


        //On screen compass
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(this.mCompassOverlay);


        //map scale
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);


        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);


        //needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        mMapView.setTilesScaledToDpi(false);

        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1);
        mMapView.getController().setZoom(zoomLevelMap);
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        mMapView.setMapOrientation(orientation, false);
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);
        mMapView.setExpectedCenter(new GeoPoint(latitude, longitude));

        if (currentLocation != null) {
            GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMapView.getController().animateTo(myPosition);
            mMapView.setExpectedCenter(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        }

        //
        mLocationOverlay.enableFollowLocation();
        btFollowMe.setImageResource(R.drawable.ic_follow_me_on);


        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        });

        //zoom si rotire
        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevelMapChanged = (float)mMapView.getZoomLevelDouble()+0.5f;
                mMapView.getController().setZoom(zoomLevelMapChanged);
            }
        });

        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevelMapChanged = (float)mMapView.getZoomLevelDouble()-0.5f;
                mMapView.getController().setZoom(zoomLevelMapChanged);
            }
        });

        rotateRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float angle = mMapView.getMapOrientation() - 25;
                if (angle < 0)
                    angle += 360f;
                mMapView.setMapOrientation(angle);
                mMapView.setMapOrientation(angle);
            }
        });

        rotateLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float angle = mMapView.getMapOrientation() + 25;
                if (angle > 360)
                    angle = 360 - angle;
                mMapView.setMapOrientation(angle);
            }
        });



        //Setari butoane center location si follow location
        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation != null) {
                    GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMapView.getController().animateTo(myPosition);
                }
            }
        });

        btFollowMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationOverlay.isFollowLocationEnabled()) {
                    mLocationOverlay.enableFollowLocation();
                    btFollowMe.setImageResource(R.drawable.ic_follow_me_on);
                } else {
                    mLocationOverlay.disableFollowLocation();
                    btFollowMe.setImageResource(R.drawable.ic_follow_me);
                }
            }
        });

        //acordare permisii
        startMyService();

        //citire viteza curenta din GPS
        getCurrentSpeed();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        linearLayoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    } else if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {
                        Toast.makeText(getActivity(), "Permisiile nu au fost acordate", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //Spinner Mode Selector
        initList();

        Spinner spinnerMode = view.findViewById(R.id.spinner_mode);

        modeAdapter = new ModeAdapter(getContext(), modeList);
        spinnerMode.setAdapter(modeAdapter);

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModeItem clickedItem = (ModeItem) parent.getItemAtPosition(position);
                String clickedModeName = clickedItem.getModeName();
                Toast.makeText(getContext(), clickedModeName + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //add pin
        addPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //take photo
        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Request for camera runtime permission
                if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.CAMERA
                    },100);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);




                alertDialog = new AlertDialog.Builder(getContext());
                final View customLayout = getLayoutInflater().inflate(R.layout.take_picture, null);



                imageViewPhoto = (ImageView) customLayout.findViewById(R.id.imageViewPhoto);
                locationInfo = (TextView) customLayout.findViewById(R.id.locationInfo);
                photoData = (TextView) customLayout.findViewById(R.id.photoData);

                alertDialog.setView(customLayout);
                alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);



            }

        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            alert.show();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageViewPhoto.setImageBitmap(bitmap);
            getLocationForPhoto();
            String currentDate = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
            photoData.setText(currentDate);

        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        textViewLocation.setText("Current location: " + addresses.get(0).getCountryName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void getLocationForPhoto() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationInfo.setText(addresses.get(0).getCountryName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void getCurrentSpeed() {

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(getActivity());
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            gpsManager.startListening(getContext());
            gpsManager.setGPSCallback(this);
        } else {
            gpsManager.showSettingsAlert();
        }
    }


    @Override
    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed * 3.6), 0, BigDecimal.ROUND_HALF_UP);
        float a = (float) kmphSpeed;
        String kmIntregi = String.valueOf(a);
        tubeSpeedometer.setSpeed(a);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager = null;
        currentLocation = null;
        mLocationOverlay = null;
        mCompassOverlay = null;
        mRotationGestureOverlay = null;
        btCenterMap = null;
        btFollowMe = null;
        if (mMapView != null)
            mMapView.onDetach();
        mMapView = null;

    }

    @Override
    public void onStop() {
        super.onStop();
        gpsManager.stopListening();

    }

    @Override
    public void onResume() {
        super.onResume();
//
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                if(currentLocation != null) {
//                    long b = 1;
//                    GeoPoint a = null;
//                    //Overlay mapa
//
//                        a = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                    controller.animateTo(a,map.getZoomLevelDouble(),b, (float) map.getMapOrientation());
//                }
//            }
//        }, delay);
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        gpsManager.startListening(getContext());
        if (mMapView != null) {
            mMapView.onResume();
        }




//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        try {
//            //on API15 AVDs,network provider fails. no idea why
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        } catch (Exception ex) {
//            //usually permissions or
//            //java.lang.IllegalArgumentException: provider doesn't exist: network
//            ex.printStackTrace();
//        }

    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
        gpsManager.stopListening();
        mCompassOverlay.disableCompass();
        mScaleBarOverlay.enableScaleBar();
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mMapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mMapView.getZoomLevelDouble());
        edit.commit();

        try {
            locationManager.removeUpdates(this::onGPSUpdate);
        } catch (Exception ex) {
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Put overlay items first
        mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);

        // Put "About" menu item last
        menu.add(0, MENU_ABOUT, Menu.CATEGORY_SECONDARY, R.string.about).setIcon(
                android.R.drawable.ic_menu_info_details);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu pMenu) {
        mMapView.getOverlayManager().onPrepareOptionsMenu(pMenu, MENU_LAST_ID, mMapView);
        super.onPrepareOptionsMenu(pMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mMapView)) {
            return true;
        }

        switch (item.getItemId()) {
            case MENU_ABOUT:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.app_name).setMessage(R.string.about_message_act)
                        .setIcon(R.drawable.btn_moreinfo)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //
                                    }
                                }
                        );
                builder.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void zoomIn() {
        mMapView.getController().zoomIn();
    }

    public void zoomOut() {
        mMapView.getController().zoomOut();
    }

    // @Override
    // public boolean onTrackballEvent(final MotionEvent event) {
    // return this.mMapView.onTrackballEvent(event);
    // }
    public void invalidateMapView() {
        mMapView.invalidate();
    }


    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void startMyService() {
        if (!alreadyExecuted) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 4 seconds
                    try {
                        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                        } else if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            getLocation();
                        } else {
                            Toast.makeText(getActivity(), "Permisiile nu au fost acordate", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alreadyExecuted = true;
                }
            }, 100);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
//        if(btnFollowPresed) {
//            map.getController().animateTo(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()), map.getZoomLevelDouble(), (long) delay, (float) map.getMapOrientation());
//        }
        if(currentLocation!=null) {
            getLocation();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    private void initList(){
        modeList = new ArrayList<>();
        modeList.add(new ModeItem("FREE RIDE MODE", R.drawable.ic_follow_me));
        modeList.add(new ModeItem("ROUTE METERING MODE", R.drawable.ic_follow_me_on));
        modeList.add(new ModeItem("TRACE MODE", R.drawable.osm_ic_ic_map_ortho));
    }

    @Override
    public void onOrientationChanged(final float orientationToMagneticNorth, IOrientationProvider source) {
    }


}

