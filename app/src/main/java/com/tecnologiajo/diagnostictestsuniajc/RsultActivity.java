package com.tecnologiajo.diagnostictestsuniajc;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.security.KeyStore;
import java.util.ArrayList;

public class RsultActivity extends AppCompatActivity {
    boolean detailPage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsult);


        if(savedInstanceState == null) {
            HistorialResult historialResult = new HistorialResult();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.display, historialResult, "Detail_Histor");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment detalleResult = new DetalleResult();
                    ft.add(R.id.display, detalleResult, "Detail_Result");
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();



            }
        });


    }

    public static class DetalleResult extends Fragment{

        public DetalleResult() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragmen_resultdetalle, container, false);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

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
