package com.ceylonapz.myfile.view.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ceylonapz.myfile.R;
import com.ceylonapz.myfile.databinding.ActivityMainBinding;
import com.ceylonapz.myfile.utility.AppPermission;
import com.ceylonapz.myfile.utility.Util;
import com.ceylonapz.myfile.viewmodel.MainViewModel;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private String path;
    private static final String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int INITIAL_REQUEST = 1220;
    private static final int REQUEST_WRITE_STORAGE = INITIAL_REQUEST + 4;
    private AppPermission permission;
    private MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBinding();

        getAppPermissions();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void getAppPermissions() {
        permission = new AppPermission();

        if (!permission.canAccessWriteStorage(this) || !permission.canAccessReadStorage(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }
    }

    private void initDataBinding() {
        ActivityMainBinding activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        activityBinding.setMainVM(mainViewModel);

        //open file browser
        mainViewModel.openFileBrowser().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {

                if (value) {
                    Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    mediaIntent.setType("*/*"); //set mime type as per requirement
                    startActivityForResult(mediaIntent, 1001);
                }
            }
        });

        //send file bt
        mainViewModel.sendFileBt().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {

                if (value) {
                    sendViaBluetooth();
                }
            }
        });

        //open all devices
        mainViewModel.getOpenDevices().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {

                if (value) {
                    startActivity(new Intent(MainActivity.this, LiveActivity.class));
                }
            }
        });

        mainViewModel.getSelectedFile().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                ((TextView) findViewById(R.id.txt_fileName)).setText(value);
            }
        });


    }

    public void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    public void sendViaBluetooth() {
        if (path == null) {
            Toast.makeText(this, "Please select file first", Toast.LENGTH_SHORT).show();
            return;
        }


        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] perm, int[] grantResults) {

        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (permission.canAccessWriteStorage(getApplicationContext())) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            } else {
                Toast.makeText(this, "The app was not allowed to write to your storage. " +
                        "Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");

            File f = new File(path);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, "Bluetooth havn't been found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        } else if (requestCode == 1001
                && resultCode == Activity.RESULT_OK) {
            Uri uriPath = data.getData();

            path = Util.getSelectedFilePath(this, uriPath);// "/mnt/sdcard/FileName.mp3"
            mainViewModel.setSelectedFile("Selected File \n" + path);

        } else {
            Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG)
                    .show();
        }
    }


}