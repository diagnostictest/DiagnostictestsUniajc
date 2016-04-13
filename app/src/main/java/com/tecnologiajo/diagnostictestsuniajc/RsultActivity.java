package com.tecnologiajo.diagnostictestsuniajc;

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
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnostico;
import com.tecnologiajo.diagnostictestsuniajc.modelos.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class RsultActivity extends AppCompatActivity {

    private static String docResultado="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsult);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /** obtenemos el resultado de la prueba
         * */
        docResultado= getIntent().getExtras().getString("result","");
        if(savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment detalleResult = new DetalleResult();
            ft.add(R.id.display, detalleResult, "Detail_Result");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                HistorialResult historialResult = new HistorialResult();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.display, historialResult, "Detail_Histor");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    public static class DetalleResult extends Fragment{
        public TextView timeTest,textquestion,Q1,Q2,Q3,Q4,itempregunta,txtnextlayout;
        private ListView listResul;
        private DrawableProvider mProvider;
        private JSONObject jsonDiagnostico;
        public  JSONArray jsonArray;
        private boolean estadoRespuesta=false;
        public DetalleResult() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragmen_resultdetalle, container, false);
            textquestion =(TextView) view.findViewById(R.id.textquestion);
            Q1 =(TextView) view.findViewById(R.id.txtQ1);
            Q2 =(TextView) view.findViewById(R.id.txtQ2);
            Q3 =(TextView) view.findViewById(R.id.txtQ3);
            Q4 =(TextView) view.findViewById(R.id.txtQ4);
            listResul = (ListView) view.findViewById(R.id.listresult);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mProvider = new DrawableProvider(getActivity());
            try {
                jsonDiagnostico = new JSONObject(docResultado);
                jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
                textquestion.setText(jsonDiagnostico.getString("descripcion"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final List<Result> convertList = new ArrayList<>();
            try {
                for(int i=0; i < jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Result result = new Result();
                    result.setDescripcion(jsonObject.getString("descripcion"));
                    JSONObject jsonObject2  = new JSONObject(jsonObject.getString("respuesta1"));
                    if(jsonObject2.getBoolean("selected")){estadoRespuesta=true;}
                    JSONObject jsonObject3  = new JSONObject(jsonObject.getString("respuesta2"));
                    if(jsonObject2.getBoolean("selected")){estadoRespuesta=true;}
                    JSONObject jsonObject4  = new JSONObject(jsonObject.getString("respuesta3"));
                    if(jsonObject2.getBoolean("selected")){estadoRespuesta=true;}
                    JSONObject jsonObject5  = new JSONObject(jsonObject.getString("respuesta4"));
                    if(jsonObject2.getBoolean("selected")){estadoRespuesta=true;}
                    result.setEstado(estadoRespuesta);
                    estadoRespuesta=false;
                    Drawable drawable = mProvider.getRound("¿");
                    result.setDrawable(drawable);
                    convertList.add(result);
                }
                ResultAdapter resultAdapter = new ResultAdapter(getActivity(), 0, convertList);
                listResul.setAdapter(resultAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class HistorialResult extends Fragment{
        private PieChart pieChart;
        //private BarChart
        public HistorialResult() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragmen_resulthistorial, container, false);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            pieChart = (PieChart) getView().findViewById(R.id.pieChart);

        /*definimos algunos atributos*/
            pieChart.setHoleRadius(40f);
            pieChart.setDrawYValues(true);
            pieChart.setDrawXValues(true);
            pieChart.setRotationEnabled(true);
            pieChart.animateXY(1500, 1500);

		/*creamos una lista para los valores Y*/
            ArrayList<Entry> valsY = new ArrayList<Entry>();
            valsY.add(new Entry(5* 100 / 25,0));
            valsY.add(new Entry(20 * 100 / 25,1));

 		/*creamos una lista para los valores X*/
            ArrayList<String> valsX = new ArrayList<String>();
            valsX.add("Varones");
            valsX.add("Mujeres");

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
}
