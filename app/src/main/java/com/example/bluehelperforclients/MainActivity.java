package com.example.bluehelperforclients;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_LOCATION_PERMISSION = 2;

    private TextView startPoint;
    private TextView endPoint;
    private TextView currentBeaconLabel;
    private ListView beaconListView;
    private Button createRoute;
    private CheckBox discoveryCheckBox;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private SharedPreferences sharedPreferences;
    private boolean canUseTTS = false;
    private TextToSpeech tts;
    private Timer beaconTimeoutTimer = new Timer(true);
    private Map<String, BeaconInfo> beaconInfos = new HashMap<>();
    private BeaconInfo nearestBeaconInfo;
    private ArrayList<Point> points = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        startPoint = findViewById(R.id.textView);
        endPoint = findViewById(R.id.textView2);
        beaconListView = findViewById(R.id.beaconListView);
        discoveryCheckBox = findViewById(R.id.discoveryCheckBox);
        currentBeaconLabel.setVisibility(View.INVISIBLE);
        currentBeaconLabel = findViewById(R.id.currentBeaconLabel);
        beaconListView.setAdapter(beaconListAdapter);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                canUseTTS = true;
            }
        });
        getBluetoothAdapter();

        discoveryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if ((bluetoothAdapter == null) || !bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        return;
                    }
                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_LOCATION_PERMISSION);
                    }
                    startDiscovery();
                } else {
                    stopDiscovery();
                }
            }
        });

        beaconTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        Iterator<Map.Entry<String, MainActivity.BeaconInfo>> it = beaconInfos.entrySet().iterator();
                        boolean somethingChanged = false;
                        while (it.hasNext()) {
                            Map.Entry<String, MainActivity.BeaconInfo> entry = it.next();
                            MainActivity.BeaconInfo beaconInfo = entry.getValue();
                            if ((curTime - beaconInfo.lastSeen) > 5000) {
                                it.remove();
                                if (beaconInfo == nearestBeaconInfo) {
                                    nearestBeaconInfo = null;
                                    currentBeaconLabel.setVisibility(View.INVISIBLE);
                                }
                                somethingChanged = true;
                            }
                        }
                        if (somethingChanged) {
                            beaconListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        stopDiscovery();
        beaconTimeoutTimer.purge();
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (getBluetoothAdapter()) {
                    startDiscovery();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery();
                } else {
                    Toast toast = Toast.makeText(this, "Cannot perform bluetooth scan " +
                                    "without coarse location permission",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    discoveryCheckBox.setChecked(false);
                }
                break;
        }
    }

    private boolean getBluetoothAdapter() {
        if (bluetoothAdapter != null) return true;
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null) {
                return true;
            }
        }
        Toast toast = Toast.makeText(this, "Failed to enable Bluetooth!",
                Toast.LENGTH_LONG);
        toast.show();
        discoveryCheckBox.setChecked(false);
        return false;
    }

    private void startDiscovery() {
        if (bluetoothAdapter != null) {
            discoveryCheckBox.setChecked(true);
            setProgressBarIndeterminateVisibility(Boolean.TRUE);
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(
                    null,
                    new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build(),
                    scanCallback
            );
            Toast toast = Toast.makeText(this, "Discovery started...",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void stopDiscovery() {
        discoveryCheckBox.setChecked(false);
        setProgressBarIndeterminateVisibility(Boolean.FALSE);
        if (bluetoothAdapter != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private void processScanResult(ScanResult result) {
        final String address = result.getDevice().getAddress();
        final int rssi = result.getRssi();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String checked = MainActivity.this.checkPoint(beaconInfos);

                if (!checked.isEmpty()) {
                    tts.speak(checked, TextToSpeech.QUEUE_FLUSH, null);
                    currentBeaconLabel.setVisibility(View.VISIBLE);
                    currentBeaconLabel.setText(checked);
                } else {
                    currentBeaconLabel.setText("");
                }

                MainActivity.BeaconInfo beaconInfo = beaconInfos.get(address);
                if (beaconInfo == null) {
                    beaconInfo = new MainActivity.BeaconInfo(address, rssi);
                    beaconInfos.put(beaconInfo.address, beaconInfo);
                    beaconInfo.title = sharedPreferences.getString(beaconInfo.address, null);
                } else {
                    beaconInfo.setRssi(rssi);
                }
                beaconInfo.lastSeen = System.currentTimeMillis();
                beaconListAdapter.notifyDataSetChanged();
                if ((beaconInfo.rssi > -50) || (beaconInfo.rssi > -60) && (beaconInfo == nearestBeaconInfo)) {
                    if ((nearestBeaconInfo == null) || ((nearestBeaconInfo.rssi <= beaconInfo.rssi)) ||
                            (nearestBeaconInfo == beaconInfo)) {
                        if ((nearestBeaconInfo != beaconInfo) && (beaconInfo.title != null) && canUseTTS) {
                            tts.speak(beaconInfo.title, TextToSpeech.QUEUE_FLUSH, null);
                        }
                        nearestBeaconInfo = beaconInfo;
                        currentBeaconLabel.setText((beaconInfo.title != null) ?
                                beaconInfo.title : beaconInfo.address);
                        currentBeaconLabel.setVisibility(View.VISIBLE);
                    }
                } else if (nearestBeaconInfo == beaconInfo) {
                    nearestBeaconInfo = null;
                    currentBeaconLabel.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public String checkPoint(Map<String, MainActivity.BeaconInfo> beaconInfos) {
        for (int i = 0; i < this.points.size(); i++) {
            Point point = this.points.get(i);

            int count = 0;

            for (Map.Entry<String, String> entry : point.beacons.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                MainActivity.BeaconInfo beacon = beaconInfos.get(key);

                if (beacon != null) {
                    int rssi = Integer.parseInt(value);

                    if (beacon.rssi >= rssi - 2 && beacon.rssi <= rssi + 1) {
                        count++;
                    }
                }
            }

            if (count == 3) {
                return point.name;
            }

        }
        return "";
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult scanResult : results) {
                processScanResult(scanResult);
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "Scan error: " + errorCode, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    };

    private final BaseAdapter beaconListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return beaconInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return beaconInfos.values().toArray(new MainActivity.BeaconInfo[0])[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.MainListHolder mainListHolder;
            if (convertView == null) {
                mainListHolder = new MainActivity.MainListHolder();
                convertView = View.inflate(MainActivity.this, android.R.layout.two_line_list_item, null);
                mainListHolder.line1 = convertView.findViewById(android.R.id.text1);
                mainListHolder.line2 = convertView.findViewById(android.R.id.text2);
                convertView.setTag(mainListHolder);

            } else {
                mainListHolder = (MainActivity.MainListHolder) convertView.getTag();
            }
            MainActivity.BeaconInfo beaconInfo = (MainActivity.BeaconInfo) getItem(position);
            mainListHolder.line1.setText((beaconInfo.title != null) ?
                    beaconInfo.title : beaconInfo.address);
            mainListHolder.line2.setText(beaconInfo.rssi + " dbi, " + beaconInfo.lastSeen);
            return convertView;
        }
    };

    private static class BeaconInfo implements Serializable {
        final String address;
        String title;
        int rssi;
        long lastSeen;
        private ArrayList<Integer> rssiArray = new ArrayList<>();

        BeaconInfo(String address, int rssi) {
            this.address = address;
            this.rssi = rssi;
            this.setRssi(rssi);
        }

        public void setRssi(int rssi) {
            if (this.rssiArray.size() < 15) {
                this.rssiArray.add(rssi);
            } else {
                ArrayList<Integer> tempArray = new ArrayList<>();
                for (int i = 1; i < this.rssiArray.size(); i++) {
                    tempArray.add(this.rssiArray.get(i));
                }
                tempArray.add(rssi);
                this.rssiArray = tempArray;
            }

            this.rssi = this.getRssi();
        }

        public int getRssi() {
            int sum = 0;
            for (int i = 0; i < this.rssiArray.size(); i++) {
                sum += this.rssiArray.get(i);
            }

            return sum / this.rssiArray.size();
        }

    }

    private static class MainListHolder {
        private TextView line1;
        private TextView line2;
    }
}