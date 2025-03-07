package com.example.estanciasapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.estanciasapp.DB.Fallas

const val DATABASE_NAME="MyDb"
const val TABLE_NAME_FALLAS="eqp_fallas"
const val TABLE_NAME_USUARIOS="usuarios"
const val COL_USER="usuario"
const val COL_PASS="contrasena"
const val COL_IDFAL="idfal"
const val COL_EQPFAL="eqpfal"
const val COL_FECFAL="fecfal"
const val COL_ESTFAL="estfal"
const val COL_OBSFAL="obsfal"
const val COL_LABFAL="labfal"

class DbHandler(private var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

  fun openDatabase(): SQLiteDatabase {
        return this.readableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        checkAndCreateTable(db)
       /* createTableUsuarios(db)*/
/*      insertUsuariosPorDefecto(db)*/
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

/*    private fun createTableUsuarios(db: SQLiteDatabase?) {
        val createTable = """
        CREATE TABLE $TABLE_NAME_USUARIOS (
            $COL_USER TEXT PRIMARY KEY,
            $COL_PASS TEXT DEFAULT NULL
        );
    """.trimIndent()
        db?.execSQL(createTable)
        Log.d("DB", "Tabla $TABLE_NAME_USUARIOS creada exitosamente.")
    }*/

/*    private fun insertUsuariosPorDefecto(db: SQLiteDatabase?) {
        val defaultUsuarios = listOf(
            Pair("cegb1402", "cegb"),
            Pair("usuario2", "contrasena2"),
            Pair("usuario3", "contrasena3")
        )

        val cv = ContentValues()
        for (usuario in defaultUsuarios) {
            cv.put(COL_USER, usuario.first)
            cv.put(COL_PASS, usuario.second)
            val result = db?.insert(TABLE_NAME_USUARIOS, null, cv)
            if (result == (-1).toLong()) {
                Log.e("DB", "Error al insertar usuario por defecto")
            } else {
                Log.d("DB", "Usuario por defecto insertado: ${usuario.first}")
            }
        }
    }*/

    private fun checkAndCreateTable(db: SQLiteDatabase?) {
        val cursor = db?.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_NAME_FALLAS'", null)
        if (cursor != null) {
            if (cursor.count == 0) {
                val createTable = """
                    CREATE TABLE $TABLE_NAME_FALLAS (
                        $COL_IDFAL INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COL_EQPFAL INTEGER DEFAULT NULL,
                        $COL_FECFAL TEXT DEFAULT NULL,
                        $COL_ESTFAL INTEGER DEFAULT 1,
                        $COL_OBSFAL TEXT DEFAULT NULL,
                        $COL_LABFAL INTEGER DEFAULT 1
                    );
                """.trimIndent()
                db.execSQL(createTable)
                Log.d("DB", "Tabla $TABLE_NAME_FALLAS creada exitosamente.")
            } else {
                Log.d("DB", "La tabla $TABLE_NAME_FALLAS ya existe.")
            }
            cursor.close()
        }
    }

    fun insertDATA(fallas: Fallas) {

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_EQPFAL, fallas.eqpfal)
        cv.put(COL_FECFAL, fallas.fecfal)
        cv.put(COL_ESTFAL, fallas.estfal)
        cv.put(COL_OBSFAL, fallas.obsfal)
        cv.put(COL_LABFAL,1)
        val result = db.insert(TABLE_NAME_FALLAS, null, cv)

        if (result == (-1).toLong()) {
            Toast.makeText(context, "Se Produjo Un Error, Intenta De Nuevo", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Registro Almacenado Localmente", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPendingFallas(): List<Fallas> {
        val fallasList = mutableListOf<Fallas>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_FALLAS ORDER BY $COL_IDFAL ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val fallas = Fallas(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_IDFAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EQPFAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FECFAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ESTFAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OBSFAL))
                )
                fallasList.add(fallas)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return fallasList
    }

    fun deleteFalla(idfal: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME_FALLAS, "$COL_IDFAL = ?", arrayOf(idfal.toString()))
    }

}
