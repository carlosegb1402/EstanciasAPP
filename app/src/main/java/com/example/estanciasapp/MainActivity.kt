package com.example.estanciasapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    //VARIABLES
    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnSalir: Button
    private lateinit var recordarCB: CheckBox
    private lateinit var loginPreferences: SharedPreferences
    private lateinit var loginPrefEditor: SharedPreferences.Editor
    private var saveLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //FN BOTON RETROCEDER
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitDialog()
            }
        })

        //FN SHARED PREFERENCES
        loginPreferences = getSharedPreferences("loginPref", MODE_PRIVATE)
        loginPrefEditor = loginPreferences.edit()
        saveLogin = loginPreferences.getBoolean("saveLogin", false)

        //INICIALIZAR COMPONENTES
        iniciarComponentes()
        eventsBTN()

        //FN RECORDAR USUARIO
        if (saveLogin) {
            etUsuario.setText(loginPreferences.getString("usuario", ""))
            etContrasena.setText(loginPreferences.getString("contrasena", ""))
            btnEntrar.isEnabled = true
            recordarCB.isChecked = true
        } else {
            limpiarInputs()
        }

        //VALIDAR CAMPOS
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnEntrar.isEnabled = etUsuario.text.isNotEmpty() && etContrasena.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        etUsuario.addTextChangedListener(textWatcher)
        etContrasena.addTextChangedListener(textWatcher)
    }

    //FN INICIALIZAR COMPONENTES
    private fun iniciarComponentes() {
        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etcontrasena)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnSalir = findViewById(R.id.btnSalir)
        recordarCB = findViewById(R.id.recordarCB)
    }

    //FN EVENTOS BOTONES
    private fun eventsBTN() {
        btnEntrar.setOnClickListener { fnAcceder() }
        btnSalir.setOnClickListener { exitDialog() }
    }

    //FN RECORDAR USUARIO
    private fun fnRecordar() {
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

    //FN ACCEDER
    private fun fnAcceder() {

        val loadingDialog = LoadingDialog(this)
        loadingDialog.startLoadingDialog()

        val baseUrl = getString(R.string.base_url)
        val url = "$baseUrl/verificarUsuario.php"

        val usuario = etUsuario.text.toString()
        val contrasena = etContrasena.text.toString()

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        fnRecordar()
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadingDialog.dismissDialog()
                            actQR()
                            showMSG("Verificación Exitosa")
                        }, 1000)
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadingDialog.dismissDialog()
                            showMSG("Credenciales Invalidas")
                        }, 800)
                    }
                } catch (e: JSONException) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.dismissDialog()
                        showMSG("Error En La Respuesta Del Servidor")
                    }, 800)
                }
            },
            Response.ErrorListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    loadingDialog.dismissDialog()
                    showMSG("Hubo Un Error En La Conexion")
                }, 800)
            }) {

            override fun getParams(): Map<String, String> {
                return hashMapOf("usuario" to usuario, "contrasena" to contrasena)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    //FN INTENT QR
    private fun actQR() {
        startActivity(Intent(this, QR::class.java))
        finish()
    }

    //FN LIMPIART EDIT TEXT
    private fun limpiarInputs() {
        etUsuario.text.clear()
        etContrasena.text.clear()
    }

    //FN MOSTRAR TOAST
    private fun showMSG(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    //FN DIALOG EXIT
    private fun exitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Aplicación")
            .setMessage("¿Estás seguro que deseas salir?")
            .setCancelable(true)
            .setPositiveButton("Sí") { _, _ -> finishAffinity() }
            .setNegativeButton("No", null)
            .show()
    }


}
