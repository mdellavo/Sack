package org.quuux.sack;

import android.os.AsyncTask;
import android.support.v4.util.AtomicFile;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Sack<T> {

    public enum Status {
        SUCCESS,
        ERROR
    }

    public interface Listener<T> {
        void onResult(Status status, T obj);
    }

    private final Class<T> mClass;
    private final File mPath;

    private final Gson mGson = new Gson();

    Sack(final Class<T> klass, final File path) {
        mClass = klass;
        mPath = path;
    }

    public Pair<Status, T> doLoad() {

        final AtomicFile file = new AtomicFile(mPath);
        try {
            final FileInputStream in = file.openRead();
            final String s = slurp(new BufferedInputStream(new GZIPInputStream(in)), 4096);
            final Pair<Status, T> rv = new Pair<>(Status.SUCCESS, mGson.fromJson(s, mClass));
            in.close();
            return rv;
        } catch (IOException e) {
            return new Pair<Status, T>(Status.ERROR, null);
        } finally {
        }
    }

    public static String slurp(final InputStream is, final int bufferSize) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try  {
            Reader in = new InputStreamReader(is, "UTF-8");
            for (;;) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        } catch (IOException ex) {
            return null;
        }
        return out.toString();
    }
    public AsyncTask<Void, Void, Pair<Status, T>> load(final Listener<T> listener) {

        final AsyncTask<Void, Void, Pair<Status, T>>  task = new AsyncTask<Void, Void, Pair<Status, T>>() {

            @Override
            protected Pair<Sack.Status, T> doInBackground(final Void... params) {
                return doLoad();
            }

            @Override
            protected void onPostExecute(final Pair<Sack.Status, T> t) {
                if (listener != null)
                    listener.onResult(t.first, t.second);
            }
        };

        task.execute();

        return task;
    }

    public AsyncTask<Void, Void, Pair<Status, T>> load() {
        return load(null);
    }

    public Pair<Status, T> doCommit(final T obj) {
        final AtomicFile file = new AtomicFile(mPath);
        FileOutputStream str = null;
        try {
            str = file.startWrite();
            final BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(str));
            out.write(mGson.toJson(obj).getBytes());
            out.flush();
            out.close();
            str.flush();
            str.close();
            file.finishWrite(str);
            return new Pair<Status, T>(Status.SUCCESS, obj);
        } catch (IOException e) {
            if (str != null)
                file.failWrite(str);
            return new Pair<Status, T>(Status.ERROR, null);
        }
    }

    public AsyncTask<T, Void, Pair<Status, T>> commit(final T obj, final Listener<T> listener) {

        final AsyncTask<T, Void, Pair<Status, T>>  task = new AsyncTask<T, Void, Pair<Status, T>>() {

            @Override
            protected Pair<Sack.Status, T> doInBackground(final T... params) {
                return doCommit(params[0]);
            }

            @Override
            protected void onPostExecute(final Pair<Sack.Status, T> t) {
                if (listener != null)
                    listener.onResult(t.first, t.second);
            }
        };

        task.execute(obj);

        return task;
    }

    public AsyncTask<T, Void, Pair<Status, T>> commit(final T obj) {
        return commit(obj, null);
    }

    public static <T> Sack<T> open(final Class<T> entity, final File path) {
        return new Sack<T>(entity, path);
    }

}
