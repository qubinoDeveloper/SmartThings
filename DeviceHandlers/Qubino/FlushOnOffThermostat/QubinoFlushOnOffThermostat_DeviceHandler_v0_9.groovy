/**
 *  Qubino Flush On Off Thermostat
 *	Device Handler 
 *	Version 0.991
 *  Date: 19.09.2018
 *	Author: Kristjan Jam&scaron;ek (Kjamsek), Goap d.o.o.
 *  Copyright 2017 Kristjan Jam&scaron;ek
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 * |---------------------------- DEVICE HANDLER FOR QUBINO FLUSH ON OFF THERMOSTAT Z-WAVE DEVICE -------------------------------------------------------|  
 *	The handler supports all unsecure functions of the Qubino Flush On Off Thermostat device, except configurable inputs. Configuration parameters and
 *	association groups can be set in the device's preferences screen, but they are applied on the device only after
 *	pressing the 'Set configuration' and 'Set associations' buttons on the bottom of the details view. 
 *
 *	This device handler supports data values that are currently not implemented as capabilities, so custom attribute 
 *	states are used. Please use a SmartApp that supports custom attribute monitoring with this device in your rules.
 * |-----------------------------------------------------------------------------------------------------------------------------------------------|
 *
 *
 *	TO-DO:
 *	- Implement Multichannel Association Command Class to add MC Association functionality and support configurable inputs.
 *  - Implement secure mode
 *
 *	CHANGELOG:
 *	0.98: Final release code cleanup and commenting
 *	0.99: Added comments to code for readability
 */
metadata {
	definition (name: "Qubino On Off Thermostat", namespace: "Goap", author: "Kristjan Jam&scaron;ek") {
		capability "Thermostat"					// - Thermostat capability tag
		capability "Thermostat Mode"			// - Thermostat Mode capability flag
		capability "Thermostat Setpoint"		// - Thermostat Setpoint capability flag
		capability "Power Meter"				// - Power Meter capability flag
		capability "Configuration" 				// - Needed for configure() function to set MultiChannel Lifeline Association Set if needed
		capability "Temperature Measurement" 	// - This capability is valid for devices with temperature sensors connected
		
		attribute "kwhConsumption", "number" 	//attribute used to store and display power consumption in KWH		
		attribute "power", "number" 			//attribute used to store and display power consumption in W
		
		command "tempUp"
		command "tempDown"
		command "modeSetting"
		command "setConfiguration" 				//command to issue Configuration Set commands to the module according to user preferences
		command "setAssociation" 				//command to issue Association Set commands to the modules according to user preferences
		command "refreshPowerConsumption" 		//command to issue Meter Get requests for KWH measurements from the device, W are already shown as part of Pwer Meter capability
		command "resetPower" 					//command to issue Meter Reset commands to reset accumulated pwoer measurements

        fingerprint mfr:"0159", prod:"0005", model:"0051", deviceJoinName: "Qubino On Off Thermostat"  //Manufacturer Information value for Qubino Flush On Off Thermostat
	}


	simulator {
		// TESTED WITH PHYSICAL DEVICE - UNNEEDED
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"thermostat", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temp", label:'${currentValue}', unit:"dF", defaultState: true, backgroundColors: [
					// Celsius Color Range
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 29, color: "#f1d801"],
					[value: 33, color: "#d04e00"],
					[value: 36, color: "#bc2323"],
					// Fahrenheit Color Range
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 92, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				])
			}
			tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "tempUp")
				attributeState("VALUE_DOWN", action: "tempDown")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#00A0DC")
				attributeState("heating", backgroundColor:"#e86d13")
				attributeState("cooling", backgroundColor:"#00A0DC")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("Off", label:'${name}')
				attributeState("Heat", label:'${name}')
				attributeState("Cool", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
			}
			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
			}
		}
		standardTile("modeSetting", "device.modeSetting", decoration: "flat", width: 6, height: 2) {
			state("modeSetting", label:'${currentValue}', action:'modeSetting')
		}
		standardTile("power", "device.power", decoration: "flat", width: 3, height: 3) {
			state("power", label:'${currentValue} W', icon: 'st.Appliances.appliances17')
		}
		standardTile("kwhConsumption", "device.kwhConsumption", decoration: "flat", width: 3, height: 3) {
			state("kwhConsumption", label:'${currentValue} kWh', icon: 'st.Appliances.appliances17')
		}
		standardTile("resetPower", "device.resetPower", decoration: "flat", width: 3, height: 3) {
			state("resetPower", label:'Reset Power', action:'resetPower')
		}
		standardTile("refreshPowerConsumption", "device.refreshPowerConsumption", decoration: "flat", width: 3, height: 3) {
			state("refreshPowerConsumption", label:'Refresh power', action:'refreshPowerConsumption')
		}
		standardTile("setConfiguration", "device.setConfiguration", decoration: "flat", width: 3, height: 3) {
			state("setConfiguration", label:'Set Configuration', action:'setConfiguration')
		}
		standardTile("setAssociation", "device.setAssociation", decoration: "flat", width: 3, height: 3) {
			state("setAssociation", label:'Set Associations', action:'setAssociation')
		}

		main("thermostat")
		details(["thermostat", "modeSetting", "power", "kwhConsumption", "resetPower", "refreshPowerConsumption", "setConfiguration", "setAssociation"])
	}
	preferences {
/**
*			--------	CONFIGURATION PARAMETER SECTION	--------
*/
				input name: "param1", type: "enum", required: false,
					options: ["0" : "0 - Mono-stable switch type (push button)",
							  "1" : "1 - Bi-stable switch type"],
					title: "1. Input I1 switch type.\n " +
						   "0 - Mono-stable switch type.\n" +
						   "1 - Bi-stable switch type.\n" +
						   "Default value: 1"
				input name: "param2", type: "enum", required: false,
					options: ["0" : "0 - Mono-stable switch type (push button)",
							  "1" : "1 - Bi-stable switch type"],
					title: "2. Input I2 switch type.\n " +
						   "0 - Mono-stable switch type.\n" +
						   "1 - Bi-stable switch type.\n" +
						   "Default value: 1"
				input name: "param3", type: "enum", required: false,
					options: ["0" : "0 - Mono-stable switch type (push button)",
							  "1" : "1 - Bi-stable switch type"],
					title: "3. Input I3 switch type.\n " +
						   "0 - Mono-stable switch type.\n" +
						   "1 - Bi-stable switch type.\n" +
						   "Default value: 1"
				input name: "param4", type: "enum", required: false,
					options: ["0" : "0 - NO (normally open) input type",
							  "1" : "1 - NC (normally close) input type"],
					title: "4. Input 1 contact type.\n " +
						   "Available settings:\n" +
						   "0 - NO (normally open) input type.\n" +
						   "1 - NC (normally close) input type.\n" +
						   "Default value: 0.\n" +
						   "NOTE: This parameter has influence only when parameter no. 11 is set to the value “2”. After setting this parameter, switch the window sensor once, so that the module could determine the input state."
				input name: "param5", type: "enum", required: false,
					options: ["0" : "0 - NO (normally open) input type",
							  "1" : "1 - NC (normally close) input type"],
					title: "5. Input 2 contact type.\n " +
						   "Available settings:\n" +
						   "0 - NO (normally open) input type.\n" +
						   "1 - NC (normally close) input type.\n" +
						   "Default value: 0.\n" +
						   "NOTE: This parameter has influence only when parameter no. 12 is set to the value “2000”. After setting this parameter, switch the condense sensor once, so that the module could determine the input state."
				input name: "param6", type: "enum", required: false,
					options: ["0" : "0 - NO (normally open) input type",
							  "1" : "1 - NC (normally close) input type"],
					title: "6. Input 2 contact type.\n " +
						   "Available settings:\n" +
						   "0 - NO (normally open) input type.\n" +
						   "1 - NC (normally close) input type.\n" +
						   "Default value: 0.\n" +
						   "NOTE: This parameter has influence only when parameter no. 13 is set to the value “2”. After setting this parameter, switch the flood sensor once, so that the module could determine the input state."
				input name: "param10", type: "enum", required: false,
					options: ["0" : "0 - ALL ON is not active, ALL OFF is not active",
							  "1" : "1 - ALL ON is not active, ALL OFF active",
							  "2" : "2 - ALL ON active, ALL OFF is not active",
							  "255" : "255 - ALL ON active, ALL OFF active"],
					title: "10. Activate / deactivate functions ALL ON / ALL OFF.\n " +
						   "Available settings:\n" +
							"255 - ALL ON active, ALL OFF active.\n" +
							"0 - ALL ON is not active, ALL OFF is not active.\n" +
							"1 - ALL ON is not active, ALL OFF active.\n" +
							"2 - ALL ON active, ALL OFF is not active.\n" +
							"Default value: 255.\n" +
							"Flush on/off thermostat module responds to commands ALL ON / ALL OFF that may be sent by the main controller or by other controller belonging to the system."
				input name: "param11", type: "enum", required: false,
					options: ["32767" : "32767 – input I1 doesn’t influence on the Heat/Cool process",
							  "1" : "1 - input I1 changes the mode of the thermostat between Off and Heat/Cool. In this case function on window sensor is disabled",
							  "2" : "2 - input I1 influences on heating/cooling valves according to status of window sensor. In this case function of Off and Heat/Cool selection by I1 is disabled."],
					title: "11. I1 Functionality selection.\n " +
						   "Available settings:\n" +
							"32767 – input I1 doesn’t influence on the Heat/Cool process.\n" +
							"1 - input I1 changes the mode of the thermostat between Off and Heat/Cool. In this case function on window sensor is disabled.\n" +
							"2 - input I1 influences on heating/cooling valves according to status of window sensor. In this case function of Off and Heat/Cool selection by I1 is disabled..\n" +
							"Default value: 1.\n" +
							"NOTE: If 'Window Sensor' selected (value set to 2), parameter 100 (enable/disable endpoint) must be set to non-zero value and module re-included!"
				input name: "param12", type: "number", range: "0..32767", required: false,
					title: "12. I2 Functionality selection.\n" +
						   "Available settings:\n" +
							"32767 - input I2 does not influence on the Heat/Cool process.\n" +
							"From 0 to 990 - Temperature set point from 0.0 °C to 99.0 °C. When I2 is pressed, it automatically set temperature setpoint according to value defined here. In this case function of condense sensor is disabled.\n" +
							"From 1001 to 1150 - Temperature set point from -0.1 °C to -15.0 °C. When I2 is pressed, it automatically set temperature setpoint according to value defined here. In this case function of condense sensor is disabled.\n" +
							"2000 - Input I2 influences on the heating/cooling valve according to status of condense sensor, In this case function of setpoint selection with I2 is disabled. This option has influence only when Parameter no. 59 is in Cool mode.\n" +
							"NOTE: If 'Condense Sensor' selected (value set to '2000'), parameter 101 (enable/disable endpoint) must be set to non-zero value and module re-included!\n" +
							"Default value: 32767"
				input name: "param13", type: "enum", required: false,
					options: ["32767" : "32767 – input I3 doesn’t influence on the Heat/Cool process",
							  "1" : "1 - input I3 changes the mode of the thermostat between Heat and Cool and override parameter 59. In this case function on flood sensor is disabled NOTE: After parameter change, first exclude module (without setting parameters to default value) and then re include the module!",
							  "2" : "2 - input I3 influences on cooling and heating valves according to status of flood sensor. In this case function of Heat and Cool selection by I3 is disabled."],
					title: "13. I3 Functionality selection.\n " +
						   "Available settings:\n" +
							"32767 – input I3 doesn’t influence on the Heat/Cool process.\n" +
							"1 - input I3 changes the mode of the thermostat between Heat and Cool and override parameter 59. In this case function on flood sensor is disabled NOTE: After parameter change, first exclude module (without setting parameters to default value) and then re include the module!\n" +
							"2 - input I3 influences on cooling and heating valves according to status of flood sensor. In this case function of Heat and Cool selection by I3 is disabled.\n" +
							"Default value: 32767.\n" +
							"NOTE: If 'Flood Sensor' selected (value set to 2), parameter 102 (enable/disable endpoint) must be set to non-zero value and module re-included!"
				input name: "param40", type: "number", range: "0..100", required: false,
					title: "40. Power reporting in Watts on power change.\n" +
						   "Set value means percentage, set value from 0 - 100 = 0% - 100%.\n" +
						   "Available settings:\n" +
							"0 - reporting disabled.\n" +
							"1 - 100 = 1% - 100% Reporting enabled. Power report is sent (push) only when actual power in Watts in real time changes for more than set percentage comparing to previous actual power in Watts, step is 1%.\n" +
							"Default value: 0." +
							"NOTE: if power changed is less than 1W, the report is not send (pushed), independent of percentage set."
				input name: "param42", type: "number", range: "0..32535", required: false,
					title: "42. Power reporting in Watts by time interval.\n" +
						   "Set value means time interval (0 - 32535) in seconds, when power report is send.\n" +
						   "Available settings:\n" +
							"0 - reporting disabled.\n" +
							"1 - 32535 = 1 second - 32535 seconds. Reporting enabled. Power report is send with time interval set by entered value.\n" +
							"Default value: 0." 			
				input name: "param43", type: "number", range: "0..1255", required: false,
					title: "43. Hysteresis On.\n" +
						   "This parameter defines temperature min difference between real measured temperature and set-point temperature to turn device on.\n" +
						   "NOTE: Values set for Hysteresis On/Off are valid for Heat Mode. If Cool Mode is selected, values are inverted automatically!\n" +
						   "Available settings:\n" +
							"0 - 255 = 0.0 °C ... 25.5°C.\n" +
							"1001 - 1255 = - 0.1°C ... - 25.5 °C\n" +
							"Default value: 1005 (- 0.5 °C)." 			   
				input name: "param44", type: "number", range: "0..1255", required: false,
					title: "44. Hysteresis Off.\n" +
						   "This parameter defines temperature min difference between real measured temperature and set-point temperature to turn device on.\n" +
						   "NOTE: Values set for Hysteresis On/Off are valid for Heat Mode. If Cool Mode is selected, values are inverted automatically!\n" +
						   "Available settings:\n" +
							"0 - 255 = 0.0 °C ... 25.5°C.\n" +
							"1001 - 1255 = - 0.1°C ... - 25.5 °C\n" +
							"Default value: 5 (+ 0.5 °C)" 		   
				input name: "param45", type: "number", range: "0..1127", required: false,
					title: "45. Antifreeze.\n" +
						   "Set value means at which temperature the device will be turned on even if the thermostat was manually set to off.\n" +
						   "NOTE: Antifreeze is activated only in heating mode and it uses hysteresis of ±0.5°C.\n" +
						   "Available settings:\n" +
							"0 - 125 = 0.0 °C - 12.5 °C\n" +
							"1001 - 1127 = -0.1°C ~ -12.6 °C\n" +
							"255 - Antifreeze functionality disabled\n" +
							"Default value: 50 (5.0 °C)." 		   
				input name: "param59", type: "enum", required: false,
					options: ["0" : "0 - Heat mode",
							  "1" : "1 - Cool mode"],
					title: "59. Thermostat Mode.\n " +
							"Available settings:\n" +
							"0 - Heat mode.\n" +
							"1 - Cool mode.\n" +
							"Default value: 0.\n" +
							"NOTE: After parameter change, first exclude module (without setting parameters to default value) and then re include the module!\n" +
							"NOTE: To enable hysteresis in Heat mode: Value of Parameter no. 44 > Value of Parameter no. 43\n" +
							"To enable hysteresis in Cool mode: Value of Parameter no. 43 > Value of Parameter no. 44\n" +
							"NOTE: When Cooling mode selected, the function of Hysteresis On and Hysteresis Off is inverted!" 
				input name: "param60", type: "number", range: "0..1150", required: false,
					title: "60. Too low temperature limit.\n" +
						   "Available settings:\n" +
							"1 - 1000 = 0.1 °C – 100.0 °C, step is 0.1 °C.\n" +
							"1001 - 1150: -0.1 °C ~ – 15.0 °C\n" +
							"Default value: 50 (Too low temperature limit is 5.0 °C)\n" +
							"NOTE:Too low temperature limit is used with Association Group 4."
				input name: "param61", type: "number", range: "0..1000", required: false,
					title: "61. Too low temperature limit.\n" +
						   "Available settings:\n" +
							"1 - 1000 = 0.1 °C - 100.0 °C, step is 0.1 °C. Too high temperature limit is used with Association Group 4.\n" +
							"Default value: 700 (too high temperature limit is 70.0 °C)"			
				input name: "param63", type: "enum", required: false,
					options: ["0" : "0 - When system is turned off the output is 0V (NC)",
							  "1" : "1 - When system is turned off the output is 230V or 24V (NO)"],
					title: "63. Output Switch selection.\n" +
						   "Set value means the type of the device that is connected to the output. The device type can be normally open (NO) or normally close (NC).\n" +
						   "Available settings:\n" +
						    "0 - When system is turned off the output is 0V (NC)\n" +
							"1 - When system is turned off the output is 230V or 24V (NO)\n" +
							"Default value: 0."
				input name: "param70", type: "number", range: "0..32000", required: false,
					title: "70. Input 1 status on delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 11.\n" +
							"NOTE: Device status on UI change immediately."
				input name: "param71", type: "number", range: "0..32000", required: false,
					title: "71. Input 1 status off delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 11."
				input name: "param72", type: "number", range: "0..32000", required: false,
					title: "72. Input 2 status on delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 12.\n" +
							"NOTE: Device status on UI change immediately."
				input name: "param73", type: "number", range: "0..32000", required: false,
					title: "73. Input 2 status off delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 12."
				input name: "param74", type: "number", range: "0..32000", required: false,
					title: "74. Input 3 status on delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 12.\n" +
							"NOTE: Device status on UI change immediately."
				input name: "param75", type: "number", range: "0..32000", required: false,
					title: "75. Input 3 status off delay.\n" +
						   "Available settings:\n" +
							"0 - 32000 seconds.\n" +
							"Default value: 0\n" +
							"If the value of parameter is different to 0, means that the Influence of this input to heating or cooling will react after inserted time. This parameter has influence only when the window sensor functionality is selected by the parameter no. 12."
				input name: "param76", type: "number", range: "0..127", required: false,
					title: "76. Association group 2, 10 - reporting on time interval.\n" +
						   "Available settings:\n" +
							"0 - Reporting disabled.\n" +
							"1 - 127 = 1 minute – 127 minutes, reporting enabled.\n" +
							"Default value: 30 = 30 minutes\n" +
							"NOTE: If the Association groups 2 or 10 are set, the device is reporting its state (Basic Set ON/ OFF) on change and on time interval (if this parameter is set)."
				input name: "param77", type: "number", range: "0..32767", required: false,
					title: "77. Association group 10 - delay before sending Basic Set ON.\n" +
						   "Available settings:\n" +
							"0 - Reports with no delay.\n" +
							"1 - 32767 = 1 second – 32767 seconds, reporting enabled.\n" +
							"Default value: 180 = 3 minutes\n" +
							"NOTE: If this parameter is set, Basic Set ON/OFF Report is delayed for the time defined in this."
				input name: "param78", type: "enum", required: false,
					options: ["0" : "0 - degrees Celsius",
							  "1" : "1 - degrees Fahrenheit"],
					title: "78. Scale selection.\n" +
						   "Available settings:\n" +
						    "0 - degrees Celsius\n" +
							"1 - degrees Fahrenheit\n" +
							"Default value 0 = degrees Celsius.\n" +
							"NOTE: This scale has influence on Temperature reporting and scale reporting. The device is capable of receiving a Setpoint in all supported scales."
				input name: "param100", type: "enum", required: false,
					options: ["0" : "0 - Endpoint, I1 disabled",
							  "1" : "1 - Home Security; Motion Detection, unknown location",
							  "2" : "2 - CO; Carbon Monoxide detected, unknown  location",
							  "3" : "3 - CO2; Carbon Dioxide detected, unknown location",
							  "4" : "4 - Water Alarm; Water Leak detected, unknown location",
							  "5" : "5 - Heat Alarm; Overheat detected, unknown location",
							  "6" : "6 - Smoke Alarm; Smoke detected, unknown location",
							  "9" : "9 - Sensor binary"],
					title: "100. Enable / Disable Endpoints I1 or select Notification Type and Event.\n" +
						   "Enabling I1 means that Endpoint (I1) will be present on UI. Disabling it will result in hiding the endpoint according to the parameter set value. Additionally, a Notification Type and Event can be selected for the endpoint.\n" +
						   "Available settings:\n" +
							"0 - Endpoint, I1 disabled\n" +
							"1 - Home Security; Motion Detection, unknown location\n" +
							"2 - CO; Carbon Monoxide detected, unknown  location\n" +
							"3 - CO2; Carbon Dioxide detected, unknown location\n" +
							"4 - Water Alarm; Water Leak detected, unknown location\n" +
							"5 - Heat Alarm; Overheat detected, unknown location\n" +
							"6 - Smoke Alarm; Smoke detected, unknown location\n" +
							"9 - Sensor binary\n" +
							"Default value: 0.\n" +
							"NOTE1: After parameter change, first exclude module (without setting parameters to default value) then wait at least 30s and then re include the module! When the parameter is set to value 9 the notifications are send for Home Security.\n" +
							"NOTE2: If 'endpoint enabled' (value set to 1..9), parameter 11 must be set to '2' as 'Window Sensor'!"
				input name: "param101", type: "enum", required: false,
					options: ["0" : "0 - Endpoint, I2 disabled",
							  "1" : "1 - Home Security; Motion Detection, unknown location",
							  "2" : "2 - CO; Carbon Monoxide detected, unknown  location",
							  "3" : "3 - CO2; Carbon Dioxide detected, unknown location",
							  "4" : "4 - Water Alarm; Water Leak detected, unknown location",
							  "5" : "5 - Heat Alarm; Overheat detected, unknown location",
							  "6" : "6 - Smoke Alarm; Smoke detected, unknown location",
							  "9" : "9 - Sensor binary"],
					title: "101. Enable / Disable Endpoints I2 or select Notification Type and Event.\n" +
						   "Enabling I2 means that Endpoint (I2) will be present on UI. Disabling it will result in hiding the endpoint according to the parameter set value. Additionally, a Notification Type and Event can be selected for the endpoint.\n" +
						   "Available settings:\n" +
							"0 - Endpoint, I2 disabled\n" +
							"1 - Home Security; Motion Detection, unknown location\n" +
							"2 - CO; Carbon Monoxide detected, unknown  location\n" +
							"3 - CO2; Carbon Dioxide detected, unknown location\n" +
							"4 - Water Alarm; Water Leak detected, unknown location\n" +
							"5 - Heat Alarm; Overheat detected, unknown location\n" +
							"6 - Smoke Alarm; Smoke detected, unknown location\n" +
							"9 - Sensor binary\n" +
							"Default value: 0.\n" +
							"NOTE1: After parameter change, first exclude module (without setting parameters to default value) then wait at least 30s and then re include the module! When the parameter is set to value 9 the notifications are send for Home Security.\n" +
							"NOTE2: If 'endpoint enabled' (value set to 1..9), parameter 12 must be set to '2000' as 'Condense sensor'!"
				input name: "param102", type: "enum", required: false,
					options: ["0" : "0 - Endpoint, I3 disabled",
							  "1" : "1 - Home Security; Motion Detection, unknown location",
							  "2" : "2 - CO; Carbon Monoxide detected, unknown  location",
							  "3" : "3 - CO2; Carbon Dioxide detected, unknown location",
							  "4" : "4 - Water Alarm; Water Leak detected, unknown location",
							  "5" : "5 - Heat Alarm; Overheat detected, unknown location",
							  "6" : "6 - Smoke Alarm; Smoke detected, unknown location",
							  "9" : "9 - Sensor binary"],
					title: "102. Enable / Disable Endpoints I3 or select Notification Type and Event.\n" +
						   "Enabling I3 means that Endpoint (I3) will be present on UI. Disabling it will result in hiding the endpoint according to the parameter set value. Additionally, a Notification Type and Event can be selected for the endpoint.\n" +
						   "Available settings:\n" +
							"0 - Endpoint, I3 disabled\n" +
							"1 - Home Security; Motion Detection, unknown location\n" +
							"2 - CO; Carbon Monoxide detected, unknown  location\n" +
							"3 - CO2; Carbon Dioxide detected, unknown location\n" +
							"4 - Water Alarm; Water Leak detected, unknown location\n" +
							"5 - Heat Alarm; Overheat detected, unknown location\n" +
							"6 - Smoke Alarm; Smoke detected, unknown location\n" +
							"9 - Sensor binary\n" +
							"Default value: 0.\n" +
							"NOTE1: After parameter change, first exclude module (without setting parameters to default value) then wait at least 30s and then re include the module! When the parameter is set to value 9 the notifications are send for Home Security.\n" +
							"NOTE2: If 'endpoint enabled' (value set to 1..9), parameter 13 must be set to '2' as 'Flood sensor'!"			
				input name: "param110", type: "number", range: "1..32536", required: false,
					title: "110. Temperature sensor offset settings.\n" +
						   "Set value is added or subtracted to actual measured value by sensor..\n" +
						   "Available settings:\n" +
							"32536 - offset is 0.0C.\n" +
							"From 1 to 100 - value from 0.1 °C to 10.0 °C is added to actual measured temperature.\n" +
							"From 1001 to 1100 - value from -0.1 °C to -10.0 °C is subtracted to actual measured temperature.\n" +
							"Default value: 32536."
				input name: "param120", type: "number", range: "0..127", required: false,
					title: "120. Digital temperature sensor reporting.\n" +
						   "If digital temperature sensor is connected, module reports measured temperature on temperature change defined by this parameter.\n" +
						   "Available settings:\n" +
							"0 - Reporting disabled.\n" +
							"1 - 127 = 0,1°C - 12,7°C, step is 0,1°C.\n" +
							"Default value: 5 = 0,5°C change."
				input name: "param121", type: "enum", required: false,
					options: ["0" : "0 - internal digital temperature sensor is mounted, setpoint is set by controller",
							  "1" : "1 - (bit 0) temperature is grabbed from external always on sensor with sensor_multilevel_get sent by association 3",
							  "2" : "2 - (bit 1) temperature is grabbed from external battery powered room sensor declared in parameter 122",
							  "4" : "4 - (bit 2) setpoint is gragged from external always on module with thermostat_setpoint_get sent by association 5",
							  "8" : "8 - (bit 3) setpoint is grabbed from external battery powered room sensor declared in parameter 122.",
							  "10" : "10 – (bit 1 and bit 3) temperature AND setpoint are grabbed from external battery powered room sensor declared in parameter 122"],
					title: "121. Digital temperature sensor / setpoint selector.\n" +
							"If digital temperature sensor is not connected, module can grab measured temperature from external secondary module.\n" +
							"Available settings:\n" +
						    "0 - internal digital temperature sensor is mounted, setpoint is set by controller\n" +
							"1 - (bit 0) temperature is grabbed from external always on sensor with sensor_multilevel_get sent by association 3\n" +
							"2 - (bit 1) temperature is grabbed from external battery powered room sensor declared in parameter 122\n" +
							"4 - (bit 2) setpoint is gragged from external always on module with thermostat_setpoint_get sent by association 5\n" +
							"8 - (bit 3) setpoint is grabbed from external battery powered room sensor declared in parameter 122\n" +
							"10 – (bit 1 and bit 3) temperature AND setpoint are grabbed from external battery powered room sensor declared in parameter 122\n" +
							"Default value 0."
				input name: "param121", type: "number", range: "0..254", required: false,
					title: "121. Node ID of external battery powered room sensor\n" +
							"If digital temperature sensor is not connected, module can grab measured temperature from external battery powered room sensor defined by this parameter.\n" +
							"Available settings:\n" +
							"0 – external battery powered room sensor not in function.\n" +
							"1 - 254 = Node ID of external battery powered room sensor.\n" +
							"Default value: 0\n" +
							"NOTE: Get sensor Node ID from controller and set parameter 122 immediately after sensor wake up (after button press on it etc.)."	
/**
*			--------	ASSOCIATION GROUP SECTION	--------
*/
				input name: "assocGroup2", type: "text", required: false,
					title: "Association group 2: \n" +
						   "Basic on/off (triggered at change of the output Q state and reflecting its state) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup3", type: "text", required: false,
					title: "Association group 3: \n" +
						   "SENSOR_MULTILEVEL_GET (triggered once per minute if Parameter 121 is not 0) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup4", type: "text", required: false,
					title: "Association group 4: \n" +
						   "Basic on/off (triggered when actual temperature reach Too high or Too Low temperature limit, it sends FF/00 in Cool Mode, 00/FF in Heat Mode and 00 when thermostat is off; hysteresis is 1°C) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup5", type: "text", required: false,
					title: "Association group 5: \n" +
						   "THERMOSTAT_SETPOINT_GET (triggered once per minute if Parameter 121 is not 0) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup6", type: "text", required: false,
					title: "Association group 6: \n" +
						   "Basic on/off (trigged by change of I1 if window sensor functionality is selected by parameter no. 11) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup7", type: "text", required: false,
					title: "Association group 7: \n" +
						   "Basic on/off (trigged by change of I2 if condense sensor functionality is selected by parameter no. 12) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup8", type: "text", required: false,
					title: "Association group 8: \n" +
						   "Basic on/off (trigged by change of I3 if flood sensor functionality is selected by parameter no. 13) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup9", type: "text", required: false,
					title: "Association group 9: \n" +
						   "Sensor multilevel report (trigged by change of temperature) up to 16 nodes.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
				
				input name: "assocGroup10", type: "text", required: false,
					title: "Association group 10: \n" +
						   "Basic on/off (triggered by change of the output Q state and reflecting its state), up to 16 nodes, Basic Set ON/OFF command is delayed for the time defined in parameter no. 77.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
	}
}
/**
*	--------	HELPER METHODS SECTION	--------
*/
/**
 * Converts a list of String type node id values to Integer type.
 *
 * @param stringList - a list of String type node id values.
 * @return stringList - a list of Integer type node id values.
*/
def convertStringListToIntegerList(stringList){
	log.debug stringList
	if(stringList != null){
		for(int i=0;i<stringList.size();i++){
			stringList[i] = stringList[i].toInteger()
		}
	}
	return stringList
}
/**
 * Converts temperature values to fahrenheit or celsius scales according to user's setting.
 *
 * @param scaleParam user set scale parameter.
 * @param encapCmd received temperature parsed value.
 * @return String type value of the converted temperature value.
*/
def convertDegrees(scaleParam, encapCmd){
	switch (scaleParam) {
		default:
				break;
		case "F":
				if(encapCmd.scale == 1){
					return encapCmd.scaledSensorValue.toString()
				}else{
					return (encapCmd.scaledSensorValue * 9 / 5 + 32).toString()
				}
				break;
		case "C":
				if(encapCmd.scale == 0){
					return encapCmd.scaledSensorValue.toString()
				}else{
					return (encapCmd.scaledSensorValue * 9 / 5 + 32).toString()
				}
				break;
	}
}
/**
 * Function that is used to convert received setpoints from degrees C to degrees F and vice versa.
 *
 * @param scaleParam - the string value of the currently selected temperature format
 * @param encapCmd - command object for received Thermostat Setpoint Report frames
 * @return String type value of the converted setpoint value.
*/
def convertSetpoint(scaleParam, encapCmd){
	switch (scaleParam) {
		default:
				break;
		case "F":
				if(encapCmd.scale == 1){
					return encapCmd.scaledValue.toString()
				}else{
					return (encapCmd.scaledValue * 9 / 5 + 32).toString()
				}
				break;
		case "C":
				if(encapCmd.scale == 0){
					return encapCmd.scaledValue.toString()
				}else{
					return (encapCmd.scaledValue * 9 / 5 + 32).toString()
				}
				break;
	}
}
/**
 * Function that is used to round the new setpoint values to 0.5 increments.
 *
 * @param val - numeric value of the setpoint we are trying to round
 * @return val - rounded numeric value of the new setpoint value
*/
def roundToHalf(val){
	return Math.round(val * 2) / 2.0;
}
/*
*	--------	HANDLE COMMANDS SECTION	--------
*/
/**
 * Function that is used for automation apps to set a heating setpoint in parallel to the standard tile functionality.
 *
 * @param temp - numeric value of the desired heating setpoint
 * List of commands that will be executed in sequence with 250 ms delay inbetween.
*/
def setHeatingSetpoint(temp) {
    def cmds = []
    if(location.temperatureScale == "C"){
		cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: temp, setpointType: 1, size: 2).format()
	}else{
		cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: temp, setpointType: 1, size: 2).format()
	}
    cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1).format()
    cmds << zwave.thermostatModeV2.thermostatModeGet().format()
    return delayBetween(cmds, 250)
}
/**
 * Function that is used for automation apps to set a cooling setpoint in parallel to the standard tile functionality.
 *
 * @param temp - numeric value of the desired cooling setpoint
 * List of commands that will be executed in sequence with 250 ms delay inbetween.
*/
def setCoolingSetpoint(temp) {
    def cmds = []
    if(location.temperatureScale == "C"){
		cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: temp, setpointType: 2, size: 2).format()
	}else{
		cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: temp, setpointType: 2, size: 2).format()
	}
    cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 2).format()
    cmds << zwave.thermostatModeV2.thermostatModeGet().format()
    return delayBetween(cmds, 250)
}
/**
 * Function that is used to change the current Thermostat Mode.
 *
 * @param void
 * @return List of commands that will be executed in sequence with 250 ms delay inbetween.
*/
def modeSetting() {
	def currentMode = device.currentValue("thermostatMode")
	def cmds = []
	switch(currentMode){
		case "Off":
			if(state.coolModeEnabled){
				log.debug "Cool mode enabled"
				cmds << zwave.thermostatModeV2.thermostatModeSet(mode: 2).format()
				cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 2).format()
				cmds << zwave.thermostatModeV2.thermostatModeGet().format()
			}else{
				log.debug "Cool mode not enabled"
				cmds << zwave.thermostatModeV2.thermostatModeSet(mode: 1).format()
				cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1).format()
				cmds << zwave.thermostatModeV2.thermostatModeGet().format()
			}
			break;
		case "Heat":
			cmds << zwave.thermostatModeV2.thermostatModeSet(mode: 0).format()
			cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1).format()
			cmds << zwave.thermostatModeV2.thermostatModeGet().format()
			break;
		case "Cool":
			cmds << zwave.thermostatModeV2.thermostatModeSet(mode: 0).format()
			cmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 2).format()
			cmds << zwave.thermostatModeV2.thermostatModeGet().format()
			break;
	}
	return delayBetween(cmds, 250)
}
/**
 * Function for increasing the current setpoint.
 *
 * @param void
 * @return void
*/
def tempUp() {
	log.debug "tempUp call"
	def spIncrease = roundToHalf(device.currentValue("thermostatSetpoint")) + 0.5
    log.debug spDecrease
	setSpValue(spIncrease)
}
/**
 * Function for decreasing the current setpoint.
 *
 * @param void
 * @return void
*/
def tempDown() {
	log.debug "tempDown call"
	def spDecrease = roundToHalf(device.currentValue("thermostatSetpoint")) - 0.5
    log.debug spDecrease
	setSpValue(spDecrease)
}
/**
 * Function that is used to determine the new setpoint value and type.
 *
 * @param setpoint - a numeric value for the new thermostat setpoint
 * @return List of commands that will be executed in sequence with 250 ms delay inbetween.
*/
def setSpValue(setpoint){
	def currentMode = device.currentValue("thermostatMode")
	def cmds = []
	switch(currentMode){
		case "Off":
			log.debug "In Off mode, setting all setpoints to new value"
			sendEvent(name: "heatingSetpoint", value: setpoint, displayed: false)
			sendEvent(name: "thermostatSetpoint", value: setpoint, displayed: false)
			sendEvent(name: "coolingSetpoint", value: setpoint, displayed: false)
			if(state.coolModeEnabled){
				if(location.temperatureScale == "C"){
					cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: setpoint, setpointType: 2, size: 2).format()
				}else{
					cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: setpoint, setpointType: 2, size: 2).format()
				}
			}else{
				if(location.temperatureScale == "C"){
					cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: setpoint, setpointType: 1, size: 2).format()
				}else{
					cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: setpoint, setpointType: 1, size: 2).format()
				}
			}
			break;
		case "Heat":
			log.debug "In Heat mode, setting Heat and Th setpoints to new value"
			sendEvent(name: "heatingSetpoint", value: setpoint, displayed: false)
			sendEvent(name: "thermostatSetpoint", value: setpoint, displayed: false)
			if(location.temperatureScale == "C"){
				cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: setpoint, setpointType: 1, size: 2).format()
			}else{
				cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: setpoint, setpointType: 1, size: 2).format()
			}
			break;
		case "Cool":
			log.debug "In Cool mode, setting Cool and Th setpoints to new value"
			sendEvent(name: "thermostatSetpoint", value: setpoint, displayed: false)
			sendEvent(name: "coolingSetpoint", value: setpoint, displayed: false)
			if(location.temperatureScale == "C"){
				cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 0, scaledValue: setpoint, setpointType: 2, size: 2).format()
			}else{
				cmds << zwave.thermostatSetpointV2.thermostatSetpointSet(precision: 1, scale: 1, scaledValue: setpoint, setpointType: 2, size: 2).format()
			}
			break;
	}
	return delayBetween(cmds, 250)
}
/**
 * Configuration capability command handler.
 *
 * @param void
 * @return List of commands that will be executed in sequence with 500 ms delay inbetween.
*/
def configure() {
	log.debug "Qubino Flush On Off Thermostat:configure()"
	state.numEndpoints = 0
	state.coolModeEnabled = false
	//log.debug state.numEndpoints
	def assocCmds = []
	assocCmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1).format()
	assocCmds << zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 2).format()
	assocCmds << zwave.thermostatModeV2.thermostatModeGet().format()
	assocCmds << zwave.configurationV1.configurationGet(parameterNumber: 59).format()
	assocCmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 1,scale: 0).format()
	return delayBetween(assocCmds, 500)
}
/**
 * Refresh Power Consumption command handler for updating the cumulative consumption fields in kWh. It will issue a Meter Get command with scale parameter set to kWh.
 *		
 * @param void.
 * @return void.
*/
def refreshPowerConsumption() {
	log.debug "Qubino Flush On Off Thermostat:refreshPowerConsumption()"
	delayBetween([
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
    ], 1000)
}
/**
 * setAssociations command handler that sets user selected association groups. In case no node id is insetred the group is instead cleared.
 * Lifeline association hidden from user influence by design.
 *
 * @param void
 * @return List of Association commands that will be executed in sequence with 500 ms delay inbetween.
*/

def setAssociation() {
	log.debug "Qubino Flush On Off Thermostat:setAssociation()"
	def assocSet = []

	if(settings.assocGroup2 != null){
		def group2parsed = settings.assocGroup2.tokenize(",")
		if(group2parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:assocGroup2).format()
		}else{
			group2parsed = convertStringListToIntegerList(group2parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:group2parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:2).format()
	}
	if(settings.assocGroup3 != null){
		def group3parsed = settings.assocGroup3.tokenize(",")
		if(group3parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:assocGroup3).format()
		}else{
			group3parsed = convertStringListToIntegerList(group3parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:group3parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:3).format()
	}
	if(settings.assocGroup4 != null){
		def group4parsed = settings.assocGroup4.tokenize(",")
		if(group4parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:4, nodeId:assocGroup4).format()
		}else{
			group4parsed = convertStringListToIntegerList(group4parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:4, nodeId:group4parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:4).format()
	}
	if(settings.assocGroup5 != null){
		def group5parsed = settings.assocGroup5.tokenize(",")
		if(group5parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:5, nodeId:assocGroup5).format()
		}else{
			group5parsed = convertStringListToIntegerList(group5parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:5, nodeId:group5parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:5).format()
	}
	if(settings.assocGroup6 != null){
		def group6parsed = settings.assocGroup6.tokenize(",")
		if(group6parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:6, nodeId:assocGroup6).format()
		}else{
			group6parsed = convertStringListToIntegerList(group6parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:6, nodeId:group6parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:6).format()
	}
	if(settings.assocGroup7 != null){
		def group7parsed = settings.assocGroup7.tokenize(",")
		if(group7parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:7, nodeId:assocGroup7).format()
		}else{
			group7parsed = convertStringListToIntegerList(group7parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:7, nodeId:group7parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:7).format()
	}
	if(settings.assocGroup8 != null){
		def group8parsed = settings.assocGroup8.tokenize(",")
		if(group8parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:8, nodeId:assocGroup8).format()
		}else{
			group8parsed = convertStringListToIntegerList(group8parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:8, nodeId:group8parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:8).format()
	}
	if(settings.assocGroup9 != null){
		def group9parsed = settings.assocGroup9.tokenize(",")
		if(group9parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:9, nodeId:assocGroup9).format()
		}else{
			group9parsed = convertStringListToIntegerList(group9parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:9, nodeId:group9parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:9).format()
	}
	if(settings.assocGroup10 != null){
		def group10parsed = settings.assocGroup10.tokenize(",")
		if(group10parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:10, nodeId:assocGroup10).format()
		}else{
			group10parsed = convertStringListToIntegerList(group10parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:10, nodeId:group10parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:10).format()
	}
	if(assocSet.size() > 0){
		return delayBetween(assocSet, 500)
	}
}

/**
 * setConfigurationParams command handler that sets user selected configuration parameters on the device. 
 * In case no value is set for a specific parameter the method skips setting that parameter.
 * Secure mode setting hidden from user influence by design.
 *
 * @param void
 * @return List of Configuration Set commands that will be executed in sequence with 500 ms delay inbetween.
*/

def setConfiguration() {
	log.debug "Qubino Flush On Off Thermostat:setConfiguration()"
	def configSequence = []
	if(settings.param1 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: settings.param1.toInteger()).format()
	}
	if(settings.param2 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: settings.param2.toInteger()).format()
	}
	if(settings.param3 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: settings.param3.toInteger()).format()
	}
	if(settings.param4 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: settings.param4.toInteger()).format()
	}
	if(settings.param5 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: settings.param5.toInteger()).format()
	}
	if(settings.param6 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 6, size: 1, scaledConfigurationValue: settings.param6.toInteger()).format()
	}
	if(settings.param10 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 10, size: 2, scaledConfigurationValue: settings.param10.toInteger()).format()
	}
	if(settings.param11 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 11, size: 2, scaledConfigurationValue: settings.param11.toInteger()).format()
	}
	if(settings.param12 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 12, size: 2, scaledConfigurationValue: settings.param12.toInteger()).format()
	}
	if(settings.param13 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 13, size: 2, scaledConfigurationValue: settings.param13.toInteger()).format()
	}
	if(settings.param40 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 40, size: 1, scaledConfigurationValue: settings.param40.toInteger()).format()
	}
	if(settings.param42 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 42, size: 2, scaledConfigurationValue: settings.param42.toInteger()).format()
	}
	if(settings.param43 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 43, size: 2, scaledConfigurationValue: settings.param43.toInteger()).format()
	}
	if(settings.param44 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 44, size: 2, scaledConfigurationValue: settings.param44.toInteger()).format()
	}
	if(settings.param45 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 45, size: 2, scaledConfigurationValue: settings.param45.toInteger()).format()
	}
	if(settings.param59 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 59, size: 1, scaledConfigurationValue: settings.param59.toInteger()).format()
	}
	if(settings.param60 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 60, size: 2, scaledConfigurationValue: settings.param60.toInteger()).format()
	}
	if(settings.param61 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 61, size: 2, scaledConfigurationValue: settings.param61.toInteger()).format()
	}
	if(settings.param63 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 63, size: 1, scaledConfigurationValue: settings.param63.toInteger()).format()
	}
	if(settings.param70 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 70, size: 2, scaledConfigurationValue: settings.param70.toInteger()).format()
	}
	if(settings.param71 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 71, size: 2, scaledConfigurationValue: settings.param71.toInteger()).format()
	}
	if(settings.param72 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 72, size: 2, scaledConfigurationValue: settings.param72.toInteger()).format()
	}
	if(settings.param73 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 73, size: 2, scaledConfigurationValue: settings.param73.toInteger()).format()
	}
	if(settings.param74 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 74, size: 2, scaledConfigurationValue: settings.param74.toInteger()).format()
	}
	if(settings.param75 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 75, size: 2, scaledConfigurationValue: settings.param75.toInteger()).format()
	}
	if(settings.param76 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 76, size: 1, scaledConfigurationValue: settings.param76.toInteger()).format()
	}
	if(settings.param77 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 77, size: 2, scaledConfigurationValue: settings.param77.toInteger()).format()
	}
	if(settings.param78 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 78, size: 1, scaledConfigurationValue: settings.param78.toInteger()).format()
	}
	if(settings.param100 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 100, size: 1, scaledConfigurationValue: settings.param100.toInteger()).format()
	}
	if(settings.param101 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 101, size: 1, scaledConfigurationValue: settings.param101.toInteger()).format()
	}
	if(settings.param102 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 102, size: 1, scaledConfigurationValue: settings.param102.toInteger()).format()
	}
	if(settings.param110 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 110, size: 2, scaledConfigurationValue: settings.param110.toInteger()).format()
	}	
	if(settings.param120 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, scaledConfigurationValue: settings.param120.toInteger()).format()
	}	
	if(settings.param121 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 121, size: 1, scaledConfigurationValue: settings.param121.toInteger()).format()
	}
	if(settings.param122 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 122, size: 1, scaledConfigurationValue: settings.param122.toInteger()).format()
	}
	if(configSequence.size() > 0){
		return delayBetween(configSequence, 500)
	}
}


/*
*	--------	EVENT PARSER SECTION	--------
*/
/**
 * parse function takes care of parsing received bytes and passing them on to event methods.
 *
 * @param description String type value of the received bytes.
 * @return Parsed result of the received bytes.
*/
def parse(String description) {
	log.debug "Qubino Flush On Off Thermostat: Parsing '${description}'"
	def result = null
    def cmd = zwave.parse(description)
    if (cmd) {
		result = zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
		log.debug "Non-parsed event: ${description}"
    }
    return result
}
/**
 * Event handler for received Sensor Multilevel Report frames. These are for the temperature sensor connected to TS connector.
 *
 * @param void
 * @return Event that updates the temperature values with received values.
*/
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport"
	def resultEvents = []
	resultEvents << createEvent(name:"temperature", value: convertDegrees(location.temperatureScale,cmd), unit:"°"+location.temperatureScale, descriptionText: "Temperature: "+convertDegrees(location.temperatureScale,cmd)+"°"+location.temperatureScale)
	return resultEvents
}
/**
 * Event handler for received Configuration Report frames. Used for debugging purposes and to detect if Cool mode was enabled. 
 *
 * @param void
 * @return void.
*/
def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.configurationv2.ConfigurationReport"
	log.debug cmd
	if(cmd.parameterNumber == 59){
		if(cmd.scaledConfigurationValue == 1){
			state.coolModeEnabled = true
		}else{
			state.coolModeEnabled = false
		}
	}
}
/**
 * Event handler for received MC Encapsulated Meter Report frames.
 *
 * @param void
 * @return List of events to update the power control elements with received values.
*/
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd, physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap command){
	log.debug "Qubino Flush On Off Thermostat:MC_MeterReport"
	def result = []
	switch(cmd.scale){
		case 0:
			result << createEvent(name:"kwhConsumption", value: cmd.scaledMeterValue, unit:"kWh", descriptionText:"${device.displayName} consumed ${cmd.scaledMeterValue} kWh")
			break;
		case 2:
			result << createEvent(name:"power", value: cmd.scaledMeterValue, unit:"W", descriptionText:"${device.displayName} consumes ${cmd.scaledMeterValue} W")
			break;
	}
	return result
}
/**
 * Event handler for received Meter Report frames. Used for displaying W and kWh measurements.
 *
 * @param void
 * @return Power consumption event for W data or kwhConsumption event for kWh data.
*/
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.meterv3.MeterReport"
	def result = []
	switch(cmd.scale){
		case 0:
			result << createEvent(name:"kwhConsumption", value: cmd.scaledMeterValue, unit:"kWh", descriptionText:"${device.displayName} consumed ${cmd.scaledMeterValue} kWh")
			break;
		case 2:
			result << createEvent(name:"power", value: cmd.scaledMeterValue, unit:"W", descriptionText:"${device.displayName} consumes ${cmd.scaledMeterValue} W")
			break;
	}
	return result
}
/**
 * Event handler for received MultiChannelEndPointReport commands. Used to distinguish when the device is in singlechannel or multichannel configuration. 
 *
 * @param cmd communication frame
 * @return commands to set up a MC Lifeline association.
*/
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport"
	if(cmd.endPoints > 0){
		state.numEndpoints = cmd.endPoints;
	}

	def cmds = []
	cmds << response(zwave.associationV1.associationRemove(groupingIdentifier:1).format())
	cmds << response(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 1, nodeId: [0,zwaveHubNodeId,1]).format())
	return cmds
}
/**
 * Event handler for received Multi Channel Encapsulated commands.
 *
 * @param cmd encapsulated communication frame
 * @return parsed event.
*/
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap"
	def encapsulatedCommand = cmd.encapsulatedCommand()
	//log.debug ("Command from endpoint ${cmd.sourceEndPoint}: ${encapsulatedCommand}")
	if (encapsulatedCommand) {
			return zwaveEvent(encapsulatedCommand, cmd)
	}
}
/**
 * Event handler for received Basic Report frames - used for debugging
 *
 * @param cmd encapsulated communication frame
 * @return void
*/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.basicv1.BasicReport"
	log.debug cmd
}
/**
 * Event handler for received Thermostat Setpoint Report frames
 *
 * @param cmd encapsulated communication frame
 * @return event list used to update various setpoint values
*/
def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport"
	def result = []
	def convertedSp = convertSetpoint(location.temperatureScale,cmd)
	log.debug cmd.scaledValue
	log.debug convertedSp
	switch(cmd.scale){
		case 0:
			switch(cmd.setpointType){
				case 1:
					log.debug device.currentValue("thermostatMode")
					log.debug device.currentValue("thermostatSetpoint")
					log.debug device.currentValue("heatingSetpoint")
					result << createEvent(name:"thermostatSetpoint", value: convertedSp, descriptionText:"${device.displayName} setpoint at ${convertedSp}")
					result << createEvent(name:"heatingSetpoint", value: convertedSp, descriptionText:"${device.displayName} heating setpoint at ${convertedSp}")
					break;
				case 2:
					log.debug device.currentValue("thermostatMode")
					log.debug device.currentValue("thermostatSetpoint")
					log.debug device.currentValue("coolingSetpoint")
					result << createEvent(name:"thermostatSetpoint", value: convertedSp, descriptionText:"${device.displayName} setpoint at ${convertedSp}")
					result << createEvent(name:"coolingSetpoint", value: convertedSp, descriptionText:"${device.displayName} heating setpoint at ${convertedSp}")
					break;
			}
			break;
		case 1:
			break;
	}
	return result;
}
/**
 * Event handler for received Thermostat Mode Report frames
 *
 * @param cmd encapsulated communication frame
 * @return event list used to update various thermostat mode settings
*/
def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd){
	log.debug "Qubino Flush On Off Thermostat:physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport"
	def result = []
	def currentSp = device.currentValue("thermostatSetpoint")
	def heatSp = device.currentValue("coolingSetpoint")
	def coolSp = device.currentValue("heatingSetpoint")
	switch(cmd.mode){
		case 0:
			log.debug "Off Mode Report"
			result << createEvent(name:"thermostatOperatingState", value: "idle", descriptionText:"${device.displayName} idle at ${currentSp}.")
			result << createEvent(name:"thermostatMode", value: "Off", descriptionText:"${device.displayName} in Off mode.")
			result << createEvent(name:"modeSetting", value: "Off mode")
			break;
		case 1:
			log.debug "Heat Mode Report"
			result << createEvent(name:"thermostatOperatingState", value: "heating", descriptionText:"${device.displayName} heating at ${heatSp}.")
			result << createEvent(name:"thermostatMode", value: "Heat", descriptionText:"${device.displayName} in Heat mode.")
			result << createEvent(name:"modeSetting", value: "Heat mode")
			break;
		case 2:
			log.debug "Cool Mode Report"
			result << createEvent(name:"thermostatOperatingState", value: "cooling", descriptionText:"${device.displayName} cooling at ${coolSp}.")
			result << createEvent(name:"thermostatMode", value: "Cool", descriptionText:"${device.displayName} in Cool mode.")
			result << createEvent(name:"modeSetting", value: "Cool mode")
			break;
	}
	return result;
}
