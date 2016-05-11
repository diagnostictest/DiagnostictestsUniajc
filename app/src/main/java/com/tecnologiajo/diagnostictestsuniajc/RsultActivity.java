package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnostico;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class RsultActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener{
    /** The async service. */
    private AsyncApp42ServiceApi asyncService;
    private static String docResultado="";
    /** The progress dialog. */
    private ProgressDialog progressDialog;
    /** @parameter total Historial Ganadas y perdidas */
    public  int totalHBuenas=0,totalHPerdidas=0,totalHistorial=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsult);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /** obtenemos el resultado de la prueba
         * */
        docResultado= getIntent().getExtras().getString("result","");
        /** Obtenemos el servicio App42 */
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService = AsyncApp42ServiceApi.instance(this);
        try {
            JSONObject jsonDiagnostico = new JSONObject(docResultado);
            asyncService.findDocByKeyValue(Constants.App42DBName, "historial","id_creator",jsonDiagnostico.getString("id_creator"),this);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {


        if(response.isFromCache()){
            ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
            for (int i=0;i<jsonDocList.size();i++){
                String docDocument=response.getJsonDocList().get(i).getJsonDoc();
                boolean estadoRespuesta=false;
                int totalBuenas=0,total=0;
                try {
                    JSONObject jsonDiagnostico = new JSONObject(docDocument);
                    JSONArray jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
                    for(int j=0; j < jsonArray.length();j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        JSONObject jsonObject2  = new JSONObject(jsonObject.getString("respuesta1"));
                        if(jsonObject2.getBoolean("selected")&& jsonObject2.getBoolean("flag")){estadoRespuesta=true;}
                        JSONObject jsonObject3  = new JSONObject(jsonObject.getString("respuesta2"));
                        if(jsonObject3.getBoolean("selected")&& jsonObject3.getBoolean("flag")){estadoRespuesta=true;}
                        JSONObject jsonObject4  = new JSONObject(jsonObject.getString("respuesta3"));
                        if(jsonObject4.getBoolean("selected")&& jsonObject4.getBoolean("flag")){estadoRespuesta=true;}
                        JSONObject jsonObject5  = new JSONObject(jsonObject.getString("respuesta4"));
                        if(jsonObject5.getBoolean("selected")&& jsonObject5.getBoolean("flag")){estadoRespuesta=true;}

                        if(estadoRespuesta){
                            totalBuenas++;
                        }
                        estadoRespuesta=false;
                        total++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int promedio = (totalBuenas*100 /total);
                if(promedio>=50){
                    totalHBuenas++;
                }else{
                    totalHPerdidas++;
                }
                totalHistorial++;
            }

            progressDialog.dismiss();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment detalleResult = DetalleResult.newInstance(totalHBuenas,totalHPerdidas,totalHistorial);
            ft.add(R.id.display, detalleResult, "Detail_Result");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }else{
            createAlertDialog("Sin conexion : ");
            progressDialog.dismiss();
        }

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : "+ ex.getMessage());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    public static class DetalleResult extends Fragment{
        public TextView timeTest,textresult;
        public ImageView iconoResult;
        public RatingBar calificacion;
        private JSONObject jsonDiagnostico;
        public  JSONArray jsonArray;
        private boolean estadoRespuesta=false;
        private int totalBuenas=0,total=0;
        private PieChart pieChart;
        public DetalleResult() {
        }

        public static DetalleResult newInstance(int totalHBuenas,int totalHPerdidas,int totalHistorial) {
            DetalleResult f = new DetalleResult();
            Bundle args = new Bundle();
            args.putInt("totalHBuenas", totalHBuenas);
            args.putInt("totalHPerdidas", totalHPerdidas);
            args.putInt("totalHistorial", totalHistorial);
            f.setArguments(args);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragmen_resultdetalle, container, false);
            textresult =(TextView) view.findViewById(R.id.textResult);
            iconoResult = (ImageView) view.findViewById(R.id.iconresult);
            calificacion = (RatingBar) view.findViewById(R.id.calificacion);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            estadoRespuesta=false;
            try {
                jsonDiagnostico = new JSONObject(docResultado);
                jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                for(int i=0; i < jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Result result = new Result();
                    result.setDescripcion(jsonObject.getString("descripcion"));
                    JSONObject jsonObject2  = new JSONObject(jsonObject.getString("respuesta1"));
                    if(jsonObject2.getBoolean("selected")&& jsonObject2.getBoolean("flag")){estadoRespuesta=true;}
                    JSONObject jsonObject3  = new JSONObject(jsonObject.getString("respuesta2"));
                    if(jsonObject3.getBoolean("selected")&& jsonObject3.getBoolean("flag")){estadoRespuesta=true;}
                    JSONObject jsonObject4  = new JSONObject(jsonObject.getString("respuesta3"));
                    if(jsonObject4.getBoolean("selected")&& jsonObject4.getBoolean("flag")){estadoRespuesta=true;}
                    JSONObject jsonObject5  = new JSONObject(jsonObject.getString("respuesta4"));
                    if(jsonObject5.getBoolean("selected")&& jsonObject5.getBoolean("flag")){estadoRespuesta=true;}
                    result.setEstado(estadoRespuesta);
                    if(estadoRespuesta){
                        totalBuenas++;
                    }
                    estadoRespuesta=false;
                    total++;
                }
                textresult.setText(totalBuenas+"/"+total);
                int promedio = (totalBuenas*100 /total);
                if(promedio>=50 && promedio<100){
                    iconoResult.setImageResource(R.drawable.cup_silver);
                }else if(promedio==100){
                    iconoResult.setImageResource(R.drawable.cup_gold);
                }else{
                    iconoResult.setImageResource(R.drawable.cup_bronze);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pieChart = (PieChart) getView().findViewById(R.id.pieChart);
            int totalHBuenas=0,totalHPerdidas=0;
            float totalHistorial= (float) 0.0;
            totalHBuenas=getArguments().getInt("totalHBuenas");
            totalHPerdidas=getArguments().getInt("totalHPerdidas");
            totalHistorial=getArguments().getInt("totalHistorial");
        /*definimos algunos atributos*/
            pieChart.setHoleRadius(40f);
            pieChart.setDrawYValues(true);
            pieChart.setDrawXValues(true);
            pieChart.setRotationEnabled(true);
            pieChart.animateXY(1500, 1500);

		/*creamos una lista para los valores Y*/
            ArrayList<Entry> valsY = new ArrayList<Entry>();
            valsY.add(new Entry(totalHBuenas * 100 / totalHistorial,0));
            valsY.add(new Entry(totalHPerdidas * 100 / totalHistorial,1));

 		/*creamos una lista para los valores X*/
            ArrayList<String> valsX = new ArrayList<String>();
            valsX.add("Ganadas");
            valsX.add("Perdidas");

 		/*creamos una lista de colores*/
            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(getResources().getColor(R.color.colorAccent));
            colors.add(getResources().getColor(R.color.colorPrimary));

 		/*seteamos los valores de Y y los colores*/
            PieDataSet set1 = new PieDataSet(valsY, "Resultados");
            set1.setSliceSpace(3f);
            set1.setColors(colors);

		/*seteamos los valores de X*/
            PieData data = new PieData(valsX, set1);
            pieChart.setData(data);
            pieChart.highlightValues(null);
            pieChart.invalidate();

        /*Ocutar descripcion*/
            pieChart.setDescription("");
        /*Ocultar leyenda*/
            pieChart.setDrawLegend(false);
        }
    }

    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(RsultActivity.this);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        alertbox.show();
    }

}
