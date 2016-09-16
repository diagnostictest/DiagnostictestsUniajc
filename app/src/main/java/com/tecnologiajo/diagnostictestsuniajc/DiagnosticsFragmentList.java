package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnostico;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticsFragmentList extends ListFragment implements AdapterView.OnItemClickListener,AsyncApp42ServiceApi.App42StorageServiceListener {
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
    private static List<Diagnostico> convertList;
    public static DiagnosticsFragmentList newInstance(Bundle arguments){
        DiagnosticsFragmentList f = new DiagnosticsFragmentList();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }

    public  DiagnosticsFragmentList()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.list_fragment , container, false);

        asyncService = AsyncApp42ServiceApi.instance(getActivity());
        mProvider = new DrawableProvider(getActivity());


        docId = getArguments().getString("id", "");

        //sharedpreferences = getSharedPreferences("diagnosticos", Context.MODE_PRIVATE);
        // temporal
        progressDialog = ProgressDialog.show(getActivity(), "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByKeyValue(Constants.App42DBName, "diagnosticos", "id_creator", docId, this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main1, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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
                DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(getActivity(), 0, true, convertList);
                listDianostic.setAdapter(diagnosticsAdapter);
                listDianostic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(convertList.get(position).getId(), convertList.get(position).getSchema());
                        editor.commit();
                        createAlertDialog("Descarga finalizada");
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
        convertList = new ArrayList<>();
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
        DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(getActivity(), 0, convertList);
        setListAdapter(diagnosticsAdapter);
        getListView().setOnItemClickListener(this);
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
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        alertbox.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", convertList.get(position).getId());
        bundle.putString("diagnostico", convertList.get(position).getSchema());

        Fragment fragment = new TestFragment().newInstance(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.display,fragment);
        transaction.commit();

        AsignatureActivity.fab.setVisibility(View.INVISIBLE);

    }
}
