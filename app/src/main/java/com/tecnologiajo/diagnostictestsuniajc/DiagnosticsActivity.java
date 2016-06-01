package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnostico;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticsActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener {
    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;
    /**
     * The doc id.
     */
    private String docId = "";
    private ListView listDianostic;
    private DrawableProvider mProvider;
    private String diagnostico = "";
    private ArrayList<Storage.JSONDocument> jsonDocList;
    private int swichetdowload = 0;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        asyncService = AsyncApp42ServiceApi.instance(this);
        mProvider = new DrawableProvider(this);
        listDianostic = (ListView) findViewById(R.id.listDiagnoostics);

        docId = getIntent().getExtras().getString("id", "");

        sharedpreferences = getSharedPreferences("diagnosticos", Context.MODE_PRIVATE);
        // temporal
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByKeyValue(Constants.App42DBName, "diagnosticos", "id_creator", docId, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dowload) {
            if (swichetdowload % 2 == 0) {
                item.setTitle("List");
                final List<Diagnostico> convertList = new ArrayList<>();
                try {
                    for (int i = 0; i < jsonDocList.size(); i++) {
                        Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                        String docId = jsonDocument.getDocId();
                        JSONObject jsonObject = new JSONObject(jsonDocument.getJsonDoc());
                        Diagnostico diagnostico = new Diagnostico();
                        diagnostico.setId(docId);
                        diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                        diagnostico.setSchema(jsonDocument.getJsonDoc());
                        diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                        diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                        Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0, 1).toUpperCase());
                        diagnostico.setDrawable(drawable);
                        convertList.add(diagnostico);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(this, 0, true, convertList);
                listDianostic.setAdapter(diagnosticsAdapter);
                listDianostic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(convertList.get(position).getId(), convertList.get(position).getSchema());
                        editor.commit();
                    }
                });
            } else {
                item.setTitle(R.string.action_dowload);
                final List<Diagnostico> convertList = new ArrayList<>();
                try {
                    for (int i = 0; i < jsonDocList.size(); i++) {
                        Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                        String docId = jsonDocument.getDocId();
                        JSONObject jsonObject = new JSONObject(jsonDocument.getJsonDoc());
                        Diagnostico diagnostico = new Diagnostico();
                        diagnostico.setId(docId);
                        diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                        diagnostico.setSchema(jsonDocument.getJsonDoc());
                        diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                        diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                        Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0, 1).toUpperCase());
                        diagnostico.setDrawable(drawable);
                        convertList.add(diagnostico);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(this, 0, convertList);
                listDianostic.setAdapter(diagnosticsAdapter);
                listDianostic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        intent.putExtra("id", convertList.get(position).getId());
                        intent.putExtra("diagnostico", convertList.get(position).getSchema());
                        intent.putExtra("dowload", "F");
                        startActivity(intent);
                    }
                });
            }
            swichetdowload++;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        progressDialog.dismiss();
        jsonDocList = response.getJsonDocList();
        final List<Diagnostico> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                JSONObject jsonObject = new JSONObject(jsonDocument.getJsonDoc());
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setId(docId);
                diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                diagnostico.setSchema(jsonDocument.getJsonDoc());
                diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0, 1).toUpperCase());
                diagnostico.setDrawable(drawable);
                convertList.add(diagnostico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(this, 0, convertList);
        listDianostic.setAdapter(diagnosticsAdapter);
        listDianostic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra("id", convertList.get(position).getId());
                intent.putExtra("diagnostico", convertList.get(position).getSchema());
                startActivity(intent);
            }
        });
        listDianostic.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("No se encontraron diagnosticos asociados a esta asignatura");
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(DiagnosticsActivity.this);
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                closeActivity();
            }
        });
        alertbox.show();
    }

    public void closeActivity() {
        finish();
    }
}
