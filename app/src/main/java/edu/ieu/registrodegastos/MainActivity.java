package edu.ieu.registrodegastos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DBAdmin bdAdmin = new DBAdmin(this, "BaseGastos2", null, 1);
    private EditText etCuenta, etNombre, etSaldo, etBuscarID;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCuenta = findViewById(R.id.et_numCuenta);
        etNombre = findViewById(R.id.et_nombre);
        etSaldo = findViewById(R.id.et_saldo);
        etBuscarID = findViewById(R.id.et_buscarID);

    }

    //Entra a la base de datos y busca el ID de la cuenta para usar
    public void buscarID(View v){

        //SI EL CAMPO ID ESTÁ VACIO, CORTA TODO EL METODO
        if(etBuscarID.getText().toString().isEmpty()){
            Toast.makeText(this,"Campo ID esta vacio", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = bdAdmin.getReadableDatabase();

        idUsuario = Integer.parseInt(etBuscarID.getText().toString());

        //Cursor registros = db.rawQuery("SELECT * FROM usuarios WHERE idUsuario = ?", new String[idUsuario]);
        Cursor registros = db.query("usuarios", null, "idUsuario = " + idUsuario, null, null, null, null);


        if(!registros.moveToFirst()) {
            Toast.makeText(this,"No existe el usuario con el ID " + idUsuario, Toast.LENGTH_LONG).show();
            db.close();
        }
        else{
            //Toast.makeText(this,"Accediendo a listado con ID " + idUsuario, Toast.LENGTH_SHORT).show();
            limpiarEditTexts();
            db.close();
            cambiarDeActividad();
        }

    }


    //Agrega una cuenta nueva a la base de usuarios y genera un ID aleatorio
    public void agregarCuenta(View v){

        //SI ALGUN CAMPO ESTÁ VACIO, CORTA TODO EL METODO
        if(!verificaCamposCuenta()){
            return;
        }

        Cursor registros;

        SQLiteDatabase db = bdAdmin.getReadableDatabase();

        //GENERA ID RANDOM Y VERIFICA QUE NO ESTÉ EN LA BASE DE DATOS
        do{
            idUsuario = new Random().nextInt(999) + 1;
            registros = db.query("usuarios", null, "idUsuario = " + idUsuario, null, null, null, null);
            System.out.println(idUsuario);
        }while(registros.moveToFirst());
        db.close();

        db = bdAdmin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("idUsuario", idUsuario);
        valores.put("numeroCuenta", Double.parseDouble(etCuenta.getText().toString()));
        valores.put("nombre", etNombre.getText().toString());
        valores.put("saldo", Double.parseDouble(etSaldo.getText().toString()));

        long idRegistro = db.insert("usuarios", null, valores);

        if(idRegistro > 0) {
            Toast.makeText(this,"Guardados los datos del usuario con ID " + idUsuario, Toast.LENGTH_LONG).show();
            limpiarEditTexts();
            db.close();
            cambiarDeActividad();
        }
        else{
            Toast.makeText(this,"ERROR guardando datos en la BASE", Toast.LENGTH_LONG).show();
            db.close();
        }

    }

    private boolean verificaCamposCuenta(){

        boolean verifica = true;

        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "El campo NOMBRE está vacío", Toast.LENGTH_SHORT).show();
        }
        if(etCuenta.getText().toString().isEmpty()){
            verifica = false;
            Toast.makeText(this, "El campo NUM. CUENTA está vacío", Toast.LENGTH_SHORT).show();
        }
        if(etSaldo.getText().toString().isEmpty()){
            verifica = false;
            Toast.makeText(this, "El campo SALDO está vacío", Toast.LENGTH_SHORT).show();
        }

        return verifica;

    }

    private void limpiarEditTexts(){
        etCuenta.setText("");
        etSaldo.setText("");
        etNombre.setText("");
        etBuscarID.setText("");
    }

    private void cambiarDeActividad(){
        Intent intencion = new Intent(this, ListaDeGastos.class);
        intencion.putExtra("idUsuario", idUsuario);
        startActivity(intencion);
    }
}