package com.example.estanciasapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.estanciasapp.databinding.ActivityMainBinding
import com.example.estanciasapp.databinding.ActivityQrBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.jar.Manifest

class QR : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner:BarcodeScanner
    private lateinit var msgTV:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor=Executors.newSingleThreadExecutor()
        barcodeScanner=BarcodeScanning.getClient()
        msgTV=binding.msgTV

        val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
            if(isGranted){
                startCamera()
            }else{
                Toast.makeText(applicationContext,"No cuenta con los permisos necesarios para usar la Camara",Toast.LENGTH_SHORT).show()
            }

        }
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun startCamera(){
        val cameraProviderFuture=ProcessCameraProvider.getInstance(this)
        val screenSize=Size(640,480)
        val resolutionSelector=ResolutionSelector.Builder().setResolutionStrategy(ResolutionStrategy(screenSize,ResolutionStrategy.FALLBACK_RULE_NONE)).build()
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()
            val preview=Preview.Builder().setResolutionSelector(resolutionSelector).build().also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
            val imageAnalyzer=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also { it.setAnalyzer(cameraExecutor,{
                imageProxy->
                processImageProxy(imageProxy)
            }) }
            val cameraSelector= CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalyzer)
        },ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy){
        val mediaImage=imageProxy.image
        if(mediaImage!=null){
            val image = InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image).addOnSuccessListener { barcodes->
                if (barcodes.isEmpty()) {
                    msgTV.setText("")
                } else {
                    for (barcode in barcodes) {
                        handleBarcode(barcode)
                    }
                }
            }.addOnFailureListener{Toast.makeText(applicationContext,"Ocurrio un error al escanear el codigo",Toast.LENGTH_SHORT).show()}.addOnCompleteListener { imageProxy.close() }
        }
    }
    private fun handleBarcode(barcode: Barcode){
        val opcionEscogida: String = intent.getStringExtra("opcion").toString()
        val opcionesValidas = setOf("1", "2", "3", "4")
        val txt=barcode.url ?.url ?:barcode.displayValue
        val partes= txt?.split("\n")

        val id= partes?.get(0)
        val nombre=partes?.get(1)
        val numEquipo=partes?.get(2)
        val area=partes?.get(3)
        val modelo=partes?.get(4)


        if (txt==opcionEscogida){

            cameraExecutor.shutdown()

            val formularioIntent=Intent(this,Formulario::class.java)

            formularioIntent.putExtra("id",id)
            formularioIntent.putExtra("nombre",nombre)
            formularioIntent.putExtra("numEquipo",numEquipo)
            formularioIntent.putExtra("area",area)
            formularioIntent.putExtra("modelo",modelo)

            startActivity(formularioIntent)

            finish()
        }
        else if (txt in opcionesValidas){
            msgTV.setText("El Codigo QR Es Incorrecto")
        }
        else{
            msgTV.setText("El Codigo QR No Es Valido")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}