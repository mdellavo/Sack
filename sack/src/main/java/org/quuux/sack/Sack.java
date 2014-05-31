package org.quuux.sack;

import android.support.v4.util.AtomicFile;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Sack<T extends Entity> {

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

    public void load(Listener<T> listener) {

        final AtomicFile file = new AtomicFile(mPath);

        try {
            T obj = mGson.fromJson(new String(file.readFully()), mClass);
            listener.onResult(Status.SUCCESS, obj);
        } catch (IOException e) {
            listener.onResult(Status.ERROR, null);
        }
    }

    public void commit(final T obj, final Listener<T> listener) {
        final AtomicFile file = new AtomicFile(mPath);
        FileOutputStream str = null;
        try {
            str = file.startWrite();
            final BufferedOutputStream out = new BufferedOutputStream(str);
            out.write(mGson.toJson(obj).getBytes());
            out.flush();
            out.close();
            str.flush();
            str.close();
            file.finishWrite(str);
            listener.onResult(Status.SUCCESS, obj);
        } catch (IOException e) {
            if (str != null)
                file.failWrite(str);
            listener.onResult(Status.ERROR, obj);
        }
    }

    public static <T extends Entity> Sack<T> open(final Class<T> entity, final File path) {
        return new Sack<T>(entity, path);
    }
}
