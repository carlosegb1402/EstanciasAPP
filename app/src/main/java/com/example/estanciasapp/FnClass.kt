package com.example.estanciasapp

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.estanciasapp.DB.Fallas


class FnClass {

    //FN MOSTRAR MSG
     fun showToast(context: Context,msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    //FN SINCRONIZACIO DE DATOS LOCALES
     fun syncPendingFallas(context: Context) {
        val db = DbHandler(context)
        val fallasList = db.getPendingFallas()

        if (fallasList.isNotEmpty()) {
            enviarFallaSecuencialmente(context,fallasList, 0, db)
        }

    }

    //FN SUBIR DATOS LOCALES
    private fun enviarFallaSecuencialmente(
        context: Context,
        fallasList: List<Fallas>,
        index: Int,
        db: DbHandler
    ) {
        if (index >= fallasList.size) {
            return
        }
        val falla = fallasList[index]
        val baseUrl = ContextCompat.getString(context,R.string.base_url)
        val url = "$baseUrl/registrarFalla.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("success")) {
                    db.deleteFalla(falla.idfal)
                    enviarFallaSecuencialmente(context,fallasList, index + 1, db)
                } else {
                    if (index < fallasList.size-1){
                        enviarFallaSecuencialmente(context,fallasList, index+1, db)
                    }

                }
            },
            Response.ErrorListener { _ ->

                if (index < fallasList.size-1){
                    enviarFallaSecuencialmente(context,fallasList, index+1, db)
                }

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

        stringRequest.retryPolicy = DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(context).add(stringRequest)
    }

}
