package edu.psu.jjb24.factorprimes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements FactoringTask.OnResult{
    FactoringTask backgroundTask = null;
    BigInteger semiPrime;  // If this is non-null, that means that there is a suspended AsyncTask that we need to restart
    BigInteger lastTested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStart).setOnClickListener(v -> factor(v));


        if (savedInstanceState != null && savedInstanceState.getBoolean("isFactoring")) {
            lastTested = new BigInteger(savedInstanceState.getString("lastTested"));
            semiPrime = new BigInteger(savedInstanceState.getString("semiprime"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (semiPrime != null) {
            backgroundTask = new FactoringTask(semiPrime, this);
            backgroundTask.execute(lastTested);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        if (backgroundTask != null) {
            savedState.putBoolean("isFactoring", true);
            savedState.putString("semiprime", semiPrime.toString());
            savedState.putString("lastTested", lastTested.toString());
        }
        else {
            savedState.putBoolean("isFactoring", false);
        }
    }

    private void factor(View v) {
        if (backgroundTask != null) {
            backgroundTask.cancel(false);
        }
        semiPrime = new BigInteger(((EditText) findViewById(R.id.etNumber)).getText().toString());

        backgroundTask = new FactoringTask(semiPrime, this);
        backgroundTask.execute();
    }

    @Override
    public void reportProgress (BigInteger lastTested){
        this.lastTested = lastTested;
        ((TextView) findViewById(R.id.txtProgress)).setText("Last Tested:\n" + lastTested.toString());
    }

    @Override
    public void foundFactor (BigInteger factor){
        ((TextView) findViewById(R.id.txtProgress)).setText("FACTORED!!!\n" + factor.toString());
        backgroundTask = null;
    }
}