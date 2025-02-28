package com.example.estanciasapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.estanciasapp.databinding.ActivityQrBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QR : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner:BarcodeScanner
    private lateinit var escanearBTN:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor=Executors.newSingleThreadExecutor()
        barcodeScanner=BarcodeScanning.getClient()
        escanearBTN=binding.escanearBTN

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
            val preview=Preview.Builder().setResolutionSelector(resolutionSelector).build().also { it.surfaceProvider =
                binding.previewView.surfaceProvider }
            val imageAnalyzer=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also { it.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }
                }
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
                    escanearBTN.isEnabled=false
                } else {
                    for (barcode in barcodes) {
                        escanearBTN.isEnabled=true
                        escanearBTN.setOnClickListener{
                        handleBarcode(barcode)
                        }
                    }
                }
            }.addOnFailureListener{Toast.makeText(applicationContext,"Ocurrio un error al escanear el codigo",Toast.LENGTH_SHORT).show()}.addOnCompleteListener { imageProxy.close() }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleBarcode(barcode: Barcode) {

        val txt = barcode.url?.url ?: barcode.displayValue

        val validacion = listOf(
            "Blower",
            "Punta",
            "Bomba",
            "Filtro Cartucho",
            "Ozono",
            "Filtro Carbón",
            "Filtro Zeolita",
            "UV",
            "Calentador 1800",
            "Gen 450",
            "Transf 500",
            "Chiller 20",
            "Cooler",
            "Extractor",
            "Serpentine",
            "Bomba Agua Dulce",
            "MiniSplit"
        )

        val regex = Regex("\\b(${validacion.joinToString("|")})\\b", RegexOption.IGNORE_CASE)

        val text = txt ?: ""

        if (regex.containsMatchIn(text)) {
                cameraExecutor.shutdown()
                val formularioIntent = Intent(this, Formulario::class.java)
                formularioIntent.putExtra("informacion",txt)

                startActivity(formularioIntent)
                finish()
            }
            else{
                FnClass().showToast(this,"El Codigo QR No Es Valido")
            }


    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }



}