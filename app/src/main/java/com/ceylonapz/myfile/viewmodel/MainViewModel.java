package com.ceylonapz.myfile.viewmodel;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class MainViewModel extends AndroidViewModel {

    private final Context context;
    private MutableLiveData<Boolean> isOpenFiles;
    private MutableLiveData<Boolean> sendFile;
    private MutableLiveData<Boolean> openDevices;
    private MutableLiveData<String> selectedFile;

    public MainViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        inits();
    }

    private void inits() {
        isOpenFiles = new MutableLiveData<>();
        sendFile = new MutableLiveData<>();
        openDevices = new MutableLiveData<>();
        selectedFile = new MutableLiveData<>();
        setSelectedFile("Please select file");
    }

    public MutableLiveData<Boolean> openFileBrowser() {
        return isOpenFiles;
    }

    public MutableLiveData<Boolean> sendFileBt() {
        return sendFile;
    }

    public MutableLiveData<Boolean> getOpenDevices() {
        return openDevices;
    }

    public MutableLiveData<String> getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(String path) {
        selectedFile.setValue(path);
    }


    //select an image file
    public View.OnClickListener selectFile() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenFiles.setValue(true);
            }
        };
    }

    //send file
    public View.OnClickListener sendFile() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile.setValue(true);
            }
        };
    }

    //open list of devices
    public View.OnClickListener getDevices() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDevices.setValue(true);
            }
        };
    }


}