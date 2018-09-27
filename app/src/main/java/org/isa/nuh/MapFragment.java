package org.isa.nuh;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLoacationProviderClient;
    LatLng deviceLocation;

    private GoogleMap mMap;
    private String dummyJSON = "[\n" +
            "    {\n" +
            "        \"position\": {\n" +
            "            \"lat\": \"12.43\",\n" +
            "            \"lng\": \"23.445\"\n" +
            "        }\n" +
            "    },\n" +
            "    {\n" +
            "        \"position\": {\n" +
            "            \"lat\": \"11.43\",\n" +
            "            \"lng\": \"10.445\"\n" +
            "        }\n" +
            "    },\n" +
            "    {\n" +
            "        \"position\": {\n" +
            "            \"lat\": \"-12.43\",\n" +
            "            \"lng\": \"-3.445\"\n" +
            "        }\n" +
            "    },\n" +
            "]";

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initMap();
        List<String> categories = new ArrayList<>(10);
        categories.add(getResources().getString(R.string.need_accommodation));
        categories.add(getResources().getString(R.string.need_food));
        categories.add(getResources().getString(R.string.need_clothes));
        categories.add(getResources().getString(R.string.need_medicine));
        categories.add(getResources().getString(R.string.volunteer_need));
        categories.add(getResources().getString(R.string.need_other));

        categories.add(getResources().getString(R.string.give_accommodation));
        categories.add(getResources().getString(R.string.give_food));
        categories.add(getResources().getString(R.string.give_clothes));
        categories.add(getResources().getString(R.string.give_medicine));
        categories.add(getResources().getString(R.string.volunteer_be));
        categories.add(getResources().getString(R.string.give_other));

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1, categories);

        Spinner spinner = getActivity().findViewById(R.id.help_spinner);
        spinner.setAdapter(categoriesAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: needAccomodation(); break;
                    case 1: needFood(); break;
                    case 2: needClothes(); break;
                    case 3: needMedicine(); break;
                    case 4: needVolunteer(); break;
                    case 5: needOther(); break;

                    case 6: giveAccomodation(); break;
                    case 7: giveFood(); break;
                    case 8: giveClothes(); break;
                    case 9: giveMedicine(); break;
                    case 10: giveVolunteer(); break;
                    case 11: giveOther(); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (mMap != null) mMap.clear();
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.locationFab);
        fab.setOnClickListener(v -> {
            if (mLocationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();
                moveCamera(deviceLocation, 15f);
            } else {
                getLocationPermission();
                getDeviceLocation();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initMap() {
        if (!isServicesOK()) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void getDeviceLocation() {
        mFusedLoacationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLoacationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        if (currentLocation == null) return;
                        deviceLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    } else {
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
        }
    }

    void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void moveCamera(LatLng latLng, float zoom) {
        if (latLng == null) return;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void addMarkersFromJSON(String JSON) {
        try {
            JSONArray array = new JSONArray(JSON);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JSONObject position = obj.getJSONObject("position");
                double latitude = Double.parseDouble(position.getString("lat"));
                double longitude = Double.parseDouble(position.getString("lng"));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Title"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // These methods make get request and retrieve locations on map for NeedHelp
    public void needAccomodation() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void needFood() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void needClothes() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void needMedicine() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void needOther() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void needVolunteer() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    // These methods make get request and retrieve locations on map for GiveHelp
    public void giveAccomodation() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void giveFood() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void giveClothes() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void giveMedicine() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void giveOther() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }

    public void giveVolunteer() {
        try {
            addMarkersFromJSON(dummyJSON);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "Problem loading map", Toast.LENGTH_SHORT).show();
        }
    }
}
