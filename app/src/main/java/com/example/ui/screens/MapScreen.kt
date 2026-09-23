package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.GhostDetectionEntity
import com.example.ui.components.CollapsibleHudWindow
import com.example.ui.components.TacticalTacticalMapFallback
import com.example.ui.theme.*
import com.example.ui.viewmodel.GhostViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: GhostViewModel) {
    val allDetections by viewModel.allDetections.collectAsState()
    val context = LocalContext.current

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Mode state: OpenStreetMap vs Tactical Radar Map Fallback
    var useTacticalFallback by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(InfraGreenBackground)) {
        if (useTacticalFallback) {
            TacticalTacticalMapFallback(
                detections = allDetections,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // OpenStreetMap
            OsmMapView(
                detections = allDetections,
                myLocationEnabled = locationPermissionsState.allPermissionsGranted,
                modifier = Modifier.fillMaxSize()
            )

            // Permission Request Overlay if not granted
            if (!locationPermissionsState.allPermissionsGranted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = InfraGreenSurface),
                        border = CardDefaults.outlinedCardBorder(enabled = true),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = InfraGreenPrimary,
                                modifier = Modifier.size(48.dp).padding(bottom = 8.dp)
                            )
                            Text(
                                text = "GPS-Freigabe erforderlich",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = InfraGreenPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Um deine Position auf der Karte anzuzeigen und Anomalien in deiner Nähe zu orten, wird der GPS-Zugriff benötigt.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = InfraGreenTextPrimaryVariant,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = InfraGreenPrimary)
                            ) {
                                Text(
                                    "GPS Freigeben",
                                    color = Color.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Tactical Control Bar & Map Mode Toggle Window
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            CollapsibleHudWindow(
                title = if (useTacticalFallback) "TAKTIK-RADAR" else "OSM-KARTE",
                icon = if (useTacticalFallback) Icons.Default.Radar else Icons.Default.Map,
                badgeText = "${allDetections.size} GEISTER",
                initialExpanded = true,
                isScrollable = false,
                testTag = "map_control_window"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STATUS: ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InfraGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "${allDetections.size} Anomalien kartiert",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InfraGreenTextPrimaryVariant,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    // Mode switch button
                    OutlinedButton(
                        onClick = { useTacticalFallback = !useTacticalFallback },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = InfraGreenSurfaceVariant,
                            contentColor = InfraGreenPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, InfraGreenPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp).testTag("map_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = InfraGreenPrimary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (useTacticalFallback) "Zur OSM-Karte" else "Zur Taktik-Karte",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OsmMapView(
    detections: List<GhostDetectionEntity>,
    myLocationEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize OSMDroid config once
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("${context.packageName}_osm_preferences", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(51.1657, 10.4515)) // Germany default
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.testTag("osm_map_view")
    ) { map ->
        map.overlays.clear()

        // Add location overlay if enabled
        if (myLocationEnabled) {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            locationOverlay.enableMyLocation()
            // Optionally follow location
            // locationOverlay.enableFollowLocation()
            map.overlays.add(locationOverlay)
            
            if (locationOverlay.myLocation != null) {
                map.controller.setCenter(locationOverlay.myLocation)
            }
        }

        // Add markers for detections
        detections.forEach { ghost ->
            if (ghost.latitude != null && ghost.longitude != null) {
                val marker = Marker(map)
                marker.position = GeoPoint(ghost.latitude, ghost.longitude)
                marker.title = "${ghost.name} (${ghost.type})"
                marker.snippet = "Gefahr: ${ghost.dangerLevel}/10 • EMF: ${ghost.emfLevel} mG"
                map.overlays.add(marker)
            }
        }
        
        map.invalidate()
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }
}
