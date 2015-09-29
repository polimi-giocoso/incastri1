package com.example.legodigitalsonoro1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * class that manages the connection setup phase
 * @author chiara
 *
 */
public class ConnectionSetupActivity extends Activity {

	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;

	protected static final String DEVICE_NAME = "abc";

	public static final String TOAST = null;

	protected static final String BTCONNECTION = "bluetoothConnection";

	private String mConnectedDeviceName;

	private Intent serverIntent;
	private BluetoothService mBTService;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection_setup);

		mBTService = BluetoothService.getBluetoothService(mHandler);
		
		//set discoverability, this part is foundamental in case of connection with new users 
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
	}
	
	
	public void startAConnection(View v){
		serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mBTService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mBTService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth chat services
				mBTService.start();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			connectDevice(data, true);
			Log.d("INFO","Connecting...");
		}
	}

	private void connectDevice(Intent data, boolean b) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
				.getRemoteDevice(address);
		// Attempt to connect to the device
		mBTService.connect(device);

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mBTService.stop();
		finish();
	}
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    break;
                case BluetoothService.STATE_CONNECTING:
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    break;
                }
                break;
            case BluetoothService.START_MATCH:
            	intent=new Intent(ConnectionSetupActivity.this, MatchActivity.class);
            	intent.putExtra(MainActivity.MODE, MainActivity.multiPlayerServer);
            	startActivity(intent);
            	finish();
                break;
            case BluetoothService.MESSAGE_FIRST_READ:
            	byte[] readBuf=(byte[])msg.obj;
            	String readMessage=new String(readBuf, 0, msg.arg1);
            	intent=new Intent(ConnectionSetupActivity.this, MatchActivity.class);
            	intent.putExtra(MatchActivity.SETTINGS, readMessage);
            	intent.putExtra(MainActivity.MODE, MainActivity.multiPlayerClient);
            	startActivity(intent);
            	finish();
                break;
            case BluetoothService.MESSAGE_DEVICE_NAME:
            	mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothService.MESSAGE_TOAST:
                break;
            }
        }
    };

}
