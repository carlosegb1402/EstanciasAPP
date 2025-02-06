package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.estanciasapp.DB.DbHandler
import com.example.estanciasapp.DB.Fallas

class Formulario : AppCompatActivity() {
    //edit text
    private lateinit var nombreET:EditText
    private lateinit var numeroET: EditText
    private lateinit var modeloET: EditText
    private lateinit var areaET: EditText
    private lateinit var estadoET: EditText
    private lateinit var observacionesET: EditText
    //buttons
    private lateinit var limpiarBTN: ImageButton
    private lateinit var cancelarBTN: Button
    private lateinit var registrarBTN: Button
    //datos
    private lateinit var id:String
    private lateinit var nombre:String
    private lateinit var numero:String
    private lateinit var area:String
    private lateinit var modelo:String
    private lateinit var laboratorio:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

        //llamado de las funciones
        iniciarComponentes()
        eventsBTN()

        //obtener infromacion del intent y split
        val values = intent.getStringExtra("informacion")?.split(",") ?: listOf()



        //condicion para comprobar los datos recibidos
        if (values.size==6) {
            id = values[0]
            nombre=   values[1]
            numero=    values[2]
            area=   values[3]
            modelo=  values[4]
            laboratorio=values[5]

            nombreET.setText(nombre);numeroET.setText(numero);areaET.setText(area);modeloET.setText(modelo)

        }else{
            actQR()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    //fn para iniciar componentes
    private fun iniciarComponentes(){
        numeroET=findViewById(R.id.numeroET)
        nombreET=findViewById(R.id.nombreET)
        modeloET=findViewById(R.id.modeloET)
        areaET=findViewById(R.id.areaET)
        estadoET=findViewById(R.id.estFalloET)
        observacionesET=findViewById(R.id.observacionesET)
        limpiarBTN=findViewById(R.id.btnLimpiar)
        cancelarBTN=findViewById(R.id.cancelarBTN)
        registrarBTN=findViewById(R.id.registrarBTN)
    }

    //fn eventos botones
    private fun eventsBTN(){
        limpiarBTN.setOnClickListener{
            if(observacionesET.text.toString().isEmpty()||estadoET.text.toString().isEmpty()){
                showMSG("No hay informacion para borrar")
            }
            else{
                limpiar()
            }
        }

        //boton registrar
        cancelarBTN.setOnClickListener{
            alertExit()
        }

        //boton registrar
        registrarBTN.setOnClickListener{
            AlertDialog.Builder(this)
                .setMessage("¿Seguro que desea realizar el registro?")
                .setCancelable(false).setTitle("Confirmacion")
                .setPositiveButton("Sí") { _, _ ->

                    if(observacionesET.text.toString().isEmpty()||estadoET.text.toString().isEmpty()){
                    showMSG("Campos Vacios")
                }
                else{
                    if (estadoET.text.toString().toInt()>4){
                        showMSG("Ingrese un Estado del 1 al 4 Solamente")
                    }
                    else {
                        val fallas = Fallas(
                            id.toInt(),
                            estadoET.text.toString().toInt(),
                            observacionesET.text.toString(),
                            laboratorio.toInt()
                        )
                        val db = DbHandler(this)
                        db.insertDATA(fallas)
                        limpiar()
                    }
                }}
                .setNegativeButton("No", null)
                .show()
        }
    }

    //fn mostrar toast
    private fun showMSG(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

    //fn limpiar edit text
    private fun limpiar(){
        observacionesET.setText("");estadoET.setText("")
    }

    //fn mostrar alert
    private  fun alertExit(){
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
            .setCancelable(false).setTitle("Aplicacion")
            .setPositiveButton("Sí") { _, _ -> actQR()}
            .setNegativeButton("No", null)
            .show()
    }

    //fn intent actividad qr
    private fun actQR(){
        val intQR=Intent(this,QR::class.java)
        startActivity(intQR)
        finish()
    }

    ///fn boton atras de android
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        alertExit()
    }



}