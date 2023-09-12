package co.edu.uniempresarial.practicaguiada41;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;

    // Android Version
    private TextView versionAndroid;
    private int versionSDK;

    // Battery
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
    private IntentFilter batteryFilter;

    // Camera
    private CameraManager cameraManager;
    private String cameraId;
    private Button btnOnLight;
    private Button btnOffLight;

    // File
    private EditText nameFile;
    private Archivo archivo;
    private ImageButton btnSaveFile;

    // Connection
    private TextView tvConnection;
    private ConnectivityManager connectionManager;

    // Bluetooth
    private BluetoothAdapter mBluetoothAdapter;
    private Button btnOnBluetooth;
    private Button btnOffBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();

        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryFilter);
    }

    private void initializeViews() {
        context = getApplicationContext();
        activity = this;
        versionAndroid = findViewById(R.id.tvVersionAndroid);
        pbLevelBattery = findViewById(R.id.pbLevelBattery);
        tvLevelBattery = findViewById(R.id.tvLevelBatteryLB);
        nameFile = findViewById(R.id.etNameFile);
        tvConnection = findViewById(R.id.tvConexion);
        btnOnLight = findViewById(R.id.btnOn);
        btnOffLight = findViewById(R.id.btnOff);
        btnOnBluetooth = findViewById(R.id.btnOnBluetooth);
        btnOffBluetooth = findViewById(R.id.btnOffBluetooth);
        btnSaveFile = findViewById(R.id.btnSaveFile);
        archivo = new Archivo(context, activity);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setupListeners() {
        btnOnLight.setOnClickListener(this::turnOnLight);
        btnOffLight.setOnClickListener(this::turnOffLight);
        btnSaveFile.setOnClickListener(this::saveFileOnClick);
        btnOnBluetooth.setOnClickListener(this::enableBluetooth);
        btnOffBluetooth.setOnClickListener(this::disableBluetooth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayAndroidVersion();
        checkInternetConnection();
    }

    private void displayAndroidVersion() {
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("La versión de este Android es: " + versionSO + ", La version del SDK es: " + versionSDK);
    }

    private void turnOnLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnOffLight(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBattery.setProgress(levelBattery);
            tvLevelBattery.setText("3. El Nivel de la bateria está en: " + levelBattery + "%");
        }
    };

    private void checkInternetConnection() {
        connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectionManager.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();
        tvConnection.setText(isConnected ? "Hay conexión a internet, el tipo de conexión es: " + network.getTypeName() : "No hay conexión a internet");
    }

    private void saveFileOnClick(View view) {
        String fileName = nameFile.getText().toString().trim();

        if (fileName.isEmpty()) {
            Toast.makeText(context, "El archivo debe tener un nombre, por favor escriba el nombre del archivo", Toast.LENGTH_SHORT).show();
        } else {
            String information = "Información del archivo";
            archivo.saveFile(fileName, information);
        }
    }

    private void disableBluetooth(View view) {
        if (mBluetoothAdapter == null) {
            showToast("El dispositivo no soporta Bluetooth");
        } else if (mBluetoothAdapter.isEnabled()) {
            try {
                mBluetoothAdapter.disable();
                showToast("Bluetooth ha sido desactivado");
            } catch (SecurityException e) {
                showToast("No hay permiso para desactivar el Bluetooth");
            }
        }
    }
    private void enableBluetooth(View view) {
        if (mBluetoothAdapter == null) {
            showToast("El dispositivo no soporta Bluetooth");
        } else if (!mBluetoothAdapter.isEnabled()) {
            try {
                mBluetoothAdapter.enable();
                showToast("Bluetooth ha sido activado");
            } catch (SecurityException e) {
                showToast("No hay permiso para activar el Bluetooth");
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
