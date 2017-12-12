package com.example.kayhan.automation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;


public class Room_Activity extends ActionBarActivity {

    boolean light1On=false;
    boolean light2On = false;
    ImageButton light1,light2;
    TextView fanTextView;
    ImageButton fan;
    boolean fanOn = false;
    ImageView spotLight1,spotLight2;
    BluetoothAdapter bluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 1;
    ListView listView;
    ArrayAdapter<String> adapter;
    public static BluetoothDevice bluetoothDevice = null;
    OutputStream outputStream;
    BroadcastReceiver receiver = null;
    ArrayList<BluetoothDevice> devices =  new ArrayList<BluetoothDevice>();
    Room currentRoom;
    int fanSpeed = 1;
    ImageView level1,level2,level3,level4,level5;
    ImageView [] levels = new ImageView[9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_);
        getSupportActionBar().hide();
        light1=(ImageButton)findViewById(R.id.light1_imageButton);
        light2=(ImageButton)findViewById(R.id.light2_imageButton);
        fan = (ImageButton)findViewById(R.id.fan);
        level1 = (ImageView)findViewById(R.id.level1);
        level2 = (ImageView)findViewById(R.id.level2);
        level3 = (ImageView)findViewById(R.id.level3);
        level4 = (ImageView)findViewById(R.id.level4);
        level5 = (ImageView)findViewById(R.id.level5);
        for(int i=0,k=1;i<9;i++,k++){
            int id = getResources().getIdentifier("level"+k,"id",getPackageName());
            levels[i] = (ImageView)findViewById(id);
        }


        fanTextView = (TextView)findViewById(R.id.fan_textView);
        spotLight1 = (ImageView)findViewById(R.id.spotlight_left);
        spotLight2 = (ImageView) findViewById(R.id.spotlight_right);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = (ListView)findViewById(R.id.listView);


        adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                try {
                    bluetoothDevice = devices.get(position);
                    ParcelUuid [] uuids = bluetoothDevice.getUuids();
                    BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();
                    outputStream = socket.getOutputStream();
//                    listView.setVisibility(View.GONE);
                    Toast.makeText(view.getContext(),"Connected to "+bluetoothDevice.getAddress(),Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        if(bluetoothAdapter == null){
            Toast.makeText(getBaseContext(),"No Bluetooth support on this device",Toast.LENGTH_LONG).show();
        }else if(!bluetoothAdapter.isEnabled()){
            Intent blueIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueIntent,REQUEST_ENABLE_BT);
        }

        if(bluetoothAdapter.isEnabled()){
//            updatePaired();
            receiver  = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // Add the name and address to an array adapter to show in a ListView
                        adapter.add(device.getName() + "\n" + device.getAddress());
                        devices.add(device);

                    }

                }
            };
            discover((Button) findViewById(R.id.discover_button));






        }

        light1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(light1On){
                    light1.setBackgroundResource(R.drawable.light_left_off);
                    spotLight1.setVisibility(View.INVISIBLE);
                    light1On=false;

                    sendData(66);
                }else if(!light1On){
                    light1.setBackgroundResource(R.drawable.light_left_on);
                    spotLight1.setVisibility(View.VISIBLE);
                    light1On=true;
                    sendData(65);
                }
            }
        });
        light2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(light2On){
                    light2.setBackgroundResource(R.drawable.light_right_off);
                    spotLight2.setVisibility(View.INVISIBLE);
                    light2On=false;
                    sendData(66);
                }else if(!light2On){
                    light2.setBackgroundResource(R.drawable.light_right_on);
                    spotLight2.setVisibility(View.VISIBLE);
                    light2On=true;
                    sendData(65);
                }
            }
        });

        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fanOn){
                    fanTextView.setText("Fan Off");
                    fanOn=false;
                    sendData(68);
                    fan.setBackgroundResource(R.drawable.fan);
                }else{
                    fanTextView.setText("Fan On");
                    fanOn=true;
                    sendData(67);
                    fan.setBackgroundResource(R.drawable.fan_on);
                }
            }
        });
        findViewById(R.id.fan_high).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeedUp();
            }
        });
        findViewById(R.id.fan_low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeedDown();
            }
        });

    }

    public void onLevelClicked(View view){
        if(view.getId()==R.id.level)
            setSpeedUp();
        else if(view.getVisibility()==View.VISIBLE){
            setSpeedDown();
        }
    }



    private void setSpeedUp(){
        if (fanOn && fanSpeed < 9 && fanSpeed > 0) {
            levels[fanSpeed].setVisibility(View.VISIBLE);
            fanSpeed++;
            sendData(String.valueOf(fanSpeed));


        }
    }
    private void setSpeedDown(){
        if (fanOn && fanSpeed > 1 && fanSpeed > 0) {
            levels[fanSpeed-1].setVisibility(View.INVISIBLE);
            fanSpeed--;
            sendData(String.valueOf(fanSpeed));


        }
    }

    private void sendData(String s) {
        try {
            if(outputStream!=null){
            outputStream.write(s.getBytes());
            char c = '\n';
            outputStream.write(c);}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendData(int s) {
        try {
            if(outputStream!=null){
                outputStream.write(s);
//                char c = '\n';
//                outputStream.write(c);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showListOnClick(View view){
        if(((ListView)findViewById(R.id.listView)).getVisibility()==View.VISIBLE){
            ((Button)view).setText("Hide List");
            listView.setVisibility(View.GONE);
        }else {
            ((Button)view).setText("Show List");
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void discover(View view){
        if(bluetoothAdapter!=null&&bluetoothAdapter.isEnabled()){
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
            bluetoothAdapter.startDiscovery();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if(receiver!=null) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(bluetoothAdapter.isEnabled()){

            }
        }
    }

    public void updatePaired(){
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device: devices){
            adapter.add(device.getName()+"\n"+device.getAddress());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
