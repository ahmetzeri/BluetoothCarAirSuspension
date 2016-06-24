/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.d.ata.ahmet.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {
    private static String sendString = null;
    private Button btn3, btn2, btn1, btnN, btnsolsolalt, btnortaust, btnortasolyukari, btnortasolalt, btnortasagyukari, btnortaalt, btnortasagalt;
    private Button btna,btnb,btnc,btnd,btne,btnf;
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton,mTouchButton,outbtn;
    private ImageView bagladiIcon,taraBT;
    private TextView baglandiText;

    private static boolean flag = false;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth bulunmamaktadir.", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else if (mChatService == null) {
                setupChat();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newlayoutfragmnet, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mTouchButton = (Button) view.findViewById(R.id.mTouchButton);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn1 = (Button) view.findViewById(R.id.btn1);
        btnN = (Button) view.findViewById(R.id.btnN);
        btnsolsolalt = (Button) view.findViewById(R.id.btnaltsol);

        btnortaust = (Button) view.findViewById(R.id.btnortaust);
        btnortasolyukari = (Button) view.findViewById(R.id.g1solyukari);
        btnortasolalt = (Button) view.findViewById(R.id.g1solalt);
        btnortasagyukari = (Button) view.findViewById(R.id.g1sagyukari);
        btnortaalt = (Button) view.findViewById(R.id.g1alt);
        btnortasagalt = (Button) view.findViewById(R.id.g1ssagalt);

        outbtn = (Button) view.findViewById(R.id.outID);

        btna = (Button)view.findViewById(R.id.btna);
        btnb = (Button)view.findViewById(R.id.btnb);
        btnc = (Button)view.findViewById(R.id.btnc);
        btnd = (Button)view.findViewById(R.id.btnd);
        btne = (Button)view.findViewById(R.id.btne);
        btnf = (Button)view.findViewById(R.id.btnf);

        bagladiIcon = (ImageView) view.findViewById(R.id.bagladiView);
        taraBT = (ImageView) view.findViewById(R.id.taraBT);
        baglandiText = (TextView) view.findViewById(R.id.bagladiIDtxt);

        ImageButton soru = (ImageButton) view.findViewById(R.id.soruID);
        soru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),HelpActvity.class);
                startActivity(i);
            }
        });


    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {



        // Initialize the send button with a listener that for click events


        outbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        taraBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mBluetoothAdapter.isEnabled()){
                    Toast.makeText(getActivity(), "Lutfen Bluetooth'u kapatmatmayiniz", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }

            }
        });

        mTouchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    mTouchButton.setBackgroundResource(R.drawable.birpress);
                    flag = true;
                    sendString = "A";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mTouchButton.setBackgroundResource(R.drawable.bir);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }


        });

        btn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    btn2.setBackgroundResource(R.drawable.ikipress);
                    flag = true;
                    sendString = "B";
                    new Thread(new Task()).start();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn2.setBackgroundResource(R.drawable.iki);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return false;
            }


        });

        btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btn1.setBackgroundResource(R.drawable.ucpress);
                    flag = true;
                    sendString = "C";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btn1.setBackgroundResource(R.drawable.uc);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }


        });

        btnN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnN.setBackgroundResource(R.drawable.noktapress);
                    flag = true;
                    sendString = "D";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnN.setBackgroundResource(R.drawable.nokta);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }


        });

        btnsolsolalt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnsolsolalt.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "E";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnsolsolalt.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }

        });

        btnortaust.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    btnortaust.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "F";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortaust.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }

        });

        btnortasolyukari.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnortasolyukari.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "G";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortasolyukari.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }

        });

        btnortasolalt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnortasolalt.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "H";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortasolalt.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btnortasagyukari.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnortasagyukari.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "K";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortasagyukari.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btnortaalt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnortaalt.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "L";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortaalt.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btnortasagalt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnortasagalt.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "M";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnortasagalt.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btna.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btna.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "N";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btna.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });
        btnb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnb.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "P";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnb.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btnc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnc.setBackgroundResource(R.drawable.upb);
                    flag = true;
                    sendString = "R";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnc.setBackgroundResource(R.drawable.up);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });

        btnd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnd.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "S";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnd.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });
        btne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btne.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "X";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btne.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });
        btnf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    btnf.setBackgroundResource(R.drawable.downb);
                    flag = true;
                    sendString = "Y";
                    new Thread(new Task()).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnf.setBackgroundResource(R.drawable.down);
                    flag = false;
                    sendString = "Z";
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                }

                return  false;
            }
        });


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    class Task implements Runnable {

        @Override
        public void run() {

            while (flag) {
                try {
                    byte[] send = sendString.getBytes();

                    mChatService.write(send);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    android.util.Log.i("SendDataErorr", e.getMessage().toString());
                }
            }
        }


    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {

            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                          //  mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                  //  mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                   // mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Baglanti Basarili "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                         changeiconNew();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                        changeiconOld();
                    }
                    break;
                case Constants.ERORR:
                    if (null != activity) {
                        Toast.makeText(activity, "Lutfen Bluetooth'u kapatmayiniz",
                                Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    private void changeiconOld() {

        bagladiIcon.setBackgroundResource(R.drawable.bdegill);
        baglandiText.setText("Baglanti Basarisiz");
        baglandiText.setTextColor(Color.RED);
    }

    private void changeiconNew() {

        bagladiIcon.setBackgroundResource(R.drawable.bagli);
        baglandiText.setText("Baglanti Basarili");
        baglandiText.setTextColor(Color.GREEN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred

                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.helpID: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent intent = new Intent(getActivity(),HelpActvity.class);
                startActivity(intent);
                return true;
            }


        }
        return false;
    }





}
