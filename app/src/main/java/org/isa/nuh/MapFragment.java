package org.isa.nuh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

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

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
