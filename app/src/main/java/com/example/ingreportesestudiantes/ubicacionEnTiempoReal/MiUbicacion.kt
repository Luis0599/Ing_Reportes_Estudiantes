package com.example.ingreportesestudiantes.ubicacionEnTiempoReal

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MiUbicacion(context: Context) {
    private val context = context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Configurar la callback para recibir actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.lastLocation?.let {
                    // Aquí puedes obtener las coordenadas de ubicación en tiempo real
                    val latitud = it.latitude
                    val longitud = it.longitude
                    println("latitud $latitud longitud $longitud")
                    //showToast("Latitud: $latitude, Longitud: $longitude")
                  //  enviarUbicacionEnTiempoReal(latitud.toString(), longitud.toString())
                    // Llamando al método onDataPass en el MainActivity desde FragmentA


                }
            }
        }

        // Solicitar actualizaciones de ubicación
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000) // Intervalo en milisegundos para recibir actualizaciones (10 segundos)
            .setFastestInterval(5000) // Intervalo mínimo en milisegundos entre actualizaciones (5 segundos)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        return
    }
}