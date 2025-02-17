package com.example.estanciasapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    //Declaracion UI
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitDialog()
            }
        })

//      val dbHandler = DbHandler(this)
//      val db = dbHandler.openDatabase()
//      dbHandler.checkAndCreateTable(db)

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

    }

    //UI Componentes
    private fun iniciarComponentes() {
        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etcontrasena)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnSalir = findViewById(R.id.btnSalir)
        recordarCB = findViewById(R.id.recordarCB)
    }

    //FN Botones
    private fun eventsBTN(){
        ///BTN ENTRAR
        btnEntrar.setOnClickListener{
            FnClass().checkConnection { isConnected ->
                runOnUiThread {
                    if (isConnected) {
                        fnAcceder()
                    } else {
                        FnClass().showToast(this, "No hay conexión a internet.")
                    }
                }
            }

        }
        //BTN SALIR
        btnSalir.setOnClickListener{ exitDialog() }
    }

    //FN Recordar Usuario
    private fun fnRecordar(){
        with(loginPrefEditor) {
            if (recordarCB.isChecked) {
                putBoolean("saveLogin", true)
                putString("usuario", etUsuario.text.toString())
                putString("contrasena", etContrasena.text.toString())
            } else {
                remove("usuario")
                remove("contrasena")
                putBoolean("saveLogin", false)
            }
            apply()
        }
    }


    //FN Acceder
    private fun fnAcceder() {
        val usuario = etUsuario.text.toString()
        val contrasena = etContrasena.text.toString()

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            showMSG("Ingrese Todos Los Datos")
            return
        }
        val url = "http://172.16.13.213/wServices/verificarUsuario.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        fnRecordar()
                        showMSG(jsonResponse.getString("message"))
                        actQR()
                    } else {
                        showMSG(jsonResponse.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showMSG("Error en la respuesta del servidor")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                showMSG("Error en la conexión con el servidor")
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = usuario
                params["contrasena"] = contrasena
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }


    //FN Intent QR
    private fun actQR() {
//      menuPrinIntent.putExtra("usuario", this)
        startActivity(Intent(this@MainActivity,QR::class.java))
        finish()
    }

    //FN Limpiar Inputs
    private fun limpiarInputs(){
        etUsuario.setText("")
        etContrasena.setText("")
    }

    //FN Show Toast
    private fun showMSG(msg:String){Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()}

    //FN Show Dialog
    private fun exitDialog(){
        AlertDialog.Builder(this)
            .setTitle("Aplicación")
            .setMessage("¿Estás seguro que deseas salir?")
            .setCancelable(true)
            .setPositiveButton("Sí") { _, _ -> finishAffinity() }
            .setNegativeButton("No", null)
            .show()
    }


}