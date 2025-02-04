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

class Formulario : AppCompatActivity() {
    private lateinit var etName:EditText
    private lateinit var etNum: EditText
    private lateinit var etMod: EditText
    private lateinit var etArea: EditText
    private lateinit var etEF: EditText
    private lateinit var etObs: EditText
    private lateinit var btnClean: ImageButton
    private lateinit var btnCancel: Button
    private lateinit var btnRegister: Button
    private lateinit var name:String
    private lateinit var num:String
    private lateinit var area:String
    private lateinit var model:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)


        iniciarComponentes()
        eventsBTN()

        val values=intent.getStringExtra("informacion").toString().split("\n")

        if (values.isNotEmpty() && values.size==5) {

            name=   values[1]
            num=    values[2]
            area=   values[3]
            model=  values[4]

            etName.setText(name);etNum.setText(num);etArea.setText(area);etMod.setText(model)

        }else{
            actQR()

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun iniciarComponentes(){
        etNum=findViewById(R.id.numET)
        etName=findViewById(R.id.nombreET)
        etMod=findViewById(R.id.modeloET)
        etArea=findViewById(R.id.areaET)
        etEF=findViewById(R.id.estFalloET)
        etObs=findViewById(R.id.etObs)
        btnClean=findViewById(R.id.btnLimpiar)
        btnCancel=findViewById(R.id.btnCancelar)
        btnRegister=findViewById(R.id.btnRegistrar)
    }

    private fun eventsBTN(){
        btnClean.setOnClickListener{
            if(etObs.text.toString().isEmpty()||etEF.text.toString().isEmpty()){
                showMSG("No hay informacion para eliminar")
            }
            else{
                limpiar()
            }
        }
        btnCancel.setOnClickListener{
            alertExit()
        }

        btnRegister.setOnClickListener{
            if(etObs.text.toString().isEmpty()||etEF.text.toString().isEmpty()||etNum.text.toString().isEmpty()
                ||etName.text.toString().isEmpty()||etArea.text.toString().isEmpty()||etMod.text.toString().isEmpty()){
                showMSG("Campos Vacios")
            }
            else{
                //
            }

        }
    }

    private fun showMSG(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

    private fun limpiar(){
        etObs.setText("");etEF.setText("")
    }

    private  fun alertExit(){
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres salir?")
            .setCancelable(false).setTitle("Aplicacion")
            .setPositiveButton("Sí") { _, _ -> actQR()}
            .setNegativeButton("No", null)
            .show()
    }

    private fun actQR(){
        val intQR=Intent(this,QR::class.java)
        startActivity(intQR)
        finish()
    }

    ///programacion boton atras de android
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        alertExit()
    }



}