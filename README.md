GetPressurePlugin is an extermely simple Cordova plugin to gather pressure data from compatible Android devices.

Usage: 

Once the plugin is installed it can be accessed by calling:

navigator.GetPressurePlugin.getPressure(getPressureSuccess, getPressureFail);

getPressureSuccess and getPressureFail each take a single parameter representing the pressure in mBars for Success and the error message for Fail.

The pressure sensor's onSensorChange method is called a total of 13 times, and the median reading is taken to help elimate any noisy readings. This value can be changed to any odd number by modifing the NUMBER_OF_READINGS field within GetPressure.java.