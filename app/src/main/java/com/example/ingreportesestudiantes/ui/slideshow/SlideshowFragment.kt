package com.example.ingreportesestudiantes.ui.slideshow

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingreportesestudiantes.databinding.FragmentSlideshowBinding
import com.example.ingreportesestudiantes.modelos.Reporte
import com.example.ingreportesestudiantes.modelos.modeloPreguntasAPersonas
import com.example.ingreportesestudiantes.modelos.ubicacionTiempoReal
import com.example.ingreportesestudiantes.setProgressDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.Locale


class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var editexPregunta1: TextInputEditText
    private lateinit var editexPregunta2: TextInputEditText
    private lateinit var editexPregunta3: TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var editexPregunta4: TextInputEditText
    private lateinit var editexPregunta5: TextInputEditText
    private lateinit var editexPregunta6: TextInputEditText
    private lateinit var editexPregunta7: TextInputEditText
    private lateinit var editexPregunta8: TextInputEditText

    private lateinit var layoutPregunta1: TextInputLayout
    private lateinit var layoutPregunta2: TextInputLayout
    private lateinit var layoutPregunta3: TextInputLayout
    private lateinit var layoutPregunta4: TextInputLayout
    private lateinit var layoutPregunta5: TextInputLayout
    private lateinit var layoutPregunta6: TextInputLayout
    private lateinit var layoutPregunta7: TextInputLayout
    private lateinit var layoutPregunta8: TextInputLayout

    private lateinit var linearLayout: LinearLayout

    private lateinit var btnGuardar: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationManager: LocationManager? = null
    private val REQUEST_LOCATION_PERMISSION = 1
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var MiDireccion:String
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    val setProgress= setProgressDialog()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Verifica si se concedió el permiso de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso ya está concedido, puedes acceder a la ubicación
            // Llama a la función para obtener la ubicación aquí
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

        layoutPregunta1 = binding.layoutPregunta1
        layoutPregunta2 = binding.layoutPregunta2
        layoutPregunta3 = binding.layoutPregunta3
        layoutPregunta4 = binding.layoutPregunta4
        layoutPregunta5 = binding.layoutPregunta5
        layoutPregunta6 = binding.layoutPregunta6
        layoutPregunta7 = binding.layoutPregunta7
        layoutPregunta8 = binding.layoutPregunta8

        editexPregunta1 = binding.editPregunta1
        editexPregunta2 = binding.editPregunta2
        editexPregunta3 = binding.editPregunta3
        editexPregunta4 = binding.editPregunta4
        radioGroup = binding.radioGrup
        editexPregunta5 = binding.editPregunta5
        editexPregunta6 = binding.editPregunta6
        editexPregunta7 = binding.editPregunta7
        editexPregunta8 = binding.editPregunta8
                //val textView: TextView = binding.textSlideshow
        linearLayout = binding.LinearlayoutRespuesta4
        btnGuardar = binding.buttonRegistrarFormulario
        val radioButton: RadioButton = binding.radioButtonSi
        radioButton.setOnClickListener {
            linearLayout.visibility = View.VISIBLE
        }
        val radioButtonNo: RadioButton = binding.radioButtonNo
        radioButtonNo.setOnClickListener {
            linearLayout.visibility = View.GONE
        }
        btnGuardar.setOnClickListener {
            // Recibir los datos enviados desde FragmentA

            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val respuesta1 = editexPregunta1.text.toString()
            val respuesta2 = editexPregunta2.text.toString()
            val respuesta3 = editexPregunta3.text.toString()
            val respuesta5 = editexPregunta5.text.toString()
            val respuesta6 = editexPregunta6.text.toString()
            val respuesta7 = editexPregunta7.text.toString()
            val respuesta8 = editexPregunta8.text.toString()

            if (respuesta1.isEmpty()){
                layoutPregunta1.error = "Campo Requerido"
            }else{
                layoutPregunta1.error = null
            }
            if (respuesta2.isEmpty()){
                layoutPregunta2.error = "Campo Requerido"
            }else{
                layoutPregunta2.error = null
            }
            if (respuesta3.isEmpty()){
                layoutPregunta3.error = "Campo Requerido"
            }else{
                layoutPregunta3.error = null
            }
            if (respuesta5.isEmpty()){
                layoutPregunta5.error = "Campo Requerido"
            }else{
                layoutPregunta5.error = null
            }
            if (respuesta6.isEmpty()){
                layoutPregunta6.error = "Campo Requerido"
            }else{
                layoutPregunta6.error = null
            }
            if (respuesta7.isEmpty()){
                layoutPregunta7.error = "Campo Requerido"
            }else{
                layoutPregunta7.error = null
            }
            if (respuesta8.isEmpty()){
                layoutPregunta8.error = "Campo Requerido"
            }else{
                layoutPregunta8.error = null
            }
            val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
            if (selectedRadioButtonId != -1) {
                // Se ha seleccionado un RadioButton
                val selectedRadioButton: RadioButton = root.findViewById(selectedRadioButtonId)
               val selectedOptionText = selectedRadioButton.text.toString()
                //Toast.makeText(requireContext(), "Opción seleccionada: $selectedOptionText", Toast.LENGTH_SHORT).show()

                if (selectedOptionText == "SI"){
                    val respuesta4 = editexPregunta4.text.toString()
                    if (respuesta4.isEmpty()){
                        layoutPregunta4.error =  "Campo requerido"
                    }else{
                        layoutPregunta4.error = null
                        setLocation(currentDateTimeString,latitud,longitud, respuesta1,respuesta2,respuesta3,respuesta4,respuesta5,respuesta6,respuesta7,respuesta8)
                        inicializar()
                    }
                }else{
                    setLocation(currentDateTimeString,latitud,longitud, respuesta1,respuesta2,respuesta3,"NO",respuesta5,respuesta6,respuesta7,respuesta8)
                    inicializar()
                }
            } else {
                // Hacer algo en caso de que no se haya seleccionado ninguna opción
                Toast.makeText(requireContext(), "Por favor selecione una respuesta para la pregunta 4", Toast.LENGTH_SHORT).show()
            }

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun enviarUbicacionEnTiempoReal(latitud: String,longitud: String){
        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("Estudiantes").child(currentUser!!.uid)
        val u = ubicacionTiempoReal(latitud, longitud)
        databaseReference.child("MisCoordenadas").setValue(u) { error, ref ->
            // showToast("Reporte Enviado.")
        }
    }

    fun setLocation(fecha: String, latitud:Double,longitud:Double,pregunta1: String, pregunta2: String, pregunta3: String, pregunta4: String,pregunta5: String, pregunta6: String,pregunta7: String,pregunta8:String) {
        if (latitud != 0.0 && longitud != 0.0) {
            try {
                setProgress.setProgressDialog(requireContext())
                val geocoder: Geocoder
                val direccion: List<Address>
                geocoder = Geocoder(requireContext(), Locale.getDefault())

                direccion =
                    geocoder.getFromLocation(latitud, longitud, 1)!! // 1 representa la cantidad de resultados a obtener
                //val address = direccion[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                //Toast.makeText(this, "Entroooo ${direccion[0].locality}", Toast.LENGTH_SHORT).show()
                if (direccion[0].locality == null || direccion[0].subLocality == null || direccion[0].thoroughfare == null){
                    //   Toast.makeText(this, "Entroooo ${direccion[0].postalCode}", Toast.LENGTH_SHORT).show()
                  val pais = direccion[0].countryName
                   val estado = direccion[0].adminArea
                    val ciudad = "Sin nombre"
                    val  colonia = "Sin nombre"
                    val  calle = "Sin Nombre"
                    val  cp = "NULL"
                    val num_ext = "Sin número"
                    println("mi direccion $pais $estado $ciudad  $colonia $calle $num_ext $cp")
                    MiDireccion = "$pais $estado $ciudad  $colonia $calle $num_ext $cp"
                    EnviarReporte(MiDireccion, fecha, latitud.toString(), longitud.toString(),pregunta1, pregunta2,pregunta3,pregunta4,pregunta5,pregunta6,pregunta7,pregunta8)
                }else{
                    val  ciudad = direccion[0].locality // ciudad
                    val  estado = direccion[0].adminArea //estado
                    val   pais = direccion[0].countryName // pais
                    val  cp = direccion[0].postalCode //codigo Postal
                    val  calle = direccion[0].thoroughfare // la calle
                    val  colonia =  direccion[0].subLocality// colonia
                    val  num_ext = direccion[0].subThoroughfare

                    println("mi direccion $pais $estado $ciudad  $colonia $calle $num_ext $cp")
                    MiDireccion = "$pais $estado $ciudad  $colonia $calle $num_ext $cp"
                    EnviarReporte(MiDireccion, fecha, latitud.toString(), longitud.toString(),pregunta1, pregunta2,pregunta3,pregunta4,pregunta5,pregunta6,pregunta7,pregunta8)
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
    override fun onPause() {
        super.onPause()
        if(currentUser != null){
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }
    private fun EnviarReporte(direccion:String,fecha: String, latitud:String,longitud:String,pregunta1: String, pregunta2: String, pregunta3: String, pregunta4: String,pregunta5: String, pregunta6: String,pregunta7: String,pregunta8:String){
        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("Estudiantes").child(currentUser!!.uid)
        val u = modeloPreguntasAPersonas(direccion, fecha, latitud, longitud, pregunta1,pregunta2,pregunta3,pregunta4,pregunta5,pregunta6,pregunta7,pregunta8)
        databaseReference.child("EncuestaPersonas").push().setValue(u) { error, ref ->
            // showToast("Reporte Enviado.")
            setProgress.dialog.dismiss()
        }
    }

    private fun inicializar(){
        editexPregunta1.setText(null)
        editexPregunta2.setText(null)
        editexPregunta3.setText(null)
        editexPregunta4.setText(null)
        editexPregunta5.setText(null)
        editexPregunta6.setText(null)
        editexPregunta7.setText(null)
        editexPregunta8.setText(null)
    }

}