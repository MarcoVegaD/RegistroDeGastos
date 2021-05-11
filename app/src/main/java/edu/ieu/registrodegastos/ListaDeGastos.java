package edu.ieu.registrodegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaDeGastos extends AppCompatActivity {

    private DBAdmin bdAdmin = new DBAdmin(this, "BaseGastos2", null, 1);
    private Cursor registro;
    private TextView tvIdUsuario;
    private int idUsuario;
    private ListView listViewGastos;
    private ArrayList<String> gastos = new ArrayList<>();
    private ArrayAdapter stringAdapter;
    private ArrayList<Integer> listaGastosID = new ArrayList<>();
    private Button botonAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_gastos);

        //ListView de gastos y su adaptador de String
        listViewGastos = findViewById(R.id.lv_listaGastos);
        stringAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, gastos);
        listViewGastos.setAdapter(stringAdapter);

        tvIdUsuario = findViewById(R.id.tv_idUsuario);
        botonAgregar = findViewById(R.id.btn_agregarGasto);

        //Obtiene la intencion de donde vino, para obtener el dato del idUsuario y lo agrega a la vista
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        tvIdUsuario.setText(idUsuario + "");

        //Verifica que haya obtenido realmente un ID, si es 0 o menor entonces no lo obtuvo
        if(idUsuario <= 0){
            Toast.makeText(this, "ERROR OBTENIENDO EL ID USUARIO", Toast.LENGTH_SHORT).show();
        }
        else {

            obtenListaGastos();

        }

        listViewGastos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cambiarActividadGasto(listaGastosID.get(position));

            }
        });

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadGasto = new Intent(getApplicationContext(), RegistroGasto.class);
                actividadGasto.putExtra("idUsuario", idUsuario);
                startActivity(actividadGasto);
            }
        });

    }


    //Cambio para editar o ver los datos del gasto
    private void cambiarActividadGasto(int idGasto){

        Intent actividadGasto = new Intent(getApplicationContext(), RegistroGasto.class);

        actividadGasto.putExtra("idUsuario", idUsuario);
        actividadGasto.putExtra("idGasto", idGasto);

        startActivity(actividadGasto);

    }

    private void obtenListaGastos(){

        SQLiteDatabase db = bdAdmin.getReadableDatabase();
        registro = db.query("gastos", null,"idUsuario = " + idUsuario, null,null,null,null);

        while(registro.moveToNext()){

            gastos.add(registro.getString(2));
            listaGastosID.add(registro.getInt(0));

        }
        db.close();
        stringAdapter.notifyDataSetChanged();

        if(gastos.isEmpty()){
            Toast.makeText(this, "No existen registros de Gastos", Toast.LENGTH_SHORT).show();
        }

    }

}