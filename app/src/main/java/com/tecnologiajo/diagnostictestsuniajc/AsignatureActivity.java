package com.tecnologiajo.diagnostictestsuniajc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Asignature;
import com.tecnologiajo.diagnostictestsuniajc.modelos.ContentManager;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnostico;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ListView listDianostic;

    private DrawableProvider mProvider;
    private int swichetdowload=0;

    private SharedPreferences sharedpreferences;
    private ArrayList<Storage.JSONDocument> jsonDocList;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = AsignatureActivity.class.getSimpleName();


    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ContentManager contentManager;
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
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        asyncService = AsyncApp42ServiceApi.instance(this);
        mProvider = new DrawableProvider(this);
        listAsignatures = (ListView) findViewById(R.id.listAsignatures);

        sharedpreferences = getSharedPreferences("diagnosticos", Context.MODE_PRIVATE);

        // temporal
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findAllDocs(Constants.App42DBName, "asignaturas", this);

        //registerReceiver();
        contentManager = new ContentManager(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.getAction().equals(MESSAGE_RECEIVED)){
                String message = intent.getStringExtra("message");
                showAlertDialog(message);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu items in to menu.
     * @param menu The options menu in which you place your items
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main1, menu);
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
        if (id == R.id.action_dowload) {
            if(swichetdowload%2==0) {
                item.setTitle("List");
                listdowload();
            }else{
                item.setTitle(R.string.action_dowload);
                listasignature();
            }
            swichetdowload++;
            return true;
        }
        if (id == R.id.action_byId) {
            if(contentManager.isAdd()) {
                testByCodeDialog();
            }else{
                nameTestCodeDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRegisterProcess(){

        if(checkPermission()){

           // startRegisterService();

        } else {

            requestPermission();
        }

    }


    private void startRegisterService(String name){

        Intent intent = new Intent(AsignatureActivity.this, RegistrationIntentService.class);
        intent.putExtra("DEVICE_NAME", name);
        startService(intent);
    }

    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REGISTRATION_PROCESS);
        intentFilter.addAction(MESSAGE_RECEIVED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(REGISTRATION_PROCESS)){

                String result  = intent.getStringExtra("result");
                String message = intent.getStringExtra("message");
                Log.d(TAG, "onReceive: " + result + message);
                //Snackbar.make(findViewById(R.id.coordinatorLayout),result + " : " + message,Snackbar.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(MESSAGE_RECEIVED)){

                String message = intent.getStringExtra("message");
                showAlertDialog(message);
            }
        }
    };

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //startRegisterService();

                } else {

                    //Snackbar.make(findViewById(R.id.co), "Permission Denied, Please allow to proceed !.", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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
        jsonDocList = response.getJsonDocList();
        final List<Asignature> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                String nombre = new JSONObject(jsonDocument.getJsonDoc()).getString("nombre");
                String descripcion = new JSONObject(jsonDocument.getJsonDoc()).getString("descripcion");
                Asignature asignature = new Asignature(docId, nombre, descripcion);
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
                Intent intent = new Intent(getApplicationContext(), DiagnosticsActivity.class);
                intent.putExtra("id", convertList.get(position).getId());
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
        createAlertDialog("Exception Occurred : " + ex.getMessage());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    public void listdowload(){
        final List<Diagnostico> convertList = new ArrayList<>();
        Map<String,?> keys = sharedpreferences.getAll();
        try {
            for(Map.Entry<String,?> entry : keys.entrySet()){
                JSONObject jsonObject = new JSONObject(entry.getValue().toString());
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setId(entry.getKey());
                diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                diagnostico.setSchema(entry.getValue().toString());
                diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0,1).toUpperCase());
                diagnostico.setDrawable(drawable);
                convertList.add(diagnostico);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(this, 0, convertList);
        listAsignatures.setAdapter(diagnosticsAdapter);
        listAsignatures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra("id", convertList.get(position).getId());
                intent.putExtra("diagnostico", convertList.get(position).getSchema());
                intent.putExtra("dowload", "D");
                startActivity(intent);
            }
        });
    }

    public void listasignature(){
        final List<Asignature> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                String nombre = new JSONObject(jsonDocument.getJsonDoc()).getString("nombre");
                String descripcion = new JSONObject(jsonDocument.getJsonDoc()).getString("descripcion");
                Asignature asignature = new Asignature(docId, nombre, descripcion);
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
                Intent intent = new Intent(getApplicationContext(), DiagnosticsActivity.class);
                intent.putExtra("id", convertList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(
                AsignatureActivity.this);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra("id", "");
                intent.putExtra("diagnostico", "");
                intent.putExtra("dowload", "A");
                startActivity(intent);
            }
        });
        alertbox.show();
    }

    /**
     * Creates the add code dialog.
     *
     */
    public void testByCodeDialog() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(AsignatureActivity.this);
        //alertbox.setTitle("Test");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout_code, null);
        final EditText editText = (EditText) view.findViewById(R.id.addcode);
        alertbox.setView(view);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                String codigo = editText.getText().toString();
                intent.putExtra("id", "");
                intent.putExtra("diagnostico", "");
                intent.putExtra("dowload", "A");
                intent.putExtra("codigo", codigo);
                startActivity(intent);
            }
        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertbox.show();
    }



    /**
     * Creates the add code dialog.
     *
     */
    public void nameTestCodeDialog() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(AsignatureActivity.this);
        //alertbox.setTitle("Test");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout_code, null);
        final EditText editText = (EditText) view.findViewById(R.id.addcode);
        editText.setHint("name");
        alertbox.setView(view);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {
                if(checkPermission()){
                    String name = editText.getText().toString();
                    startRegisterService(name);

                }
            }
        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertbox.show();
    }



    private void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GCM Message Received !");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }




}
