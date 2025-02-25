package com.example.estanciasapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.estanciasapp.DB.Fallas
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
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var db: DbHandler

    //Variable Datos
    private lateinit var idEquipo: String
    private lateinit var nombreEquipo: String
    private lateinit var numeroEquipo: String
    private lateinit var areaEquipo: String
    private lateinit var modeloEquipo: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        loadingDialog = LoadingDialog(this)
        db= DbHandler(this)
        db.openDatabase()
        FnClass().syncPendingFallas(this)

        initComponents()
        obtenerInformacionEquipo()
        setButtonListeners()

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
                AlertDialog.Builder(this)
                    .setMessage("¿Seguro que desea realizar el registro?")
                    .setCancelable(false)
                    .setTitle("Confirmación")
                    .setPositiveButton("Sí") { _, _ -> fnRegistrar() }
                    .setNegativeButton("No", null)
                    .show()
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
        val fallas = Fallas(
            eqpfal = idEquipo.toInt(),
            fecfal = obtenerFechaActual(),
            estfal = estadoSP.getSelectedItem().toString().toInt(),
            obsfal = observacionesET.text.toString()
        )

        sendToServer(fallas)

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


    //FN ACTIVIDAD QR
    private fun actQR() {
        startActivity(Intent(this, QR::class.java))
        finish()
    }


    //FN INSERTAR WS
   private fun sendToServer(falla: Fallas) {

        limpiar(); loadingDialog.startLoadingDialog()

        val baseUrl = ContextCompat.getString(this,R.string.base_url)
        val url = "$baseUrl/registrarFalla.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("success")){

                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.dismissDialog()
                        FnClass().showToast(this, "Registro Exitoso")
                        actQR()
                    }, 1000)

                }
            },
            Response.ErrorListener { _ ->

                Handler(Looper.getMainLooper()).postDelayed({
                    loadingDialog.dismissDialog()
                    db.insertDATA(falla)
                    actQR()
                }, 1000)

            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "eqpfal" to falla.eqpfal.toString(),
                    "fecfal" to falla.fecfal,
                    "estfal" to falla.estfal.toString(),
                    "obsfal" to falla.obsfal
                )
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(this).add(stringRequest)
    }

}
