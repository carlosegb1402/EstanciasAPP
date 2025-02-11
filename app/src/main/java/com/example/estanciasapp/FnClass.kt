package com.example.estanciasapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.estanciasapp.DB.DbHandler
import com.example.estanciasapp.DB.Fallas

class FnClass {

    //Funcion Para La Comprobacion De Internet
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    //Funcion Mandar UN Solo Request
    fun sendToServer(context: Context,falla: Fallas) {

        val db = DbHandler(context)
        val url = "http://192.168.137.1/wServices/registrarFalla.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("Falla registrada exitosamente")) showToast(context,"Registro Exitoso")

            },
            Response.ErrorListener { _ ->
                db.insertDATA(falla)
                showToast(context,"Error de conexión con el servidor")
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
        Volley.newRequestQueue(context).add(stringRequest)
    }

    fun syncPendingFallas(context: Context, onComplete: () -> Unit) {
        val db = DbHandler(context)
        val fallasList = db.getPendingFallas()

        if (fallasList.isNotEmpty()) {
            enviarFallaSecuencialmente(context, fallasList, 0, db, onComplete)
        } else {
            onComplete()
        }
    }

    fun enviarFallaSecuencialmente(context: Context, fallasList: List<Fallas>, index: Int, db: DbHandler, onComplete: () -> Unit) {
        if (index >= fallasList.size) {
            onComplete()
            return
        }

        val falla = fallasList[index]
        val url = "http://192.168.137.1/wServices/registrarFalla.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("Falla registrada exitosamente")) {
                    db.deleteFalla(falla.eqpfal)
                }
                enviarFallaSecuencialmente(context, fallasList, index + 1, db, onComplete)
            },
            Response.ErrorListener { _ ->
                enviarFallaSecuencialmente(context, fallasList, index + 1, db, onComplete)
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

        Volley.newRequestQueue(context).add(stringRequest)
    }


    //FN MOSTRAR MSG
     fun showToast(context: Context,msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
