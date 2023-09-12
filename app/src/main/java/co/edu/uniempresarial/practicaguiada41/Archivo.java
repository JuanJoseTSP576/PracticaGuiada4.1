package co.edu.uniempresarial.practicaguiada41;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Archivo {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final String DIRECTORY_NAME = "DirectorioArchivo";

    private final Context context;
    private final Activity activity;

    public Archivo(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void saveFile(String fileName, String content) {
        File directory = getDirectory();

        if (directory == null) {
            return;
        }

        writeFile(directory, fileName, content);
    }

    private File getDirectory() {
        if (!hasWritePermission()) {
            requestWritePermission();
            return null;
        }

        return createOrGetDirectory();
    }

    private void writeFile(File directory, String fileName, String content) {
        try {
            File file = new File(directory, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(content);
            writer.flush();
            writer.close();

            Toast.makeText(context, "El archivo " + fileName + " se ha guardado en: " + directory, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.i("Archivo", e.getMessage());
            Toast.makeText(context, "Hubo un error al guardar el archivo", Toast.LENGTH_LONG).show();
        }
    }

    private File createOrGetDirectory() {
        File directory = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                ? new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME)
                : new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), DIRECTORY_NAME);

        if (!directory.exists()) {
            directory.mkdir();
        }

        return directory;
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    private boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
