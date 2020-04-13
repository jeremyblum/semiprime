package edu.psu.jjb24.factorprimes;

import android.os.AsyncTask;
import android.util.Log;

import java.math.BigInteger;
import java.util.Random;

public class FactoringTask extends AsyncTask<BigInteger, BigInteger, BigInteger> {

    public interface OnResult {
        void reportProgress(BigInteger lastTested);
        void foundFactor(BigInteger factor);
    }

    private static final String TAG = "Factoring Task";

    private final BigInteger semiprime;
    private BigInteger lastTested;
    private OnResult listener;

    public FactoringTask(BigInteger semiprime, OnResult listener) {
        this.semiprime = semiprime;
        this.listener = listener;
    }

    public BigInteger getSemiPrime() {
        return semiprime;
    }

    // Q: How do we prevent thread interference?
    private synchronized void setLastTested(BigInteger lastTested) {
        this.lastTested = lastTested;
    }
    public synchronized BigInteger getLastTested() {
        return lastTested;
    }

    @Override
    protected BigInteger doInBackground(BigInteger... params) {
        Log.d(TAG, "doInBackground started");

        BigInteger factor;

        if (params.length != 0) {
            factor = params[0];
        }
        else {
            factor = BigInteger.probablePrime(semiprime.bitLength()/2, new Random());
        }

        while (!semiprime.mod(factor).equals(BigInteger.ZERO)) {
            setLastTested(factor);

            publishProgress(factor);

            if (isCancelled()) {
                Log.d(TAG, "doInBackground determined cancelled");
                return null;
            }
            factor = factor.nextProbablePrime();
        }
        return factor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute called");
    }

    @Override
    protected void onProgressUpdate(BigInteger... values) {
        Log.v(TAG, "publishing progress" + values[0]);
        listener.reportProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "onCancelled called");
    }

    @Override
    protected void onPostExecute(BigInteger factor) {
        Log.d(TAG, "onPostExecute called with result" + factor);

        listener.foundFactor(factor);
    }
}


