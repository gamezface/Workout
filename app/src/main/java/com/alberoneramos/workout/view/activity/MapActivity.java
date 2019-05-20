package com.alberoneramos.workout.view.activity;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alberoneramos.workout.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, MapboxMap.OnMoveListener {


    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private static final String FILL_LAYER_ID = "FILL_LAYER_ID";
    private static final String GEO_JSON_SOURCE_ID = "Academia";
    private static final String ID_OF_LAYER_TO_HIGHLIGHT = "Academia";
    private GeoJsonSource dataGeoJsonSource;
    private Button redoSearchButton;
    private boolean moveMapInstructionShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZ2FtZXpmYWNlIiwiYSI6ImNqdndkaGQ3NDRid3A0Y29pbHNhODNkZzUifQ.lGdytqychZA8hKU3YfX8kA");
        setContentView(R.layout.activity_map);
        redoSearchButton = findViewById(R.id.redo_search_button);
        moveMapInstructionShown = false;
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                this::enableLocationComponent);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

                mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                mapboxMap.addOnMoveListener(MapActivity.this);

                style.addSource(new GeoJsonSource(GEO_JSON_SOURCE_ID,
                        FeatureCollection.fromFeatures(new Feature[]{})));

                style.addLayer(new FillLayer(FILL_LAYER_ID,
                        GEO_JSON_SOURCE_ID).withProperties(
                        fillOpacity(interpolate(exponential(1f), zoom(),
                                stop(3, 0f),
                                stop(8, .5f),
                                stop(15f, 1f))),
                        fillColor(Color.parseColor("#00F7FF"))
                ));

                redoSearchButton.setOnClickListener(view -> {
                    if (!moveMapInstructionShown) {
                        Toast.makeText(MapActivity.this,
                                "Mova o mapa e tente novamente", Toast.LENGTH_SHORT).show();
                        moveMapInstructionShown = true;
                    }
                    FeatureCollection featureCollection = null;
                    if (style.getLayer(ID_OF_LAYER_TO_HIGHLIGHT) != null) {
                        featureCollection = FeatureCollection.fromFeatures(getFeaturesInViewport(ID_OF_LAYER_TO_HIGHLIGHT));
                    } else {
                        Toast.makeText(MapActivity.this,
                                String.format("Não encontrado", ID_OF_LAYER_TO_HIGHLIGHT),
                                Toast.LENGTH_SHORT).show();
                    }
// Retrieve and update the GeoJSON source used in the FillLayer
                    dataGeoJsonSource = style.getSourceAs(GEO_JSON_SOURCE_ID);
                    if (dataGeoJsonSource != null && featureCollection != null) {
                        dataGeoJsonSource.setGeoJson(featureCollection);
                    }
                    redoSearchButton.setVisibility(View.INVISIBLE);
                });
            });
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    /**
     * Perform feature query within the viewport.
     */
    private List<Feature> getFeaturesInViewport(String layerName) {
        RectF rectF = new RectF(mapView.getLeft(),
                mapView.getTop(), mapView.getRight(), mapView.getBottom());
        return mapboxMap.queryRenderedFeatures(rectF, layerName);
    }

    @Override
    public void onMoveEnd(MoveGestureDetector detector) {
        redoSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMoveBegin(MoveGestureDetector detector) {
// Left empty on purpose
    }

    @Override
    public void onMove(MoveGestureDetector detector) {
// Left empty on purpose
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Precisamos da sua localização para buscar academias próximas", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(this, "Permissão não concedida", Toast.LENGTH_LONG).show();
            finish();
        }
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
