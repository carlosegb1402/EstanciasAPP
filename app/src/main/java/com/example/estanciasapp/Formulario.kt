package com.example.estanciasapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
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
    private lateinit var btnClean: Button
    private lateinit var btnCancel: Button
    private lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)

        iniciarComponentes()
        val id=intent.getStringExtra("id").toString()
        etName.setText(intent.getStringExtra("nombre").toString())
        etNum.setText(intent.getStringExtra("numEquipo").toString())
        etArea.setText(intent.getStringExtra("area").toString())
        etName.setText(intent.getStringExtra("modelo").toString())
        etName.setText(intent.getStringExtra("nombre").toString())



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


}