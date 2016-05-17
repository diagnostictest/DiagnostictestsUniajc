package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import com.tecnologiajo.diagnostictestsuniajc.modelos.Diagnosticos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener{

    public boolean seleccion,finalizar;
    public ViewAnimator viewAnimator;
    public TextView timeTest,textquestion,Q1,Q2,Q3,Q4,itempregunta,txtnextlayout;
    public ImageView imgnextlayout;
    public Button btnnextlayout;
    public LinearLayout nextlayout;

    public RelativeLayout relativeLayout;
    Animation slide_in_left, slide_out_right;
    /** The async service. */
    private AsyncApp42ServiceApi asyncService;
    /** The progress dialog. */
    private ProgressDialog progressDialog;
    /** The id . */
    private String Id = "";
    /** The doc id. */
    private String docId = "";
    /** resive el documento json devuelto del servicio */
    private String docDocument="";
    /** The doc id_creator id padre. */
    private String id_creator = "";
    /** Documento asignatura */
    private String diagnostico = "";
    /** json array para el grupo de respuestas */
    JSONArray jsonArray;
    JSONArray jsonArrayResult;
    JSONObject jsonDiagnostico;
    JSONObject jsonObject1;
    /** contador para las preguntas */
    private int next;
    /** Controlador de tiempos */
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        textquestion =(TextView) findViewById(R.id.textquestion);
        Q1 =(TextView) findViewById(R.id.txtQ1);
        Q2 =(TextView) findViewById(R.id.txtQ2);
        Q3 =(TextView) findViewById(R.id.txtQ3);
        Q4 =(TextView) findViewById(R.id.txtQ4);
        itempregunta =(TextView) findViewById(R.id.idpregunta);
        txtnextlayout =(TextView) findViewById(R.id.txtnextlayout);
        timeTest = (TextView) findViewById(R.id.timeTest);

        imgnextlayout = (ImageView) findViewById(R.id.imgnextlayout);
        btnnextlayout = (Button) findViewById(R.id.btnnextlayout);
        nextlayout = (LinearLayout) findViewById(R.id.nextlayout);
        viewAnimator = (ViewAnimator)findViewById(R.id.viewanimator);

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewAnimator.setInAnimation(slide_in_left);
        viewAnimator.setOutAnimation(slide_out_right);

        /** Inicializamos las banderas */
        seleccion=false;
        finalizar=false;
        next=0;
        /** Obtenemos el servicio App42 */
        asyncService = AsyncApp42ServiceApi.instance(this);

        /** Obtenemos el id del diagnostico*/
        docId= getIntent().getExtras().getString("id","");
        diagnostico = getIntent().getExtras().getString("diagnostico","");
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByDocId(Constants.App42DBName, "diagnosticos", docId, this);
        /** definimos la primera pregunta de la prueba **/
        itempregunta.setText("Q"+String.valueOf((next + 1)));
        /** Json para el resultado final **/
        jsonArrayResult = new JSONArray();

    }


    public void sincronizatedTest(){
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTest.setText(String.valueOf((millisUntilFinished/1000)));
            }

            public void onFinish() {
                txtnextlayout.setText("Incorrecto!");
                imgnextlayout.setImageResource(R.drawable.xx);
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
                viewAnimator.showNext();
            }
        }.start();
    }
    /** recorre el documento json extraido del servicio
     *  para esteblecer la pregunta con sus posible
     *  respuestas y activa el cronometro
     * */
    public void assignQuestion(){
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
    /** pasa a la siguientee pregunta */
    public void nextQuestion(View view){
        if(next<(jsonArray.length()-1)) {
            next++;
            itempregunta.setText("Q" + String.valueOf((next + 1)));
            jsonArrayResult.put(jsonObject1);
            assignQuestion();
            viewAnimator.showNext();

        }else{
            try {
                jsonArrayResult.put(jsonObject1);
                jsonDiagnostico.put("preguntas", jsonArrayResult);
                jsonDiagnostico.put("id_usuario","");
                jsonDiagnostico.put("id_creator",id_creator);

                progressDialog = ProgressDialog.show(this, "", "Input..");
                progressDialog.setCancelable(true);
                asyncService.insertJSONDoc(Constants.App42DBName,"historial",jsonDiagnostico,this);
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
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                imgnextlayout.setImageResource(R.drawable.xx);
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta1", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 2 */
    public void Answer2(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta2"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else {
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta2", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 3 */
    public void Answer3(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta3"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
            }
            jsonObject1.put("respuesta3", jsonObject);
            countDownTimer.cancel();
            viewAnimator.showNext();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** selecciona repuesta opcion 4 */
    public void Answer4(View view){
        try {
            JSONObject jsonObject  = new JSONObject(jsonObject1.getString("respuesta4"));
            jsonObject.put("selected", true);
            if(jsonObject.getBoolean("flag")){
                txtnextlayout.setText("Correcto!");
                imgnextlayout.setImageResource(R.drawable.tick);
                nextlayout.setBackgroundColor(Color.parseColor("#FF1A831A"));
            }else{
                txtnextlayout.setText("Incorrecto!");
                nextlayout.setBackgroundColor(Color.parseColor("#FFF20404"));
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
        /*if(response.isFromCache()){
            Diagnosticos diagnosticos= new Diagnosticos(this);
            try {
                jsonDiagnostico.put("registrado",false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            diagnosticos.insertarDIAGNOSTICOS(Id, jsonDiagnostico.toString());
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),RsultActivity.class);
            intent.putExtra("result", jsonDiagnostico.toString());
            startActivity(intent);

        }else{*/
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),RsultActivity.class);
            intent.putExtra("result",jsonDiagnostico.toString());
            intent.putExtra("diagnostico", diagnostico);
            startActivity(intent);
            finish();
        //}

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        //if(response.isFromCache()){
            docDocument=response.getJsonDocList().get(0).getJsonDoc();
            Id = response.getJsonDocList().get(0).getDocId();
            try {
                jsonDiagnostico = new JSONObject(docDocument);
                jsonArray = new JSONArray(jsonDiagnostico.getString("preguntas"));
                id_creator = jsonDiagnostico.getString("id_creator");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assignQuestion();
            progressDialog.dismiss();

        /*}else{
            createAlertDialog("Sin conexion : ");
            progressDialog.dismiss();
        }*/

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : "+ ex.getMessage());
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
