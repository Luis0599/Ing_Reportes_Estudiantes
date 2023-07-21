package com.example.ingreportesestudiantes.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder

import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingreportesestudiantes.R
import com.example.ingreportesestudiantes.conexionInternet.conexionInternet
import com.example.ingreportesestudiantes.databinding.FragmentHomeBinding
import com.example.ingreportesestudiantes.interfaces.registrarEstudiante
import com.example.ingreportesestudiantes.interfaces.reporteEstudiante
import com.example.ingreportesestudiantes.modelos.Reporte
import com.example.ingreportesestudiantes.modelos.ubicacionTiempoReal
import com.example.ingreportesestudiantes.modelos.usuarios
import com.example.ingreportesestudiantes.setProgressDialog
import com.example.ingreportesestudiantes.ubicacionEnTiempoReal.MiUbicacion
import com.example.ingreportesestudiantes.ui.slideshow.SlideshowFragment
import com.example.kerklyv5.url.Url
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.GoogleMap
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.DateFormat
import java.util.Date
import java.util.Locale

//import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val REQUEST_LOCATION_PERMISSION = 1
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    lateinit var pais: String
    private lateinit var ciudad: String
    private  lateinit var estado:String
    private lateinit var colonia:String
    private lateinit var calle:String
    private lateinit var cp:String
    private  lateinit var num_ext:String
    private var locationManager: LocationManager? = null

    private lateinit var MiDireccion:String
    private lateinit var layot_oficio: TextInputLayout
    private lateinit var txt_oficio: TextInputEditText
    private lateinit var layout_palabrasClaves: TextInputLayout
    private lateinit var txt_palabrasClaves: TextInputEditText
    private lateinit var idEstudiante:String
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    val setProgress= setProgressDialog()
    private lateinit var layout_NombreDTecnico: TextInputLayout
    private lateinit var txt_NombreDeltecnico: TextInputEditText
    private lateinit var layout_Telefono: TextInputLayout
    private lateinit var txt_telefonoTecnico: TextInputEditText

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View =binding.root
        // Obtener los argumentos pasados al fragmento
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        layot_oficio = root.findViewById(R.id.layoutNombreOficio)
        txt_oficio = root.findViewById(R.id.edit_Nombre_oficio)
        layout_palabrasClaves = root.findViewById(R.id.layoutPalabrasC)
        txt_palabrasClaves = root.findViewById(R.id.edit_PalabrasClaves)

        layout_NombreDTecnico = root.findViewById(R.id.layoutNombreDelTecnico)
        txt_NombreDeltecnico = root.findViewById(R.id.edit_Nombre_DelTecnico)
        layout_Telefono = root.findViewById(R.id.layoutTelefono)
        txt_telefonoTecnico = root.findViewById(R.id.edit_Telefono)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Verifica si se concedió el permiso de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso ya está concedido, puedes acceder a la ubicación
            // Llama a la función para obtener la ubicación aquí
            currentUser = mAuth!!.currentUser
            if(currentUser != null){
              getLocation()
            }

        } else {
            // El permiso no está concedido, solicítalo en tiempo de ejecución
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }

        val getLocationButton: Button = root.findViewById(R.id.buttonRegistrar)
        getLocationButton.setOnClickListener {
            val oficio = txt_oficio.text.toString()
            val palabrasClaves =  txt_palabrasClaves.text.toString()
            val nombreDelTecnico = txt_NombreDeltecnico.text.toString()
            val telefonoDelTecnico = txt_telefonoTecnico.text.toString()
            idEstudiante = currentUser!!.uid.toString()
            val conexion = conexionInternet(requireContext())
            if (conexion.isInternetConnected()) {
                // El usuario tiene conexión a internet
                locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!gpsEnabled) {
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(settingsIntent)
                }else {
                   getLocation()
                    if (oficio.isEmpty()){
                        layot_oficio.error = "Campo requerido"
                    }else{
                        layot_oficio.error = null
                    }
                    if (palabrasClaves.isEmpty()){
                        layout_palabrasClaves.error = "Campo requerido"
                    }else{
                        layout_palabrasClaves.error = null
                    }

                    if (nombreDelTecnico.isEmpty()){
                        layout_NombreDTecnico.error = "Campo requerido"
                    }else{
                        layout_NombreDTecnico.error = null
                    }
                    if (telefonoDelTecnico.isEmpty()){
                        layout_Telefono.error = "Campo requerido"
                    }else{
                        layout_Telefono.error = null
                    }

                    if (latitud != 0.0 && longitud != 0.0) {
                        //showToast("Latitud: $latitud, Longitud: $longitud")
                        if (!oficio.isEmpty() && !palabrasClaves.isEmpty() && !telefonoDelTecnico.isEmpty() && !nombreDelTecnico.isEmpty()){
                            EnviarReporteMysQL(oficio,palabrasClaves, currentUser!!.email!!,telefonoDelTecnico,nombreDelTecnico)
                            setLocation(idEstudiante,latitud, longitud,oficio,palabrasClaves,nombreDelTecnico,telefonoDelTecnico)
                            txt_oficio.setText(null)
                            txt_palabrasClaves.setText(null)
                            txt_telefonoTecnico.setText(null)
                            txt_NombreDeltecnico.setText(null)
                          //  fusedLocationClient?.removeLocationUpdates(locationCallback)
                        }
                    } else {
                      // showToast("Latitud: $latitud, Longitud: $longitud")
                    }
                }
            } else {
                // El usuario no tiene conexión a internet
                showToast("No tienes conexión a internet")
            }
        }
        return root
    }


    // Este método se invoca cuando el usuario responde a la solicitud de permisos en tiempo de ejecución
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes acceder a la ubicación
                getLocation()
            } else {
                // Permiso denegado, muestra un mensaje o realiza alguna acción apropiada
                showToast("Permiso de ubicación denegado.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setLocation(idEstudiante: String,latitud: Double, longitud: Double,oficio: String,palabrasClaves: String,nombreTecnico:String,telefono:String) {
        if (latitud != 0.0 && longitud != 0.0) {
            try {
                val geocoder: Geocoder
                val direccion: List<Address>
                geocoder = Geocoder(requireContext(), Locale.getDefault())

                direccion =
                    geocoder.getFromLocation(latitud, longitud, 1)!! // 1 representa la cantidad de resultados a obtener
                //val address = direccion[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                //Toast.makeText(this, "Entroooo ${direccion[0].locality}", Toast.LENGTH_SHORT).show()
                if (direccion[0].locality == null || direccion[0].subLocality == null || direccion[0].thoroughfare == null){
                   //   Toast.makeText(this, "Entroooo ${direccion[0].postalCode}", Toast.LENGTH_SHORT).show()
                    pais = direccion[0].countryName
                    estado = direccion[0].adminArea
                    ciudad = "Sin nombre"
                    colonia = "Sin nombre"
                    calle = "Sin Nombre"
                    cp = "NULL"
                    num_ext = "Sin número"
                    println("mi direccion $pais $estado $ciudad  $colonia $calle $num_ext $cp")
                    MiDireccion = "$pais $estado $ciudad  $colonia $calle $num_ext $cp"
                    EnviarReporte(idEstudiante,MiDireccion, latitud.toString(), longitud.toString(),oficio, palabrasClaves,nombreTecnico,telefono)
                }else{
                    ciudad = direccion[0].locality // ciudad
                    estado = direccion[0].adminArea //estado
                    pais = direccion[0].countryName // pais
                    cp = direccion[0].postalCode //codigo Postal
                    calle = direccion[0].thoroughfare // la calle
                    colonia =  direccion[0].subLocality// colonia
                    num_ext = direccion[0].subThoroughfare

                    println("mi direccion $pais $estado $ciudad  $colonia $calle $num_ext $cp")
                    MiDireccion = "$pais $estado $ciudad  $colonia $calle $num_ext $cp"
                    EnviarReporte(idEstudiante,MiDireccion, latitud.toString(), longitud.toString(),oficio, palabrasClaves,nombreTecnico,telefono)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    private  fun EnviarReporteMysQL(nombreOficio:String,palabrasClaves: String,correoEstudiante:String,telefono:String,nombreTecnico:String){
        setProgress.setProgressDialog(requireContext())
        val Url = Url().url
        val adapter = RestAdapter.Builder()
            .setEndpoint(Url)
            .build()
        val api: reporteEstudiante? = adapter.create(reporteEstudiante::class.java)
        api!!.EnviarReporteEstudiante(nombreOficio,palabrasClaves,correoEstudiante,telefono,nombreTecnico,
            object : Callback<Response?> {
                @SuppressLint("SuspiciousIndentation")
                override fun success(t: Response?, response: Response?) {
                    var entrada: BufferedReader? =  null
                    var Respuesta = ""
                    try {
                        entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        Respuesta = entrada.readLine()
                        //Toast.makeText(contexto,Respuesta,Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    var Res = "Registrado"
                    if (Res.equals(Respuesta)){
                        showToast(Respuesta)
                        setProgress.dialog.dismiss()
                    }else{

                        setProgress.dialog.dismiss()
                        showToast(Respuesta)
                    }

                }
                override fun failure(error: RetrofitError) {
                    Toast.makeText(requireContext(), "error $error" , Toast.LENGTH_SHORT).show()
                }

            }
        )

    }

    private fun EnviarReporte(idEstudiante: String, direccion: String, latitud: String, longitud: String,oficio: String, palabrasClaves: String,nombreTecnico: String,telefono:String){
        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("Estudiantes").child(idEstudiante!!)
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        val u = Reporte(direccion, currentDateTimeString, latitud, longitud, oficio,palabrasClaves,nombreTecnico,telefono)
        databaseReference.child("MisReportes").push().setValue(u) { error, ref ->
           // showToast("Reporte Enviado.")
        }

    }

    private fun enviarUbicacionEnTiempoReal(latitud: String,longitud: String){
        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("Estudiantes").child(currentUser!!.uid)
        val u = ubicacionTiempoReal(latitud, longitud)
        databaseReference.child("MisCoordenadas").setValue(u) { error, ref ->
            // showToast("Reporte Enviado.")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Configurar la callback para recibir actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.lastLocation?.let {
                    // Aquí puedes obtener las coordenadas de ubicación en tiempo real
                     latitud = it.latitude
                     longitud = it.longitude
                    println("latitud $latitud longitud $longitud")
                    //showToast("Latitud: $latitude, Longitud: $longitude")
                    enviarUbicacionEnTiempoReal(latitud.toString(), longitud.toString())
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
    override fun onPause() {
        super.onPause()
       // val listener = activity as OnDataPassListener
       // listener.onDataPass("$latitud $longitud")
        if(currentUser != null){
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }

    }
}