package edu.ieu.registrodegastos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBAdmin extends SQLiteOpenHelper {

    public DBAdmin(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREAR LA BASE DEL USUARIO(s)
        String sqlUsuarios = "CREATE TABLE usuarios(idUsuario int primary key, numeroCuenta int unique,  nombre text, saldo real)";
        db.execSQL(sqlUsuarios);

        String sqlGastos = "CREATE TABLE gastos(idGasto INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, idUsuario int NOT NULL, nombre text, area text, descripcion text, monto real, FOREIGN KEY(idUsuario) REFERENCES usuarios(idUsuario))";
        db.execSQL(sqlGastos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
