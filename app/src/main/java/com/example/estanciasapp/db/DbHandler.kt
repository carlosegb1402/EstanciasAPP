package com.example.estanciasapp.db

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

        val createTable="CREATE TABLE "+ TABLE_NAME+" (\n" +
                COL_IDFAL+" int NOT NULL AUTO_INCREMENT,\n" +
                COL_EQPFAL+" int DEFAULT NULL,\n" +
                COL_FECFAL+" date DEFAULT (CURRENT_TIMESTAMP),\n" +
                COL_ESTFAL+" int DEFAULT '1',\n" +
                COL_OBSFAL+" varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,\n" +
                COL_LABFAL+" int DEFAULT '1',\n" +
                "PRIMARY KEY ("+ COL_IDFAL+")\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=353 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;"

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



