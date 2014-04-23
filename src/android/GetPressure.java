/*
	See:
	http://www.apache.org/licenses/LICENSE-2.0
	for license information.
*/

package com.wx.iseeweather.GetPressurePlugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.concurrent.Semaphore;
import java.util.Arrays;

public class GetPressure extends CordovaPlugin implements SensorEventListener {

	/*
		-The idea is to take an odd number of readings, store them into an array, 
		sort the array, and then take the middle of the array. 
		-This is the median result so that an initial 'bad' reading doesn't cause any issues.
	*/
	private final int NUMBER_OF_READINGS = 13;
	private final double NUMBER_OF_READINGS_D = NUMBER_OF_READINGS;

	private SensorManager mSensorManager;
	private Sensor mPressure;

	private CallbackContext callbackContext;

	private final Semaphore pressureMux = new Semaphore(1);
	private float[] pressures = new float[NUMBER_OF_READINGS];
	private int currentNumberOfReadings;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;

        mSensorManager = (SensorManager) this.cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		if (mPressure != null){
			currentNumberOfReadings = 0;
			mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);

			PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
			return true;
		}
		else {
			callbackContext.error("No pressure sensor found.");
			return false;
		}
    }

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes. Do what exactly?
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		try {
			pressureMux.acquire();
		} catch (InterruptedException e) {

		}
		pressures[currentNumberOfReadings] = event.values[0];
		currentNumberOfReadings++;
		pressureMux.release();
		if(currentNumberOfReadings >= NUMBER_OF_READINGS - 1) {
			sendFinalResult();
		} 
	}

	private void sendFinalResult() {
		mSensorManager.unregisterListener(this);
		Arrays.sort(pressures);
		PluginResult result = new PluginResult(PluginResult.Status.OK, pressures[(NUMBER_OF_READINGS - 1)/2]);
		result.setKeepCallback(false);
		callbackContext.sendPluginResult(result);
	}
}