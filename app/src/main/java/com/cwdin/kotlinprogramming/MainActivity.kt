package com.cwdin.kotlinprogramming

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.cwdin.kotlinprogramming.databinding.ActivityMainBinding
import com.cwdin.kotlinprogramming.utils.Constants


class MainActivity : AppCompatActivity(), EventHandler{

    // val is constant variable, Immutable and can initialized only once.
    private val tag = "MainActivity"

    // late initialization, can only be a var, can only be non-null type.
    private lateinit var binding: ActivityMainBinding

    // val is a general variable, is mutable and can be assigned multiple times.
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Data binding to introduce layout file.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.eventHandler = this

        // Get Bluetooth adapter.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Close application if bluetoothAdapter is NULL
        if(bluetoothAdapter == null){
            Log.e(tag, "Device does not Support Bluetooth")
            finish()
            return
        }

        if(!bluetoothAdapter.isEnabled){
            enableBluetooth()
        }else {
            Log.e(tag, "Bluetooth is Enabled")
            checkPermission()
        }

    }


    // Override function of Event handler
    override fun onScanButtonClicked(view: View) {
        Log.e(tag, "Scan Button clicked")
    }

    // Start Activity to enable Bluetooth and get response in this Activity.
    private fun enableBluetooth(){

        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBluetoothIntent, Constants.ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    // Get the result back here. To know if Bluetooth is enabled or not.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.ENABLE_BLUETOOTH_REQUEST_CODE){

            // Bluetooth is enabled
            if(resultCode == Activity.RESULT_OK){
                Log.e(tag, "Bluetooth enabled successfully")
                checkPermission()
            }else{
                Log.e(tag, "Failed to enabled Bluetooth")
            }
        }
    }

    // Check for Permission required for this Application.
    private fun checkPermission(){

        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        // Check if location permission is denied.
        // If multiple permissions are required, check each of them, if they are granted. Same as locationPermission
        // in if condition use || operator.
        if(locationPermission != PackageManager.PERMISSION_GRANTED ){

            // Request for run time permission
            requestRuntimePermission()
        }else {
            checkLocationStatus()
        }
    }


    // Request runtime permission.
    private fun requestRuntimePermission() {

        // arrayOf create String array of required permission
        // If multiple permissions are required, add it to permissions array.
        val permissions = arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, Constants.RUNTIME_PERMISSION_REQUEST_CODE)
    }

    // Get results: Permission Granted or Denied.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.RUNTIME_PERMISSION_REQUEST_CODE){

            if(!grantResults.isEmpty()){

                for ( i in grantResults.indices){
                    val x = if(grantResults[i] == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"
                    Log.e(tag, permissions[i]+" is "+x)
                }

                checkLocationStatus()

            }else{
                Log.e(tag, "Grant results is empty")
            }

        }
    }


    // Check if Location service is enabled.
    private fun checkLocationStatus(){

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showLocationAlertMessage()
        }else{
            Log.e(tag, "Location is enabled")
        }

    }


    // Show dialog to enable location.
    private fun showLocationAlertMessage(){

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Kotlin app makes use of location for Bluetooth device scanning, please enable it")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialogInterface, i ->
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))}
                .setNegativeButton("No") { dialogInterface, i ->  dialogInterface.cancel()}

        val alert = builder.create()
        alert.show()

    }


}


