package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.estanciasapp.DB.DbHandler
import com.example.estanciasapp.DB.Fallas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Formulario : AppCompatActivity() {

    // Componentes UI
    private lateinit var nombreET: EditText
    private lateinit var numeroET: EditText
    private lateinit var modeloET: EditText
    private lateinit var areaET: EditText
    private lateinit var estadoSP: Spinner
    private lateinit var observacionesET: EditText
    private lateinit var limpiarBTN: ImageButton
    private lateinit var cancelarBTN: Button
    private lateinit var registrarBTN: Button

    //Variable Datos
    private lateinit var idEquipo: String
    private lateinit var nombreEquipo: String
    private lateinit var numeroEquipo: String
    private lateinit var areaEquipo: String
    private lateinit var modeloEquipo: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        initComponents()
        obtenerInformacionEquipo()
        setButtonListeners()

    }

    fun checkConnection(callback: (Boolean) -> Unit) {
        val url = "https://www.google.com"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w("CheckConnection", "SIN CONEXIÓN")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val isConnected = response.isSuccessful
                Log.w("CheckConnection", "CONEXIÓN EXITOSA")
                callback(isConnected)
            }
        })
    }



    // Obtener información del intent
    private fun obtenerInformacionEquipo(){

        val values = intent.getStringExtra("informacion")?.split(",") ?: listOf()

        if (values.size == 6) {
            idEquipo = values[0]
            nombreEquipo = values[1]
            numeroEquipo = values[2]
            areaEquipo = values[3]
            modeloEquipo = values[4]

            nombreET.setText(nombreEquipo)
            numeroET.setText(numeroEquipo)
            areaET.setText(areaEquipo)
            modeloET.setText(modeloEquipo)

        } else {
            actQR()
        }

    }

    private fun initComponents() {
        nombreET = findViewById(R.id.nombreET)
        numeroET = findViewById(R.id.numeroET)
        modeloET = findViewById(R.id.modeloET)
        areaET = findViewById(R.id.areaET)
        estadoSP = findViewById(R.id.estFalloSP)
        observacionesET = findViewById(R.id.observacionesET)
        limpiarBTN = findViewById(R.id.btnLimpiar)
        cancelarBTN = findViewById(R.id.cancelarBTN)
        registrarBTN = findViewById(R.id.registrarBTN)
    }

    private fun limpiar(){
        observacionesET.setText("")
        estadoSP.setSelection(0)
    }

    //BOTONES FUNCIONES
    private fun setButtonListeners() {

        //BTN LIMPIAR
        limpiarBTN.setOnClickListener {
            if (observacionesET.text.isNotEmpty()) limpiar()
            else FnClass().showToast(this,"No hay información para borrar")
        }

        //BTN CANCELAR
        cancelarBTN.setOnClickListener {
            showExitDialog()
        }

        //BTN REGISTRAR
        registrarBTN.setOnClickListener {
            if (observacionesET.text.isEmpty()) FnClass().showToast(this,"Hay Campos Vacíos")

            else {
                AlertDialog.Builder(this)
                    .setMessage("¿Seguro que desea realizar el registro?")
                    .setCancelable(false)
                    .setTitle("Confirmación")
                    .setPositiveButton("Sí") { _, _ -> fnRegistrar() }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    //FN OBTENER FECHA
    private fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = Calendar.getInstance().time
        return formatoFecha.format(fechaActual)
    }

    //FN REGISTRAR
    private fun fnRegistrar() {
        val db = DbHandler(this)
        val listaPending = db.getPendingFallas()

        val fallas = Fallas(
            eqpfal = idEquipo.toInt(),
            fecfal = obtenerFechaActual(),
            estfal = estadoSP.getSelectedItem().toString().toInt(),
            obsfal = observacionesET.text.toString()
        )


        checkConnection { isConnected ->
            if (isConnected) {
                FnClass().showToast(this, "Hay conexión a internet.")
            } else {
                FnClass().showToast(this, "No hay conexión a internet.")
            }
        }


        /*if (listaPending.isNotEmpty()) {
                    FnClass().syncPendingFallas(this@Formulario) {
                        FnClass().sendToServer(this@Formulario, fallas)
                        limpiar()
                        actQR()
                    }
                }

              else if(isInternetAvailable(this@Formulario)){
                    FnClass().sendToServer(this@Formulario, fallas)
                    limpiar()
                    actQR()
                }else{
                    limpiar()
                    db.insertDATA(fallas)
                    actQR()
                }

        }*/

    }

    //FN MOSTRAR DIALOG
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
            .setCancelable(false)
            .setTitle("Aplicación")
            .setPositiveButton("Sí") { _, _ -> actQR() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun actQR() {
        startActivity(Intent(this, QR::class.java))
        finish()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showExitDialog()
    }
}
