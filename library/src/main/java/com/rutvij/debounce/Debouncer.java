package com.rutvij.debounce;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;


/**
 * Debouncer provides facilities to debouce execution of runnables.
 *
 * <p>Created by Rutvijkumar Shah.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class Debouncer {

    /**
     * Default delay time when not provided in debouce call.
     */
    private static final long DEFAULT_DELAY_MILLIS = 250L;

    /**
     * Singleton instance.
     */
    private static Debouncer instance;

    /**
     * Default delay time in millis.
     *
     */
    private long defaultDelayTimeMillis = DEFAULT_DELAY_MILLIS;

    /**
     * Default hanlder, by default all runnable will be executed on main thread.
     */
    private Handler mainHandler;

    /**
     * Wrorkers runnables that keep tracks of each worker added to debcounce execution.
     */
    private Map<String, Worker> runnables;

    /**
     * Singleton constructor.
     */
    private Debouncer() {
        runnables = new HashMap<>();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * @return instance of Debouncer.
     */
    @MainThread
    public static Debouncer getInstance() {
        if (instance == null) {
            instance = new Debouncer();
        }
        return instance;
    }

    /**
     * Sets default delay time, this value will be used when delayTime is not passed in debouce.
     * Make sure to set default delay time before calling getInstnace().
     *
     * @param delayTime Default delayTime
     */
    @MainThread
    public static void setDefaultDelayTime(final long delayTime) {
        if (instance != null) {
            throw new IllegalArgumentException("setDefaultDelayTime should be called before getInstance()");
        } else {
            getInstance().defaultDelayTimeMillis = delayTime;
        }
    }

    /**
     * @param key         identifier key for the runnable.
     * @param runnable    runnable to be executed with debounced behavior.
     * @param delayMillis delay milliseconds
     */
    public void debounce(@NonNull final String key, @NonNull final Runnable runnable, final long delayMillis) {
        debounce(key, runnable, delayMillis, mainHandler);
    }

    /**
     * Debounces execution of given runnable.
     *
     * @param key      identifier key for the runnable.
     * @param runnable runnable to be executed with debounced behavior.
     */
    public void debounce(@NonNull final String key, @NonNull final Runnable runnable) {
        debounce(key, runnable, defaultDelayTimeMillis, mainHandler);
    }

    /**
     * Debounces execution of given runnable.
     *
     * @param key         identifier key for the runnable.
     * @param runnable    runnable to be executed with debounced behavior.
     * @param delayMillis delay milliseconds
     * @param handler     Handler on which debounced runnable will be executed.
     */
    public void debounce(@NonNull final String key, @NonNull final Runnable runnable, final long delayMillis, @NonNull final Handler handler) {
        if (runnables.containsKey(key)) {
            clear(key);
        }
        Worker worker = new Worker(key, runnable, handler);
        runnables.put(key, worker);
        handler.postDelayed(worker, delayMillis);
    }

    /**
     * Clears all debouced runnables.
     * All pending runnables will be removed and will not be executed.
     */
    public void clearAll() {
        for (Map.Entry<String, Worker> entry : runnables.entrySet()) {
            clear(entry.getKey());
        }
    }

    /**
     * Clears specific debouced runnable identified by the key.
     *
     * If runnable is pending it will be removed and will not ge executed.
     * @param key identifier key which was used to add runnable.
     */
    public void clear(@NonNull final String key) {
        if (runnables.containsKey(key)) {
            Worker worker = runnables.get(key);
            worker.getHandler().removeCallbacks(worker);
            runnables.remove(key);
        }
    }


    /**
     * Important: Order of execution of runnable is not garunteed.
     * Do not use this method if order is important, use multiple <code>flush(key);</code> instead in expected order.
     *
     * Immediately execute all pending runnables.
     * After execution each runnable will be removed from the pending list.
     */
    public void flushAll() {
        for (Map.Entry<String, Worker> entry : runnables.entrySet()) {
            flush(entry.getKey());
        }
    }

    /**
     * Immediately execute runnable identified by the key.
     *
     * If runnable is pending it will be executed immediately and removed from the pending list.
     *
     * @param key identifier key which was used to add runnable.
     */
    public void flush(@NonNull final String key) {
        if (runnables.containsKey(key)) {
            Worker worker = runnables.get(key);
            worker.run();
        }
    }

    /**
     * Debounces execution of given runnable.
     *
     * @param key      identifier key for the runnable.
     * @param runnable runnable to be executed with debounced behavior.
     * @param handler  Handler on which debounced runnable will be executed.
     */
    public void debounce(@NonNull final String key, @NonNull final Runnable runnable, @NonNull final Handler handler) {
        debounce(key, runnable, defaultDelayTimeMillis, handler);
    }

    /**
     * Worker object that keeps handler and executable information.
     */
    private class Worker implements Runnable {

        /**
         * actual runnable to be executed.
         */
        private Runnable runnable;

        /**
         * Handler on which runnable to be executed.
         */
        private Handler handler;

        /**
         * identifier key for runnable.
         */
        private String key;

        /**
         * Worker constructor.
         * @param idKey identifier key for runnable.
         * @param targetRunnable runnable.
         * @param workHandler  Handler.
         */
        Worker(final String idKey, final Runnable targetRunnable, final Handler workHandler) {
            this.key = idKey;
            this.runnable = targetRunnable;
            this.handler = workHandler;
        }

        @Override
        public void run() {
            runnables.remove(key);
            handler.post(runnable);
        }

        protected Runnable getRunnable() {
            return runnable;
        }

        protected Handler getHandler() {
            return handler;
        }

        protected String getKey() {
            return key;
        }
    }

    //Methods expose for unit tests.

    @VisibleForTesting
    long getDefaultDelayTimeMillis() {
        return defaultDelayTimeMillis;
    }

    @VisibleForTesting
    Handler getMainHandler() {
        return mainHandler;
    }

    @VisibleForTesting
    Map<String, Worker> getRunnables() {
        return runnables;
    }
}
