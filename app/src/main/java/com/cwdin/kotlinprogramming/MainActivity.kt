package com.cwdin.kotlinprogramming

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cwdin.kotlinprogramming.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(){

    private val tag = "MainActivity"
    lateinit var binding: ActivityMainBinding
    lateinit var bluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null ){
            showToast("Device does not support Bluetooth")
            return
        }


        if(!bluetoothAdapter.isEnabled){
            enableBluetooth()
        }

    }


    /**
     * Enable Bluetooth
     * */
    private fun enableBluetooth(){

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, Constants.BLUETOOTH_ENABLE_REQUEST_CODE)
    }


    /**
     * On Activity result
     * To get the response from another activity
     *
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.BLUETOOTH_ENABLE_REQUEST_CODE){

            if(resultCode == Activity.RESULT_OK){
                Log.e(tag, "Bluetooth enabled successfully")
            }else{
                Log.e(tag, "Bluetooth not enabled")
            }

        }
    }

    /**
     * Show Toast message
     * @message: Message to be displayed on Toast.
     * */
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
