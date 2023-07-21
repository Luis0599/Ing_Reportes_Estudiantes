package com.example.ingreportesestudiantes.ui.gallery

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingreportesestudiantes.adaptadores.adapterReportes
import com.example.ingreportesestudiantes.databinding.FragmentGalleryBinding
import com.example.ingreportesestudiantes.modelos.Reporte
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    lateinit var recyclerView: RecyclerView
    lateinit var  txtp: TextView
    private lateinit var arrayListdatos: ArrayList<Reporte>
    private lateinit var Miadapter: adapterReportes
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.recyclerReportes

        arrayListdatos = ArrayList()
        Miadapter = adapterReportes(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = Miadapter

        Miadapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollBar()
            }
        })
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        var firebaseDatabaseLista: FirebaseDatabase
        var databaseReference: DatabaseReference
        firebaseDatabaseLista = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabaseLista.getReference("Estudiantes").child(currentUser!!.uid).child("MisReportes")
        //  val  firebaseDatabaseusu = firebaseDatabaseLista.getReference("UsuariosR").child("7471503418")
        // .child("Lista de Usuarios")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val u2 = snapshot.getValue(Reporte::class.java)
                if (u2 ==null){
                    Toast.makeText(requireContext(), "No Tienes Ningun Reporte", Toast.LENGTH_SHORT).show()
                }else{
                    println("id "+snapshot.key.toString())
                    println("todo "+snapshot.value)
                    Miadapter.agregarUsuario(u2!!)
                }

                val mGestureDetector = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            return true
                        }
                    })
                } else {
                    TODO("VERSION.SDK_INT < CUPCAKE")
                }
                recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {}
                    override fun onInterceptTouchEvent(
                        recyclerView: RecyclerView, motionEvent: MotionEvent
                    ): Boolean {
                        try {
                            val child = recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
                            if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                                val position = recyclerView.getChildAdapterPosition(child)
                                val id = Miadapter.lista[position].oficio
                               // Toast.makeText(requireContext(), "$id", Toast.LENGTH_SHORT).show()

                                return true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return false
                    }
                    override fun onTouchEvent(
                        recyclerView: RecyclerView,
                        motionEvent: MotionEvent
                    ) {
                    }
                })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //   Miadapter.lista.clear()
                Miadapter.notifyDataSetChanged();
                //mostrarUsuarios(snapshot)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return root
    }

    private fun setScrollBar() {
        recyclerView.scrollToPosition(Miadapter.itemCount-1)
        // println("entro 217 "+ {Miadapter.itemCount-1 })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}