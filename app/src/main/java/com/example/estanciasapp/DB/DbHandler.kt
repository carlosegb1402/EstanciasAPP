package com.example.estanciasapp.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME="MyDb"
val TABLE_NAME="eqp_fallas"
val COL_IDFAL="idfal"
val COL_EQPFAL="eqpfal"
val COL_FECFAL="fecfal"
val COL_ESTFAL="estfal"
val COL_OBSFAL="obsfal"
val COL_LABFAL="labfal"


class DbHandler (var context:Context):SQLiteOpenHelper(context, DATABASE_NAME,null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable="""
            CREATE TABLE $TABLE_NAME (
                $COL_IDFAL INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EQPFAL INTEGER DEFAULT NULL,
                $COL_FECFAL TEXT DEFAULT CURRENT_TIMESTAMP,
                $COL_ESTFAL INTEGER DEFAULT 1,
                $COL_OBSFAL TEXT DEFAULT NULL,
                $COL_LABFAL INTEGER DEFAULT 1
            );
        """.trimIndent()

        db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertDATA(fallas: Fallas){

        val db=this.writableDatabase
        var cv=ContentValues()

        cv.put(COL_EQPFAL,fallas.eqpfal)
        cv.put(COL_ESTFAL,fallas.estfal)
        cv.put(COL_OBSFAL,fallas.obsfal)
        cv.put(COL_LABFAL,fallas.labfal)

        var result=db.insert(TABLE_NAME,null,cv)

        if(result==-1.toLong()){
            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,"Succes",Toast.LENGTH_SHORT).show()
        }

    }
}



