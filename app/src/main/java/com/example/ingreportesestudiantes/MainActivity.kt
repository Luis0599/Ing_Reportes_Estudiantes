package com.example.ingreportesestudiantes

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ingreportesestudiantes.conexionInternet.conexionInternet
import com.example.ingreportesestudiantes.databinding.ActivityMainBinding
import com.example.ingreportesestudiantes.interfaces.registrarEstudiante
import com.example.ingreportesestudiantes.modelos.usuarios
import com.example.ingreportesestudiantes.ui.gallery.GalleryFragment
import com.example.ingreportesestudiantes.ui.home.HomeFragment
import com.example.ingreportesestudiantes.ui.slideshow.SlideshowFragment
import com.example.kerklyv5.url.Url
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.Arrays
import java.util.Date


class MainActivity : AppCompatActivity() {
    //Autenticacion para saber la hora activo
    var providers: MutableList<AuthUI.IdpConfig?>? = null
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private val MY_REQUEST_CODE = 200

    private lateinit var dialogPedirMatricula: Dialog

    private lateinit var layoutNombre: TextInputLayout
    private lateinit var editNombre: TextInputEditText
    private  lateinit var layoutP: TextInputLayout
    private lateinit var editP: TextInputEditText
    private lateinit  var layoutM: TextInputLayout
    private  lateinit var editM: TextInputEditText
    private   lateinit var layoutMatricula: TextInputLayout
    private   lateinit var editextMatricula: TextInputEditText
    private lateinit var botonGuardar: Button

    private lateinit var txt_nombre: TextView
    private lateinit var ImageViewFoto: CircleImageView
    private lateinit var txt_correo: TextView
    private lateinit var txtMatricula: TextView

    val setProgress= setProgressDialog()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val view = navView.getHeaderView(0)
        txt_nombre = view.findViewById(R.id.txtNombre)
        txt_correo = view.findViewById(R.id.txtCorreo)
        txtMatricula = view.findViewById(R.id.txtMatricula)
        ImageViewFoto = view.findViewById(R.id.imageViewFoto)
        //Firebase
        providers = Arrays.asList(
          //  AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        mAuth = FirebaseAuth.getInstance()
        dialogPedirMatricula = Dialog(this)

        navView.itemIconTintList = null

      /*  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> metodoHome()
                R.id.nav_gallery -> metodoGallery()
                R.id.nav_slideshow -> metodoSlideshow()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }*/
    }

    private fun metodoSlideshow() {
        val f = SlideshowFragment()
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_main,f).commit()
        }
    }

    private fun metodoGallery() {
        val f = GalleryFragment()
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_main,f).commit()
        }
    }

    private fun metodoHome() {
        val f = HomeFragment()
        // Toast.makeText(this,"Nombre ${nombre.toString()}", Toast.LENGTH_LONG).show()
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_main,f).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection based on the item ID
        when (item.itemId) {
            R.id.action_settings -> {
                // Código para manejar la selección del ítem 1
               metodoSalir()
                return true
            }
            // Agrega más casos para otros ítems del menú si es necesario
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val conexion = conexionInternet(this)
        if (conexion.isInternetConnected()) {
            if (requestCode == MY_REQUEST_CODE) {
                currentUser = mAuth!!.currentUser
                txt_nombre.text = currentUser!!.displayName.toString()
                txt_correo.text = currentUser!!.email.toString()
                cargarImagen(currentUser!!.photoUrl.toString())
                pedirMatricula(currentUser!!.email.toString(), currentUser!!.photoUrl.toString())
                Toast.makeText(this@MainActivity, "Bienvenido ${currentUser!!.email}", Toast.LENGTH_SHORT) .show()
            }
        }else{
            Toast.makeText(this@MainActivity, "No tienes Internet", Toast.LENGTH_SHORT) .show()
        }
    }

    override fun onStart() {
        super.onStart()
        currentUser = mAuth!!.currentUser
        if(currentUser != null){
            txt_nombre.text = currentUser!!.displayName.toString()
            txt_correo.text = currentUser!!.email.toString()
            cargarImagen(currentUser!!.photoUrl.toString())
             //Toast.makeText(this@MainActivity, "Bienvenido 2 ${currentUser!!.email}", Toast.LENGTH_SHORT) .show()
            //pedirMatricula("","")
            val database = FirebaseDatabase.getInstance()
            val databaseReference = database.getReference("Estudiantes").child(currentUser!!.uid.toString())
            val updates = HashMap<String, Any>()
            updates["fechaHora"] = DateFormat.getDateTimeInstance().format(Date())
            databaseReference.child("informacionEstudiante").updateChildren(updates) { error, ref ->
                if (error != null) {
                    // Manejar el error en caso de que ocurra
                   // Toast.makeText(this@MainActivity, "${error.message}", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this@MainActivity, "Fecha actualizada correctamente", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@MainActivity, "Bienvenido ${currentUser!!.displayName}", Toast.LENGTH_SHORT).show()
                }
                }
        }else{
            muestraOpciones()
        }
    }

    private fun cargarImagen(urlImagen: String) {
        // Cargando la imagen en la ImageView
        Picasso.get().load(urlImagen).into(ImageViewFoto)
    }

    fun getRoundedBitmap(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = -0x1
        canvas.drawCircle(width / 2f, height / 2f, (width / 2f).coerceAtMost(height / 2f), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
        return outputBitmap
    }


    fun muestraOpciones() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers!!)
                .build(),MY_REQUEST_CODE
        )
    }

    fun metodoSalir() {
        val conexionInternet = conexionInternet(this)
        if (conexionInternet.isInternetConnected()){
            AuthUI.getInstance()
                .signOut(applicationContext)
                .addOnCompleteListener {// muestraOpciones()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        applicationContext, ""
                                + e.message, Toast.LENGTH_LONG
                    ).show()
                }
            finish()
        }else{
            Toast.makeText(this@MainActivity, "No tienes Internet", Toast.LENGTH_SHORT) .show()
        }


    }
    fun pedirMatricula(correo: String,foto: String){
        dialogPedirMatricula.setContentView(R.layout.pedir_matricula)
        dialogPedirMatricula.setCanceledOnTouchOutside(false) // Evitar el cierre al hacer clic fuera del diálogo
        layoutNombre = dialogPedirMatricula.findViewById<TextInputLayout>(R.id.layoutNombre_Registrado)
        editNombre = dialogPedirMatricula.findViewById<TextInputEditText>(R.id.edit_Nombre_Registrado)
        layoutP = dialogPedirMatricula.findViewById<TextInputLayout>(R.id.layoutApellidoP)
        editP = dialogPedirMatricula.findViewById<TextInputEditText>(R.id.edit_ApellidoP)
        layoutM = dialogPedirMatricula.findViewById<TextInputLayout>(R.id.layoutApellidoM)
        editM = dialogPedirMatricula.findViewById<TextInputEditText>(R.id.edit_ApellidoM)
        layoutMatricula = dialogPedirMatricula.findViewById<TextInputLayout>(R.id.layoutMatricula)
        editextMatricula = dialogPedirMatricula.findViewById<TextInputEditText>(R.id.edit_Matricula)
        botonGuardar = dialogPedirMatricula.findViewById(R.id.botonGuardar)

        botonGuardar.setOnClickListener{
            val nombre = editNombre.text.toString()
            val apellidoP = editP.text.toString()
            val apellidoM = editM.text.toString()
            val matricula =  editextMatricula.text.toString()
            if(nombre.isEmpty()) {
                layoutNombre.error = "Campo Requerido"
            } else {
                layoutNombre.error = null
            }
            if (apellidoP.isEmpty()){
                layoutP.error = "Campo Requerido"
            }else{
                layoutP.error = null
            }
            if (apellidoM.isEmpty()){
                layoutM.error = "Campo Requerido"
            }else{
                layoutM.error = null
            }
            if (matricula.isEmpty()){
                layoutMatricula.error = "Campo Requerido"
            }else{
                layoutMatricula.error = null
            }
            if (!nombre.isEmpty() && !apellidoP.isEmpty() && !apellidoM.isEmpty() && !matricula.isEmpty()){
                //Toast.makeText(this,"todo bien",Toast.LENGTH_SHORT).show()
                val database = FirebaseDatabase.getInstance()
                val databaseReference = database.getReference("Estudiantes").child(currentUser!!.uid)
                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                val u = usuarios(matricula, nombre, apellidoP, apellidoM, foto,currentDateTimeString, correo)
                databaseReference.child("informacionEstudiante").setValue(u) { error, ref ->
                    // Toast.makeText(this@MainActivity, "Bienvenido $nombre $apellidoP", Toast.LENGTH_SHORT) .show()
                    println("-------> "+nombre)
                    ingresarEstudiante(correo,nombre,apellidoP,apellidoM,matricula)
                }
            }
        }
        dialogPedirMatricula.show()
    }

    private fun ingresarEstudiante(correo: String, nombre: String, apellidoP: String, apellidoM: String, matricula: String) {
       setProgress.setProgressDialog(this)
        val Url = Url().url
        val adapter = RestAdapter.Builder()
            .setEndpoint(Url)
            .build()
        val api: registrarEstudiante = adapter.create(registrarEstudiante::class.java)
        api.registraEstudiante(correo,nombre,apellidoP,apellidoM,matricula,
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
                        Toast.makeText(this@MainActivity,Respuesta,Toast.LENGTH_SHORT).show()
                        setProgress.dialog.dismiss()
                        dialogPedirMatricula.dismiss()
                    }else{
                        Toast.makeText(this@MainActivity, "$Respuesta", Toast.LENGTH_SHORT).show()
                        setProgress.dialog.dismiss()
                        dialogPedirMatricula.dismiss()
                    }

                }
                override fun failure(error: RetrofitError) {
                    Toast.makeText(this@MainActivity, "error $error" , Toast.LENGTH_SHORT).show()
                    setProgress.dialog.dismiss()
                }

            }
        )
    }

 /*   override fun onDataPass(data: String) {
        val fragmentB = SlideshowFragment()
        val args = Bundle()
        args.putString("data_key", data)
        fragmentB.arguments = args

        // Obtén el NavController desde el NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Navegar al FragmentB utilizando el NavController y el ID del Fragment de destino
        navController.navigate(R.id.nav_slideshow, args)
    }*/


}