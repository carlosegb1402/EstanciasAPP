package com.example.estanciasapp.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

const val DATABASE_NAME="MyDb"
const val TABLE_NAME="eqp_fallas"
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
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun checkAndCreateTable(db: SQLiteDatabase?) {
        val cursor = db?.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_NAME'", null)
        if (cursor != null) {
            if (cursor.count == 0) {
                val createTable = """
                    CREATE TABLE $TABLE_NAME (
                        $COL_IDFAL INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COL_EQPFAL INTEGER DEFAULT NULL,
                        $COL_FECFAL TEXT DEFAULT NULL,
                        $COL_ESTFAL INTEGER DEFAULT 1,
                        $COL_OBSFAL TEXT DEFAULT NULL,
                        $COL_LABFAL INTEGER DEFAULT 1
                    );
                """.trimIndent()
                db.execSQL(createTable)
                Log.d("DB", "Tabla $TABLE_NAME creada exitosamente.")
            } else {
                Log.d("DB", "La tabla $TABLE_NAME ya existe.")
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
        cv.put(COL_LABFAL, fallas.labfal)

        val result = db.insert(TABLE_NAME, null, cv)

        if (result == (-1).toLong()) {
            Toast.makeText(context, "Error al Almacenar Localmente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Almacenado Localmente", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPendingFallas(): List<Fallas> {
        val fallasList = mutableListOf<Fallas>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_IDFAL DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val fallas = Fallas(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_EQPFAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FECFAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ESTFAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OBSFAL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_LABFAL))
                )
                fallasList.add(fallas)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return fallasList
    }

    fun deleteFalla(eqpfal: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_EQPFAL = ?", arrayOf(eqpfal.toString()))
    }
}
