package senba.tourisme.senbalok.fragment;
/*
NIM : 10119084
Nama : Muhammad idris Merdefi
Kelas : IF-2
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import senba.tourisme.senbalok.R;

public class MyLocationFragment extends Fragment {

    // Initialize variables
    FusedLocationProviderClient client;
    private GoogleMap map;

    public MyLocationFragment() {
    }

    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        //menginisialisasi map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);



        //Inisialisasi client
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
                for (int i=0;i<arrayList.size();i++){
                    map.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
                }
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));

                    }
                });

                //Mengecek izin aplikasi untuk menggunakan lokasi
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // pemanggilan method untuk mengambil lokasi saat ini
                    getCurrentLocation();
                }
                //kondisi yang akan dijalankan bila izin aplikasi tidak diberikan
                else {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
                }
            }
            @SuppressLint("MissingPermission")
            private void getCurrentLocation()
            {
                // Mnisialisasi map fragment
                SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

                LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    client.getLastLocation().addOnCompleteListener(
                            (Task<Location> task) -> {
                                // Menginisialisasi lokasi
                                Location location = task.getResult();
                                // Mengecek kondisi apakah pap kosong atau tidak
                                if (location != null) {
                                    // When location result is not null set latitude and longitude
                                    mapFragment.getMapAsync(googleMap -> {
                                        LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Anda");
                                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,15));
                                        googleMap.addMarker(options);
                                    });
                                }
                                else {
                                    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                                    LocationCallback locationCallback = new LocationCallback() {
                                        @Override
                                        public void
                                        onLocationResult(@NonNull LocationResult locationResult)
                                        {
                                            mapFragment.getMapAsync(googleMap -> {
                                                LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                                MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Sekarang");
                                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,15));
                                                googleMap.addMarker(options);
                                            });
                                        }
                                    };
                                    // update lokasi
                                    client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                                }
                            });
                }
                else {
                    startActivity(
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
        return view;
    }
}
