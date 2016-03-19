package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.tecnologiajo.diagnostictestsuniajc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener{

    public boolean seleccion,finalizar;
    public ViewAnimator viewAnimator;
    public TextView timeTest,textquestion,Q1,Q2,Q3,Q4;
    public RelativeLayout relativeLayout;
    Animation slide_in_left, slide_out_right;
    /** The async service. */
    private AsyncApp42ServiceApi asyncService;
    /** The progress dialog. */
    private ProgressDialog progressDialog;
    /** The doc id. */
    private String docId = "56e0c3cae4b0e89150c63cba";
    /** resive el documento json devuelto del servicio */
    private String docDocument="";
    /** json array para el grupo de respuestas */
    JSONArray jsonArray;
    JSONArray jsonArrayResult;
    JSONObject jsonDiagnostico;
    JSONObject jsonObject1;
    /** contador para las preguntas */
    private int next;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        textquestion =(TextView) findViewById(R.id.textquestion);
        Q1 =(TextView) findViewById(R.id.txtQ1);
        Q2 =(TextView) findViewById(R.id.txtQ2);
        Q3 =(TextView) findViewById(R.id.txtQ3);
        Q4 =(TextView) findViewById(R.id.txtQ4);


        timeTest = (TextView) findViewById(R.id.timeTest);
        viewAnimator = (ViewAnimator)findViewById(R.id.viewanimator);

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(slide_in_left);
        viewAnimator.setOutAnimation(slide_out_right);
        seleccion=false;
        finalizar=false;
        next=0;
        asyncService = AsyncApp42ServiceApi.instance(this);
        // temporal
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByDocId(Constants.App42DBName, "diagnosticos", docId, this);
        // definir primera pregunta del test

        //relativeLayout.addView(cardview);
        jsonArrayResult = new JSONArray();

    }

    public void sincronizatedTest(){
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTest.setText(String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish() {
                viewAnimator.showNext();
            }
        }.start();
    }
    /** recorre el documento json extraido del servicio  y activa el cronometro */
    public void asignarPreguntas(){
        try {
            sincronizatedTest();
            jsonObject1  = jsonArray.getJSONObject(next);
            textquestion.setText(jsonObject1.getString("descripcion"));
            JSONObject jsonObject2  = new JSONObject(jsonObject1.getString("respuesta1"));
            JSONObject jsonObject3  = new JSONObject(jsonObject1.getString("respuesta2"));
            JSONObject jsonObject4  = new JSONObject(jsonObject1.getString("respuesta3"));
            JSONObject jsonObject5  = new JSONObject(jsonObject1.getString("respuesta4"));
            Q1.setText(jsonObject2.getString("descripcion"));
            Q2.setText(jsonObject3.getString("descripcion"));
            Q3.setText(jsonObject4.getString("descripcion"));
            Q4.setText(jsonObject5.getString("descripcion"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** pasa a la siguiere pregunta */
    public void nextQuestion(View view){
        if(next<(jsonArray.length()-1)) {
            next++;
            jsonArrayResult.put(jsonObject1);
            asignarPreguntas();
            viewAnimator.showNext();
        }else{
            try {
                jsonDiagnostico.put("preguntas", jsonArrayResult);
                createAlertDialog("Exception Occurred : " + jsonDiagnostico.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer1(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta1"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){

            }
            jsonObject1.put("respuesta1", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer2(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta2"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){

            }
            jsonObject1.put("respuesta2", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer3(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta3"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){

            }
            jsonObject1.put("respuesta3", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 1 */
    public void Answer4(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta4"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){

            }
            jsonObject1.put("respuesta4", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {

        docDocument=response.getJsonDocList().get(0).getJsonDoc();
        try {
            jsonDiagnostico = new JSONObject(docDocument);
            jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        asignarPreguntas();
        progressDialog.dismiss();
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
    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(TestActivity.this);
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
