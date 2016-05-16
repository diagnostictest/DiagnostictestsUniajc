package com.tecnologiajo.diagnostictestsuniajc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Asignature;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AsignatureActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener {

    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;

    private ListView listAsignatures;

    private DrawableProvider mProvider;

    /**
     * Perform initialization of all fragments and loaders.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in Activity.onSaveInstanceState(android.os.Bundle).
     * Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignature);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        asyncService = AsyncApp42ServiceApi.instance(this);
        mProvider = new DrawableProvider(this);
        listAsignatures = (ListView) findViewById(R.id.listAsignatures);

        // temporal
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findAllDocs(Constants.App42DBName, "asignaturas", this);
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * @param menu The options menu in which you place your items
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal processing happen
     * (calling the item's Runnable or sending a message to its Handler as appropriate).
     * You can use this method for any items for which you would like to do processing without those other facilities.
     * @param item The menu item that was selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementación desde la interface @link{AsyncApp42ServiceApi.App42StorageServiceListener}
     * @param response the response
     */
    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        progressDialog.dismiss();
        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        final List<Asignature> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                String nombre = new JSONObject(jsonDocument.getJsonDoc()).getString("nombre");
                String descripcion = new JSONObject(jsonDocument.getJsonDoc()).getString("descripcion");
                Asignature asignature = new Asignature(docId, nombre, descripcion,jsonDocList.get(i).getJsonDoc());
                Drawable drawable = mProvider.getRoundWithBorder(asignature.getDescripcion().substring(0,1).toUpperCase());
                asignature.setDrawable(drawable);
                convertList.add(asignature);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        AsignatureAdapter asignatureAdapter = new AsignatureAdapter(this, 0, convertList);
        listAsignatures.setAdapter(asignatureAdapter);
        listAsignatures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),DiagnosticsActivity.class);
                intent.putExtra("id",convertList.get(position).getId());
                intent.putExtra("asignatura",convertList.get(position).getSchema());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }
}
