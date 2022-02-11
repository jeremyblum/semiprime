package edu.psu.jjb24.factorprimes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements FactoringTask.OnResultListener, FactoringTask.OnProgressListener {
    FactoringTask backgroundTask = null;
    BigInteger semiPrime;
    BigInteger lastTested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStart).setOnClickListener(this::factor);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isFactoring")) {
            lastTested = new BigInteger(savedInstanceState.getString("lastTested"));
            semiPrime = new BigInteger(savedInstanceState.getString("semiprime"));
            backgroundTask = new FactoringTask(semiPrime, lastTested, this, this);
            backgroundTask.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundTask != null) {
            backgroundTask.cancel();
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
            backgroundTask.cancel();
        }
        semiPrime = new BigInteger(((EditText) findViewById(R.id.etNumber)).getText().toString());

        backgroundTask = new FactoringTask(semiPrime, null, this, this); // Note we use the Activity as callback object
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
        semiPrime = null;
    }
}