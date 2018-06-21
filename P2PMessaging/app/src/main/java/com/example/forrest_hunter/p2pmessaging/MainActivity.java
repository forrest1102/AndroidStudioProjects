package com.example.forrest_hunter.p2pmessaging;

import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button wifiBtn, btBtn, btnDiscover, btnSend;
    ListView peerListView;
    ListView messageChatView;
    TextView connStats;
    TextView connectedTo;
    EditText writeMsg;
    //RecyclerView viewMsg;

    WifiManager wifiManager;
    BluetoothManager bluetoothManager;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    List<String> messageChat = new ArrayList<String>();

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialWork();

        if(wifiManager.isWifiEnabled()){
            wifiBtn.setText("WIFI ON");
        }

        exqListener();
    }

    //Set up all the various buttons and listViews in the GUI
    private void initialWork() {
        wifiBtn = (Button) findViewById(R.id.wifiOnOff);
        btBtn = (Button) findViewById(R.id.btOnOff);
        btnDiscover = (Button) findViewById(R.id.discoverPeers);
        btnSend = (Button) findViewById(R.id.sendButton);

        peerListView = (ListView) findViewById(R.id.peerListView);
        messageChatView = (ListView) findViewById(R.id.messageChatView);
        //viewMsg = (RecyclerView) findViewById(R.id.messageChatRecyclerView);

        connStats = (TextView) findViewById(R.id.connectionStatus);
        connectedTo = (TextView) findViewById(R.id.connectedTo);

        writeMsg = (EditText) findViewById(R.id.writeMsg);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();

        //all of the possible options for intents wanted...
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                peerListView.setAdapter(adapter);
            }

            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                connStats.setText("Host");
                serverClass = new ServerClass(mHandler);
                serverClass.start();
            } else if(wifiP2pInfo.groupFormed){
                connStats.setText("Client");
                clientClass = new ClientClass(groupOwnerAddress, mHandler);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        exqListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    @Override
    protected void onStop(){
        super.onStop();
        try {
            clientClass.socket.close();
            serverClass.serverSocket.close();
            sendReceive.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exqListener(){
        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    wifiBtn.setText("WIFI OFF");
                } else{
                    wifiManager.setWifiEnabled((true));
                    wifiBtn.setText("WIFI ON");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connStats.setText("Discovery Started");
                        }

                    @Override
                    public void onFailure(int i) {
                        connStats.setText("Discovery Startup Failed");
                        }
                });
            }
        });

        peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectedTo.setText("Connected to " + device.deviceName);
                    }

                    @Override
                    public void onFailure(int i) {
                        connectedTo.setText("Unable to Connect" + device.deviceName);
                    }
                });
            }

        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = writeMsg.getText().toString();
                if (sendReceive != null)
                    sendReceive.write(message.getBytes());
            }
        });

    }

    Handler mHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch(msg.what){
                case Strings.MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMessage = new String(readBuffer, 0, msg.arg1);
                    messageChat.add(tempMessage);
                    ArrayAdapter<String> messageChatAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, messageChat);
                    messageChatView.setAdapter(messageChatAdapter);
                    break;
            }
            return true;
        }
    });

}
