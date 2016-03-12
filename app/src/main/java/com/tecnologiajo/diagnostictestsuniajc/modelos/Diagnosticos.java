package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by estudiante308 on 09/03/2016.
 */
public class Diagnosticos extends SQLiteOpenHelper {
    private static final int VERSION_BASEDATOS = 1;
    // Nombre de nuestro archivo de base de datos
    private static final String NOMBRE_BASEDATOS = "daignostictest.db";
    // Sentencia SQL para la creación de una tabla
    private static final String TABLA_DIAGNOSTICOS = "CREATE TABLE diagnostico" +
            "(_id VARCHAR(100) PRIMARY KEY, JSONDiagnostico  TEXT)";
    public Diagnosticos(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_DIAGNOSTICOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_DIAGNOSTICOS);
        onCreate(db);
    }
    public void insertarDIAGNOSTICOS(int id, String JSONDoc) {
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put("_id", id);
            valores.put("JSONDiagnostico", JSONDoc);
            db.insert("diagnostico", null, valores);
            db.close();
        }
    }

    public void modificarDIAGNOSTICOS(int id, String JSONDoc){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("JSONDiagnostico", JSONDoc);
        db.update("diagnostico", valores, "_id=" + id, null);
        db.close();
    }

    public void borrarDIAGNOSTICOS(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("diagnostico", "_id=" + id, null);
        db.close();
    }

    public Diagnostico recuperarDIAGNOSTICOS(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id", "JSONDiagnostico"};
        Cursor c = db.query("negocios", valores_recuperar, "id_registro='" + id +"'", null, null, null, null, null);
        Diagnostico diagnostico=null;
        if(c.getCount()>0) {
            if (c != null) {
                c.moveToFirst();
            }
            //diagnostico = new diagnostico(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getInt(6), c.getString(7));

        }
        db.close();
        c.close();
        return diagnostico;
    }

    public List<Diagnostico> recuperarDIAGNOSTICOSS() {
        SQLiteDatabase db = getReadableDatabase();
        List<Diagnostico> lista_negocios = new ArrayList<Diagnostico>();
        String[] valores_recuperar = {"_id", "JSONDiagnostico"};
        Cursor c = db.query("negocios", valores_recuperar, null, null, null, null, null, null);
        if(c.getCount()>0) {
            c.moveToFirst();
            do {

                //Diagnostico diagnostico = new Diagnostico(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),c.getString(4),c.getString(5),c.getDouble(6));

                //lista_negocios.add(diagnostico);
            } while (c.moveToNext());
        }
        db.close();
        c.close();
        return lista_negocios;
    }
}
