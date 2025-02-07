package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.estanciasapp.DB.DbHandler
import com.example.estanciasapp.DB.Fallas

class Formulario : AppCompatActivity() {

    // Componentes UI
    private lateinit var nombreET: EditText
    private lateinit var numeroET: EditText
    private lateinit var modeloET: EditText
    private lateinit var areaET: EditText
    private lateinit var estadoET: EditText
    private lateinit var observacionesET: EditText
    private lateinit var limpiarBTN: ImageButton
    private lateinit var cancelarBTN: Button
    private lateinit var registrarBTN: Button

    // Datos
    private lateinit var id: String
    private lateinit var nombre: String
    private lateinit var numero: String
    private lateinit var area: String
    private lateinit var modelo: String
    private lateinit var laboratorio: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        // Inicializar componentes
        initComponents()

        // Obtener información del intent
        val values = intent.getStringExtra("informacion")?.split(",") ?: listOf()

        if (values.size == 6) {
            id = values[0]
            nombre = values[1]
            numero = values[2]
            area = values[3]
            modelo = values[4]
            laboratorio = values[5]

            nombreET.setText(nombre)
            numeroET.setText(numero)
            areaET.setText(area)
            modeloET.setText(modelo)

        } else {
            actQR()
        }

        // Función para manejar botones
        setButtonListeners()
    }

    private fun initComponents() {
        nombreET = findViewById(R.id.nombreET)
        numeroET = findViewById(R.id.numeroET)
        modeloET = findViewById(R.id.modeloET)
        areaET = findViewById(R.id.areaET)
        estadoET = findViewById(R.id.estFalloET)
        observacionesET = findViewById(R.id.observacionesET)
        limpiarBTN = findViewById(R.id.btnLimpiar)
        cancelarBTN = findViewById(R.id.cancelarBTN)
        registrarBTN = findViewById(R.id.registrarBTN)
    }

    private fun setButtonListeners() {
        limpiarBTN.setOnClickListener {
            if (observacionesET.text.isNotEmpty() || estadoET.text.isNotEmpty()) limpiar()
            else showToast("No hay información para borrar")
        }

        cancelarBTN.setOnClickListener {
            showExitDialog()
        }

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

    private fun fnRegistrar() {
        val estado = estadoET.text.toString().toIntOrNull()
        val observaciones = observacionesET.text.toString()

        if (estado == null || observaciones.isEmpty()) {
            showToast("Campos Vacíos o inválidos")
            return
        }

        val fallas = Fallas(eqpfal = id.toInt(), estfal = estado, obsfal = observaciones, labfal = laboratorio.toInt())
        val db = DbHandler(this)

        if (estado > 4) {
            showToast("Estado debe estar entre 1 y 4")
            return
        }

        // Verificar conexión a Internet
        if (FnClass().haveNetwork(this)) {
            sendToServer(fallas)
            limpiar()
        } else {
            db.insertDATA(fallas)
            limpiar()
        }
    }


    private fun sendToServer(falla: Fallas) {
        val url = "http://192.168.1.77/wServices/registrarFalla.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("Falla registrada exitosamente")) showToast("Registro Subido Correctamente")
                else {
                    val db = DbHandler(this)
                    db.insertDATA(falla)
                    limpiar()
                }
            },
            Response.ErrorListener { error ->
                showToast("Error de conexión con el servidor: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "eqpfal" to falla.eqpfal.toString(),
                    "estfal" to falla.estfal.toString(),
                    "obsfal" to falla.obsfal,
                    "labfal" to falla.labfal.toString()
                )
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun syncDataWithServer() {
        val db = DbHandler(this)
        val fallasList = db.getPendingFallas()

        fallasList.forEach { falla ->
            if (FnClass().haveNetwork(this)) {
                showToast("Registros Sincronizados")
                sendToServer(falla)
                db.dropTable()
            }
        }
    }

    private fun limpiar() {
        observacionesET.setText("")
        estadoET.setText("")
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

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

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showExitDialog()
    }
}
