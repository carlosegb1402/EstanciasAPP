package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipal : AppCompatActivity() {

    //declaracion de los objetos
    private lateinit var opcion1BTN : TextView
    private lateinit var opcion2BTN : TextView
    private lateinit var opcion3BTN : TextView
    private lateinit var opcion4BTN : TextView
    private lateinit var usuarioTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        //llamado de la funciones
        iniciarComponentes()
        eventsBTN()

        //mostrar nombre del usuario
        usuarioTV.setText("Usuario: "+intent.getStringExtra("usuario").toString())


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    //conexion de los widgets del layout
    private fun iniciarComponentes(){
        opcion1BTN=findViewById(R.id.opcion1BTN)
        opcion2BTN=findViewById(R.id.opcion2BTN)
        opcion3BTN=findViewById(R.id.opcion3BTN)
        opcion4BTN=findViewById(R.id.opcion4BTN)
        usuarioTV=findViewById(R.id.usuarioTV)
    }

    //programacion de los botones
    private fun eventsBTN(){

        //programacion del boton 1
        opcion1BTN.setOnClickListener(View.OnClickListener {
            intentQR("1")
        })

        //programacion del boton 2
        opcion2BTN.setOnClickListener(View.OnClickListener {
            intentQR("2")
        })

        //programacion del boton 3
        opcion3BTN.setOnClickListener(View.OnClickListener {
            intentQR("3")
        })

        //programacion del boton 4
        opcion4BTN.setOnClickListener(View.OnClickListener {
            intentQR("4")
        })

    }

    //funcion intent
    private fun intentQR(opcion: String){
        val intent=Intent(this,QR::class.java)
        intent.putExtra("opcion",opcion)
        startActivity(intent)
    }


    private  fun alertExit(){
        AlertDialog.Builder(this)
            .setMessage("¿Desea volver al menu principal?")
            .setCancelable(false).setTitle("Aplicacion")
            .setPositiveButton("Sí") { _, _ -> finish()}
            .setNegativeButton("No", null)
            .show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        alertExit()
    }

}