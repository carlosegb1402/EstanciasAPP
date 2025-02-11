import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.estanciasapp.DB.DbHandler
import com.example.estanciasapp.DB.Fallas
import java.net.HttpURLConnection
import java.net.URL

class FnClass {

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }

     fun sendToServer(context: Context,falla: Fallas) {

        val db = DbHandler(context)
        val url = "http://172.16.13.213/wServices/registrarFalla.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("Falla registrada exitosamente")) showToast(context,"Registro Exitoso")

            },
            Response.ErrorListener { error ->
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


    fun syncPendingFallas(context: Context) {
        val db = DbHandler(context)
        val fallasList = db.getPendingFallas()
<<<<<<< Updated upstream
        val url = "http://172.16.13.213/wServices/registrarFalla.php"

        if(fallasList.isNotEmpty()){
        fallasList.forEach {
                falla ->
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    if (response.contains("Falla registrada exitosamente")) db.deleteFalla(falla.eqpfal)
                },
                Response.ErrorListener { error ->
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
        }else{
            return
=======

        if (fallasList.isNotEmpty()) {
            enviarFallaSecuencialmente(context, fallasList, 0, db)
>>>>>>> Stashed changes
        }

    }

    fun enviarFallaSecuencialmente(context: Context, fallasList: List<Fallas>, index: Int, db: DbHandler) {
        if (index >= fallasList.size) return // Si ya procesamos todos, salimos

        val falla = fallasList[index]

        val url = "http://192.168.1.152/wServices/registrarFalla.php"
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.contains("Falla registrada exitosamente")) {
                    db.deleteFalla(falla.eqpfal) // Eliminamos la falla si se subió correctamente
                }
                // Llamamos al siguiente registro solo después de recibir respuesta
                enviarFallaSecuencialmente(context, fallasList, index + 1, db)
            },
            Response.ErrorListener { error ->
                // Si hay un error, simplemente pasamos al siguiente sin insertar localmente
                enviarFallaSecuencialmente(context, fallasList, index + 1, db)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "eqpfal" to falla.eqpfal.toString(),
                    "fecfal" to falla.fecfal,
                    "estfal" to falla.estfal.toString(),
                    "obsfal" to falla.obsfal,
                    "labfal" to falla.labfal.toString()
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
