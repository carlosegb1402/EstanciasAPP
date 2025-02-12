package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.estanciasapp.DB.DbHandler
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    //declaracion de los objetos
    private lateinit var etUsuario : EditText
    private lateinit var etContrasena : EditText
    private lateinit var btnEntrar : Button
    private lateinit var btnSalir: Button
    private lateinit var recordarCB:CheckBox
    private lateinit var loginPreferences: SharedPreferences
    private lateinit var loginPrefEditor: Editor
    private var saveLogin by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val dbHandler = DbHandler(this)
        val db = dbHandler.openDatabase()

        dbHandler.checkAndCreateTable(db)

        loginPreferences=getSharedPreferences("loginPref", MODE_PRIVATE)
        loginPrefEditor=loginPreferences.edit()
        saveLogin=loginPreferences.getBoolean("saveLogin",false)

        //llamado de las funciones
        iniciarComponentes()
        eventsBTN()

        if (saveLogin){
            etUsuario.setText(loginPreferences.getString("usuario",""))
            etContrasena.setText(loginPreferences.getString("contrasena",""))
            recordarCB.isChecked=true
        }else{
            limpiarInputs()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }


    }

    //conexion de los widgets del layout
    private fun iniciarComponentes(){
        etUsuario=findViewById(R.id.etUsuario)
        etContrasena=findViewById(R.id.etcontrasena)
        btnEntrar=findViewById(R.id.btnEntrar)
        btnSalir=findViewById(R.id.btnSalir)
        recordarCB=findViewById(R.id.recordarCB)
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

    private fun fnRecordar(){
        if (recordarCB.isChecked){
           loginPrefEditor.putBoolean("saveLogin",true)
           loginPrefEditor.putString("usuario",etUsuario.text.toString())
           loginPrefEditor.putString("contrasena",etUsuario.text.toString())
           loginPrefEditor.commit()
        }
        else{
         limpiarInputs()
         loginPrefEditor.clear()
         loginPrefEditor.commit()
        }
    }

    //funcion para acceso
    private fun fnAcceder(){

        val usuario ="12345"
        val contrasena ="12345"


        if (etUsuario.text.toString().isEmpty() && etContrasena.text.toString().isEmpty()){
            showMSG("Ingrese los datos requeridos para acceder")
        }


        else if (etContrasena.text.toString().isEmpty()){
            showMSG("Ingrese La Contraseña")
        }

        else if (etUsuario.text.toString().isEmpty()){
            showMSG("Ingrese El Usuario")
        }

        else if(etUsuario.text.toString()==usuario && etContrasena.text.toString()==contrasena){
                fnRecordar()
                usuario.qrActivity()
        }

        else{
            showMSG("Datos Incorrectos")
        }
    }

    //fn ir activity qr
    private fun String.qrActivity() {
        val menuPrinIntent=Intent(this@MainActivity,QR::class.java)
        menuPrinIntent.putExtra("usuario", this)
        startActivity(menuPrinIntent)
        finish()
    }

    //fn limpiar inputs
    private fun limpiarInputs(){
        etUsuario.setText("")
        etContrasena.setText("")
    }

    //fn mostrar msg errores
    private fun showMSG(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
            }

    //fn mostrar msg salir aplicacion
    private  fun alertExit(){
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
            .setCancelable(false).setTitle("Aplicacion")
            .setPositiveButton("Sí") { _, _ -> finish()}
            .setNegativeButton("No", null)
            .show()
    }

    ///programacion boton atras de android
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        alertExit()
    }

}