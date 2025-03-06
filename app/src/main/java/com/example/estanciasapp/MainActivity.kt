package com.example.estanciasapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnSalir: Button
    private lateinit var recordarCB: CheckBox
    private lateinit var showPass: ImageButton
    private lateinit var loginPreferences: SharedPreferences
    private var pushed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loginPreferences = getSharedPreferences("loginPref", MODE_PRIVATE)

        iniciarComponentes()
        cargarSesionGuardada()
        configurarListeners()
    }

    private fun iniciarComponentes() {
        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etcontrasena)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnSalir = findViewById(R.id.btnSalir)
        recordarCB = findViewById(R.id.recordarCB)
        showPass = findViewById(R.id.showPass)
    }

    private fun configurarListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnEntrar.isEnabled = etUsuario.text.isNotEmpty() && etContrasena.text.isNotEmpty()
                showPass.isVisible = etContrasena.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        etUsuario.addTextChangedListener(textWatcher)
        etContrasena.addTextChangedListener(textWatcher)

        btnEntrar.setOnClickListener {
            guardarSesion()
            verificarCredenciales()
        }

        btnSalir.setOnClickListener { exitDialog() }
        showPass.setOnClickListener { toggleMostrarContrasena() }

        etContrasena.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !pushed && etContrasena.text.toString() == loginPreferences.getString("contrasena", "")) {
                pushed = true
                etContrasena.text.clear()
            }
        }

        // Configurar retroceso con confirmación
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { exitDialog() }
        })

    }

    private fun toggleMostrarContrasena() {
        val method = if (etContrasena.transformationMethod == android.text.method.PasswordTransformationMethod.getInstance()) {
            android.text.method.HideReturnsTransformationMethod.getInstance()
        } else {
            android.text.method.PasswordTransformationMethod.getInstance()
        }
        etContrasena.transformationMethod = method
        showPass.setImageResource(if (method == android.text.method.HideReturnsTransformationMethod.getInstance()) R.drawable.show_pass else R.drawable.show_pass_0)
        etContrasena.setSelection(etContrasena.text.length)
    }

    private fun cargarSesionGuardada() {
        val saveLogin = loginPreferences.getBoolean("saveLogin", false)
        if (saveLogin) {
            etUsuario.setText(loginPreferences.getString("usuario", ""))
            etContrasena.setText(loginPreferences.getString("contrasena", ""))
            btnEntrar.isEnabled = true
            recordarCB.isChecked = true
        } else {
            limpiarInputs()
        }
    }

    private fun guardarSesion() {
        val editor = loginPreferences.edit()
        if (recordarCB.isChecked) {
            editor.putBoolean("saveLogin", true)
            editor.putString("usuario", etUsuario.text.toString())
            editor.putString("contrasena", etContrasena.text.toString())
        } else {
            editor.clear()
        }
        editor.apply()
    }

    private fun verificarCredenciales() {

        val loadingDialog = LoadingDialog(this)
        loadingDialog.startLoadingDialog()

        val url = getString(R.string.base_url) + "/verificarUsuario.php"
        val usuario = etUsuario.text.toString()
        val contrasena = etContrasena.text.toString()

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.dismissDialog()
                        if (jsonResponse.getString("status") == "success") {
                            iniciarActividadQR()
                            showMSG("Verificación Exitosa")
                        } else {
                            showMSG("Credenciales Inválidas")
                        }
                    }, 1000)
                } catch (e: JSONException) {
                    mostrarError(loadingDialog, "Error en la respuesta del servidor")
                    Log.e("Error Server", "${e.message}")
                }
            },
            Response.ErrorListener {
                mostrarError(loadingDialog, "Hubo un error en la conexión")
                Log.e("Error Server", "${it.message}")
            }) {
            override fun getParams(): Map<String, String> = hashMapOf(
                "usuario" to usuario,
                "contrasena" to contrasena
            )
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarError(dialog: LoadingDialog, mensaje: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismissDialog()
            showMSG(mensaje)
        }, 800)
    }

    private fun iniciarActividadQR() {
        startActivity(Intent(this, QR::class.java))
        finish()
    }

    private fun limpiarInputs() {
        etUsuario.text.clear()
        etContrasena.text.clear()
    }

    private fun showMSG(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

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
