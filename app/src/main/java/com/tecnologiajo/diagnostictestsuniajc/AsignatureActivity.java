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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.tecnologiajo.diagnostictestsuniajc.modelos.RequestResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class AsignatureActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = AsignatureActivity.class.getSimpleName();


    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private static ContentManager contentManager;
    public static FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignature);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        contentManager = new ContentManager(this);
        contentManager.closeContentTemp();
        if(getIntent().getBooleanExtra("TESTBYCODE",false)) {
            Bundle bundle = new Bundle();
            bundle.putString("codigo",getIntent().getStringExtra("codigo"));
            bundle.putString("id",getIntent().getStringExtra("id"));
            bundle.putString("name",getIntent().getStringExtra("name"));
            bundle.putString("device",getIntent().getStringExtra("device"));
            bundle.putString("dowload", "A");
            register((getIntent().getStringExtra("codigo")+"-srv"),bundle);

        }else{
            Fragment fragment = new AsignatureFragmentList();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.display, fragment);
            transaction.commit();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AsignatureFragmentList();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.display, fragment);
                transaction.commit();
                fab.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dowload) {
            /*if(swichetdowload%2==0) {
                item.setTitle("List");
                listdowload();
            }else{
                item.setTitle(R.string.action_dowload);
                listasignature();
            }
            swichetdowload++;**/
            return true;
        }
        if (id == R.id.action_byId) {
            boolean Addname=true;
            if(contentManager.isAdd()) {
                Addname=false;
            }
            Intent intent = new Intent(this,TestCodeGroupActivity.class);
            intent.putExtra("ADDNAME",Addname);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

   /*
    /**
     * Creates the alert dialog.
     * @param msg the msg
     */


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

    public void register(String codigoser, final Bundle bundle){
        RequestResult requestBody = new RequestResult();
        requestBody.setDescripcion("se ha registrado");
        requestBody.setEstado(true);
        requestBody.setGroupTest(codigoser);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOSTSERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<RequestResult> call = request.register(requestBody);
        call.enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                //RequestResult responseBody = response.body();
                Fragment fragment = new TestFragment().newInstance(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.display, fragment);
                transaction.commit();
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                Toast toast= Toast.makeText(getApplication(), t.getMessage(),Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public static class AsignatureFragmentList extends ListFragment implements AdapterView.OnItemClickListener, AsyncApp42ServiceApi.App42StorageServiceListener{


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
        private SharedPreferences sharedpreferences;
        private ArrayList<Storage.JSONDocument> jsonDocList;
        private String docId;
        private int swichetdowload=0;
        private ContentManager contentManager;
        private List<Asignature> convertList;
        public AsignatureFragmentList() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.list_fragment , container, false);
            asyncService = AsyncApp42ServiceApi.instance(getActivity());
            asyncService.setLoggedInUser("jorge");
            mProvider = new DrawableProvider(getActivity());
            sharedpreferences = getActivity().getSharedPreferences("diagnosticos", Context.MODE_PRIVATE);
            // temporal
            progressDialog = ProgressDialog.show(getActivity(), "", "Searching..");
            progressDialog.setCancelable(true);
            asyncService.findAllDocs(Constants.App42DBName, "asignaturas", this);

            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }




        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*Intent intent = new Intent(getActivity(), .class);
            intent.putExtra("id", docId);
            startActivity(intent);*/
            Bundle bundle = new Bundle();
            bundle.putString("id",convertList.get(position).getId());
            Fragment fragment = new DiagnosticsFragmentList().newInstance(bundle);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.display, fragment);
            transaction.commit();
            fab.setVisibility(view.VISIBLE);
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
            convertList = new ArrayList<>();
            try {
                for (int i = 0; i < jsonDocList.size(); i++) {
                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                    docId = jsonDocument.getDocId();
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
            AsignatureAdapter asignatureAdapter = new AsignatureAdapter(getActivity(), 0, convertList);
            setListAdapter(asignatureAdapter);
            getListView().setOnItemClickListener(this);
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

        public  void createAlertDialog(String msg) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
            alertbox.setTitle("Response Message");
            alertbox.setMessage(msg);
            alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                // do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                   /* Intent intent = new Intent(getActivity(), .class);
                    intent.putExtra("id", "");
                    intent.putExtra("diagnostico", "");
                    intent.putExtra("dowload", "A");
                    startActivity(intent);*/
                }
            });
            alertbox.show();
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
            AsignatureAdapter asignatureAdapter = new AsignatureAdapter(getActivity(), 0, convertList);
            setListAdapter(asignatureAdapter);
            getListView().setOnItemClickListener(this);

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
            AsignatureAdapter asignatureAdapter = new AsignatureAdapter(getActivity(), 0, convertList);
            setListAdapter(asignatureAdapter);
            getListView().setOnItemClickListener(this);
            /*listAsignatures.setAdapter(asignatureAdapter);
            listAsignatures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), .class);
                    intent.putExtra("id", convertList.get(position).getId());
                    startActivity(intent);
                }
            });*/
        }
    }


}
