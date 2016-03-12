package com.tecnologiajo.diagnostictestsuniajc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

public class MainActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener {
    /** The async service. */
    private AsyncApp42ServiceApi asyncService;
    /** The progress dialog. */
    private ProgressDialog progressDialog;
    /** The doc id. */
    private String docId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        asyncService = AsyncApp42ServiceApi.instance(this);
        // temporal
        progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByKeyValue(Constants.App42DBName, Constants.CollectionName, "nombre", "Matematica 1", this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        createAlertDialog("Document SuccessFully Fetched : "+ response.getJsonDocList().get(0).getJsonDoc());
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
        AlertDialog.Builder alertbox = new AlertDialog.Builder(
                MainActivity.this);
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
