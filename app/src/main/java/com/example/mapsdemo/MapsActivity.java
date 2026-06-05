package com.example.mapsdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker currentMarker;       // Un seul marker, sera déplacé

    // Demander les permissions au runtime
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Vérifier permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            // Demander la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        try {
            // Vérifier si le GPS est activé (sinon afficher dialogue)
            if (!isGpsEnabled()) {
                buildAlertMessageNoGps();
                return;
            }

            // S'abonner aux mises à jour de position (Network Provider = rapide, moins précis)
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    2000,   // toutes les 2 secondes (pour les tests)
                    10,     // 10 mètres de déplacement minimum
                    this
            );
            // Optionnel : ajouter aussi GPS_PROVIDER pour plus de précision
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    10,
                    this
            );

            Toast.makeText(this, "Recherche de position...", Toast.LENGTH_SHORT).show();

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur de permission", Toast.LENGTH_LONG).show();
        }
    }

    // Vérifie si le GPS (ou réseau) est activé
    private boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Boîte de dialogue pour inviter l'utilisateur à activer la localisation
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre GPS semble désactivé. Voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Toast.makeText(MapsActivity.this, "Localisation désactivée", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // --- Implémentation de LocationListener ---
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng position = new LatLng(latitude, longitude);

        // Afficher un toast pour debug (optionnel)
        Toast.makeText(this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();

        // Gérer le marker : un seul, on le déplace
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Vous êtes ici"));
        } else {
            currentMarker.setPosition(position);
        }

        // Zoomer et centrer sur la nouvelle position
        float zoomLevel = 15.0f; // 15 = niveau rue/quartier
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Non utilisé, mais obligatoire pour l'interface
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(this, "Provider activé : " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Si le provider (GPS ou réseau) est désactivé, on propose d'activer
        if (provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    // --- Gestion de la réponse à la demande de permission ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission acceptée
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission refusée, impossible d'afficher la carte", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Nettoyer les listeners lors de la destruction de l'activité
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}