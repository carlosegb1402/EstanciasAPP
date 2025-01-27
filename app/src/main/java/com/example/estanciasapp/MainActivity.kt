package com.example.estanciasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //declaracion de los objetos
    private lateinit var txtUsuario : TextView
    private lateinit var txtContrasena : TextView
    private lateinit var btnEntrar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //llamado de las funciones
        iniciarComponentes()
        eventEntrar()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    //funcion para iniciar los componentes
    fun iniciarComponentes(){
        txtUsuario=findViewById(R.id.etUsuario)
        txtContrasena=findViewById(R.id.etcontrasena)
        btnEntrar=findViewById(R.id.btnEntrar)
    }

    //funcion para ingresar
    fun eventEntrar(){
        var usuario ="12345"
        var contrasena ="12345"

        //progrmacion del boton entrar
        btnEntrar.setOnClickListener(View.OnClickListener {

            if (txtUsuario.text.toString().contentEquals("") || txtContrasena.text.toString().contentEquals("")){
                Toast.makeText(applicationContext,"Ingrese todos los datos requeridos para acceder",Toast.LENGTH_SHORT)
            }

            else if(txtUsuario.text.toString().contentEquals(usuario) && txtContrasena.text.toString().contentEquals(contrasena)){
                Toast.makeText(applicationContext,"Accedio Correctamente",Toast.LENGTH_SHORT)
                val menuPrin=Intent(this,menuPrincipal::class.java)
                startActivity(menuPrin)
                finish()
            }

            else{
                Toast.makeText(applicationContext,"Los datos para acceder son incorrectos",Toast.LENGTH_SHORT)
            }

        })
    }
}