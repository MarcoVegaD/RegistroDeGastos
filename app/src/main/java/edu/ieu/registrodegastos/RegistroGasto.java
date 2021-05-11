package edu.ieu.registrodegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistroGasto extends AppCompatActivity {

    private DBAdmin bdAdmin = new DBAdmin(this, "BaseGastos2", null, 1);
    private Spinner opciones;
    private EditText etNombreGasto, etDescripcion, etMonto;
    private String opcionTipo;
    private int idUsuario, idGasto;
    private Button btnAgregar, btnEditar, btnBorrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_gasto);

        etNombreGasto = findViewById(R.id.et_nombreGasto);
        etDescripcion = findViewById(R.id.et_descripcion);
        etMonto = findViewById(R.id.et_montoGasto);

        btnAgregar = findViewById(R.id.btn_agregarNuevoGasto);
        btnEditar = findViewById(R.id.btn_editarGasto);
        btnBorrar = findViewById(R.id.btn_borrarGasto);

        opciones = findViewById(R.id.spi_AreaGasto);
        String[] listaOpciones = {"Transporte", "Alimento", "Trabajo" , "Entretenimiento", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaOpciones);
        opciones.setAdapter(adapter);

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", -1);
        idGasto = intent.getIntExtra("idGasto", -1);

        //ENTRA AQUI SI EL REGISTRO YA EXISTE
        if(idGasto > 0){
            rellenaCampos(idGasto);
        }

        btnAgregar.setOnClickListener(this::agregarGasto);
        btnEditar.setOnClickListener(this::editarGasto);
        btnBorrar.setOnClickListener(this::borrarGasto);

    }

    private void rellenaCampos(int idABuscar){

        SQLiteDatabase db = bdAdmin.getReadableDatabase();

        Cursor registro = db.query("gastos", new String[]{"nombre", "area", "descripcion", "monto"}, "idGasto = ?", new String[]{idABuscar+""}, null, null, null);

        registro.moveToFirst();

        etNombreGasto.setText(registro.getString(0));

        switch (registro.getString(1)){

            case "Transporte": opciones.setSelection(0); break;
            case "Alimento": opciones.setSelection(1); break;
            case "Trabajo": opciones.setSelection(2); break;
            case "Entretenimiento": opciones.setSelection(3); break;
            case "Otro": opciones.setSelection(4); break;

        }

        etDescripcion.setText(registro.getString(2));
        etMonto.setText(registro.getString(3));

        db.close();

    }

    private void limpiaCampos(){
        etNombreGasto.setText("");
        opciones.setSelection(0);
        etDescripcion.setText("");
        etMonto.setText("");
    }

    private boolean verificaCamposCuenta(){

        boolean verifica = true;

        if(etNombreGasto.getText().toString().isEmpty()){
            Toast.makeText(this, "El campo NOMBRE está vacío", Toast.LENGTH_SHORT).show();
        }
        if(etDescripcion.getText().toString().isEmpty()){
            verifica = false;
            Toast.makeText(this, "El campo DESCRIPCION está vacío", Toast.LENGTH_SHORT).show();
        }
        if(etMonto.getText().toString().isEmpty()){
            verifica = false;
            Toast.makeText(this, "El campo MONTO está vacío", Toast.LENGTH_SHORT).show();
        }

        return verifica;

    }

    private void agregarGasto(View v){

        if(!verificaCamposCuenta()){
            return;
        }


        ContentValues valores = new ContentValues();
        valores.put("idUsuario", idUsuario);
        valores.put("nombre", etNombreGasto.getText().toString());
        valores.put("area", opciones.getSelectedItem().toString());
        valores.put("descripcion", etDescripcion.getText().toString());
        valores.put("monto", etMonto.getText().toString());

        SQLiteDatabase db = bdAdmin.getWritableDatabase();
        long idRegistro = db.insert("gastos", null, valores);
        if(idRegistro > 0) {
            Toast.makeText(this,"Guardado el nuevo gasto", Toast.LENGTH_SHORT).show();
            limpiaCampos();
        }
        else{
            Toast.makeText(this,"ERROR guardando el gasto en la BASE", Toast.LENGTH_SHORT).show();
        }
        db.close();
        regresarAListaGastos();

    }

    private void editarGasto(View v){

        if(!verificaCamposCuenta()){
            return;
        }


        ContentValues valores = new ContentValues();
        valores.put("idUsuario", idUsuario);
        valores.put("nombre", etNombreGasto.getText().toString());
        valores.put("area", opciones.getSelectedItem().toString());
        valores.put("descripcion", etDescripcion.getText().toString());
        valores.put("monto", etMonto.getText().toString());

        SQLiteDatabase db = bdAdmin.getWritableDatabase();
        long idRegistro = db.update("gastos", valores, "idGasto = ?", new String[]{idGasto+""});

        if(idRegistro > 0) {
            Toast.makeText(this,"Gasto editado Satisfactoriamente", Toast.LENGTH_SHORT).show();
            limpiaCampos();
        }
        else{
            Toast.makeText(this,"ERROR editando el gasto", Toast.LENGTH_SHORT).show();
        }
        db.close();
        regresarAListaGastos();

    }

    private void borrarGasto(View v){

        SQLiteDatabase db = bdAdmin.getWritableDatabase();
        long rowsAfectadas = db.delete("gastos", "idGasto = ?", new String[]{idGasto+""});

        if(rowsAfectadas > 0) {
            Toast.makeText(this,"Gasto eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiaCampos();
        }
        else{
            Toast.makeText(this,"ERROR eliminando el gasto", Toast.LENGTH_SHORT).show();
        }
        db.close();
        regresarAListaGastos();

    }

    private void regresarAListaGastos(){
        Intent intencion = new Intent(getApplicationContext(), ListaDeGastos.class);
        intencion.putExtra("idUsuario", idUsuario);
        startActivity(intencion);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("idUsuario", idUsuario);
        setResult(RESULT_OK, intent);
        finish();
    }

}