package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    //declaracion de los objetos
    private lateinit var txtUsuario : TextView
    private lateinit var txtContrasena : TextView
    private lateinit var btnEntrar : Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //llamado de las funciones
        iniciarComponentes()
        eventsBTN()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    //conexion de los widgets del layout
    private fun iniciarComponentes(){
        txtUsuario=findViewById(R.id.etUsuario)
        txtContrasena=findViewById(R.id.etcontrasena)
        btnEntrar=findViewById(R.id.btnEntrar)
        btnSalir=findViewById(R.id.btnSalir)
    }

    //funcion para ingresar
    private fun eventsBTN(){

        //programacion del boton entrar
        btnEntrar.setOnClickListener{
             fnAcceder()

        }

        //programacion del boton salir
        btnSalir.setOnClickListener{
            alertExit()
            }

    }

    //funcion para acceso
    private fun fnAcceder(){

        val usuario ="12345"
        val contrasena ="12345"

        if (txtUsuario.text.toString().isEmpty() && txtContrasena.text.toString().isEmpty()){
            showMSG("Ingrese los datos requeridos para acceder")
        }


        else if (txtContrasena.text.toString().isEmpty()){
            showMSG("Ingrese La Contraseña")
        }

        else if (txtUsuario.text.toString().isEmpty()){
            showMSG("Ingrese El Usuario")
        }

        else if(txtUsuario.text.toString()==usuario && txtContrasena.text.toString()==contrasena){
            val menuPrinIntent=Intent(this,MenuPrincipal::class.java)
            menuPrinIntent.putExtra("usuario",usuario)
            limpiarInputs()
            startActivity(menuPrinIntent)
        }

        else{
            showMSG("Datos Incorrectos")
        }
    }

    private fun limpiarInputs(){
        txtUsuario.setText("")
        txtContrasena.setText("")
    }

    private fun showMSG(msg:String){
            Snackbar.make(findViewById(android.R.id.content)
            ,msg
            ,Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.WHITE)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
            .setTextColor(ContextCompat.getColor(applicationContext,R.color.principal)).setDuration(800).show()
    }
    private  fun alertExit(){
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
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