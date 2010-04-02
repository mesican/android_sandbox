package com.wifion;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

public class WifiOn extends Activity {
	private TelephonyManager tm;
	private WifiManager wm;
	private PhoneStateListener listener;
	private GsmCellLocation home;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		this.tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		this.wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		if (tm.getCellLocation() instanceof GsmCellLocation){
			home = (GsmCellLocation)tm.getCellLocation();
		}

		this.listener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(android.telephony.CellLocation location) {
				setWifi(location);
			};
		};
		
		this.tm.listen(this.listener, PhoneStateListener.LISTEN_CELL_LOCATION);
		
		setWifi(home);
    }
    
    @Override
    protected void onDestroy() {
    	if (this.isFinishing()){
    		this.tm.listen(this.listener, PhoneStateListener.LISTEN_NONE);
    	}
    	super.onDestroy();
    }
    
    public void setWifi(CellLocation location){    	
    	if (location instanceof GsmCellLocation){
			GsmCellLocation gsmLocation = (GsmCellLocation)location;
			
			if (gsmLocation.getLac() == home.getLac()){
				if (!wm.isWifiEnabled()){
					wm.setWifiEnabled(true);
					StringBuilder builder = new StringBuilder("Enabling Wifi");
					builder.append("\nHome Tower : " + this.home.getCid() + " - " + this.home.getLac());
					builder.append("\nCurrent Tower:" + gsmLocation.getCid() + " - " + gsmLocation.getLac());
					Toast.makeText(getBaseContext(),builder.toString(), Toast.LENGTH_LONG).show();
				}		
			} else {
				if (wm.isWifiEnabled()){
					wm.setWifiEnabled(false);
					StringBuilder builder = new StringBuilder("Disabling Wifi");
					builder.append("\nHome Tower : " + this.home.getCid() + " - " + this.home.getLac());
					builder.append("\nCurrent Tower:" + gsmLocation.getCid() + " - " + gsmLocation.getLac());
					Toast.makeText(getBaseContext(),builder.toString(), Toast.LENGTH_LONG).show();
				}	
			}
		}
    }
}