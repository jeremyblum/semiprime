package edu.psu.jjb24.factorprimes;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.math.BigInteger;
import java.util.Random;

public class FactoringTask {
    public interface OnProgressListener {
        void reportProgress(BigInteger lastTested);
    }

    public interface OnResultListener {
        void foundFactor(BigInteger factor);
    }

    private static int PROGRESS_MESSAGE = 1;
    private static int RESULT_MESSAGE = 2;

    // Handler for the main thread
    private Handler mThreadHandler;
    private Thread thread;

    private final BigInteger semiprime; // Semiprime to factor
    private BigInteger lastTested; // Last prime factor that was tested

    // Note: constructor registers the callback object
    public FactoringTask(BigInteger semiprime, BigInteger lastTested, OnResultListener resultListener, OnProgressListener progressListener) {
        this.semiprime = semiprime;
        this.lastTested = lastTested;

        mThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.arg1 == PROGRESS_MESSAGE) {
                    progressListener.reportProgress((BigInteger) msg.obj);
                } else if (msg.arg1 == RESULT_MESSAGE) {
                    resultListener.foundFactor((BigInteger) msg.obj);
                }
            }
        };
    }

    public void execute(){
        thread = new Thread(() -> {
            if (lastTested == null) {
                lastTested = BigInteger.probablePrime(semiprime.bitLength() / 2, new Random());
            }

            while (!semiprime.mod(lastTested).equals(BigInteger.ZERO)) {

                Message msg = mThreadHandler.obtainMessage();
                msg.arg1 = PROGRESS_MESSAGE;
                msg.obj = lastTested;
                mThreadHandler.sendMessage(msg);

                lastTested = lastTested.nextProbablePrime();

                if (Thread.interrupted()) return;
            }
            Message msg = mThreadHandler.obtainMessage();
            msg.arg1 = RESULT_MESSAGE;
            msg.obj = lastTested;
            mThreadHandler.sendMessage(msg);
        });
        thread.start();
    }

    public void cancel() {
        if (thread != null) thread.interrupt();
    }
}




