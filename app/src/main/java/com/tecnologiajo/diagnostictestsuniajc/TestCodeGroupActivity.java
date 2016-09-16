package com.tecnologiajo.diagnostictestsuniajc;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Asignature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestCodeGroupActivity extends AppCompatActivity {
    private static final String TAG = TestCodeGroupActivity.class.getSimpleName();

    public static  ProgressDialog progressDialog;
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_code_group);
        if(getIntent().getBooleanExtra("ADDNAME",false)){
            Fragment fragment = new TestByGroupCodeNameFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.commit();
        }else {
            Fragment fragment = new TestByGroupCodeCodFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.commit();
        }
        registerReceiver();

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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(REGISTRATION_PROCESS)){

                String result  = intent.getStringExtra("result");
                String message = intent.getStringExtra("message");
                Log.d(TAG, "onReceive: " + result + message);
                Fragment fragment = new TestByGroupCodeCodFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.commit();

            }
        }
    };

    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REGISTRATION_PROCESS);
        intentFilter.addAction(MESSAGE_RECEIVED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    public static class TestByGroupCodeNameFragment extends Fragment {

        public TestByGroupCodeNameFragment() {
            super();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_layout_code , container, false);
            Button btnaddcoode = (Button) view.findViewById(R.id.btnaddcoode);
            final EditText editText = (EditText) view.findViewById(R.id.addcode);

            btnaddcoode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editText.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("name",name);
                    Fragment fragment = new TestByGroupCodeCodFragment().newInstance(bundle);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentFragment, fragment);
                    transaction.commit();
                }
            });
            return view;
        }

    }

    public static class TestByGroupCodeCodFragment extends Fragment implements  AsyncApp42ServiceApi.App42StorageServiceListener{
        /**
         * The async service.
         */
        private AsyncApp42ServiceApi asyncService;
        String codigo,id;
        public static TestByGroupCodeCodFragment newInstance(Bundle arguments){
            TestByGroupCodeCodFragment f = new TestByGroupCodeCodFragment();
            if(arguments != null){
                f.setArguments(arguments);
            }
            return f;
        }


        public TestByGroupCodeCodFragment() {
            super();
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_groupcode, container, false);
            Button btnstartTest = (Button) view.findViewById(R.id.btnstartTest);
            final EditText editText = (EditText) view.findViewById(R.id.addcode);
            asyncService = AsyncApp42ServiceApi.instance(getActivity());
            btnstartTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codigo = editText.getText().toString();
                    progressDialog = ProgressDialog.show(getActivity(), "", "Inserting..");
                    progressDialog.setCancelable(true);
                    getGroupbyCode(codigo);
                }
            });
            return view;
        }
        public void getGroupbyCode(String codigo){
            asyncService.findDocByKeyValue(Constants.App42DBName,"Groups","groupId",codigo,this);
        }

        private String getDeviceId(){
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }

        @Override
        public void onDocumentInserted(Storage response) {

        }

        @Override
        public void onUpdateDocSuccess(Storage response) {
            progressDialog.dismiss();
            Intent intent = new Intent(getContext(), AsignatureActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("name", getArguments().getString("name"));
            intent.putExtra("device", getDeviceId());
            intent.putExtra("diagnostico", "");
            intent.putExtra("codigo", codigo);
            intent.putExtra("TESTBYCODE", true);
            startActivity(intent);
            getActivity().finish();
        }

        @Override
        public void onFindDocSuccess(Storage response) {
            JSONObject jsonDocument = null;
            JSONArray jsonArray = null;
            ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
            try {
                for (int i = 0; i < jsonDocList.size(); i++) {
                    jsonDocument = new JSONObject(jsonDocList.get(i).getJsonDoc());
                    jsonArray = new JSONArray(jsonDocument.getString("persons"));

                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",getArguments().getString("name"));
                jsonObject.put("device",getDeviceId());
                jsonArray.put(jsonObject);
                jsonDocument.put("persons",jsonArray);
                id= jsonDocument.getString("idTest");
                asyncService.updateDocByKeyValue(Constants.App42DBName,"Groups","groupId",jsonDocument.getString("groupId"),jsonDocument,this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onInsertionFailed(App42Exception ex) {

        }

        @Override
        public void onFindDocFailed(App42Exception ex) {

        }

        @Override
        public void onUpdateDocFailed(App42Exception ex) {

        }
    }
}
