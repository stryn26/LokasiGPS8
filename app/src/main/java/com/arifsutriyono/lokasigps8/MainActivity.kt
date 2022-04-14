package com.arifsutriyono.lokasigps8

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.arifsutriyono.lokasigps8.databinding.ActivityMainBinding
import java.util.*

//import library yang dibutuhkan

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater) //binding digunakan untuk melakukan interaksi dengan layout utama
        setContentView(mainBinding.root)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mainBinding.buttonLocation.setOnClickListener{  //meelakukan listener atau mendengarkan perintah dari buttonLocation
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")

    private fun getLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task -> //listener ketika data sudah diambil maka akan di eksekusi langkah selanjutnya
                    val location: Location? = task.result //mengambil data location seperti latitude dan longitude
                    if(location != null){
                        val geocoder = Geocoder(this,Locale.getDefault())
                        val list:List<Address> =
                            geocoder.getFromLocation(location.latitude,location.longitude,1) //mengambil data wilayah dari latitude dan longitude yang sudah didapatkan kemudian didapatkan data lebih spesifik

                        mainBinding.apply{//merubah atau berinteraksi dengan layout
                            latitude.text = "Latitude\n${list[0].latitude}"
                            longitude.text = "Longitude\n${list[0].longitude}"
                            namaNegara.text = "Nama Negara\n${list[0].countryName}"
                            wilayah.text = "Wilayah\n${list[0].locality}"
                            alamat.text = "Alamat\n${list[0].getAddressLine(0)}"
                            //mengirimkan data ke layout kemudian akan ditampilkan
                        }
                    }
                }
            }
            else{
                Toast.makeText(this,"tolong aktifkan GPS ",Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) //berfungsi untuk membuka aplikasi setting untuk mengaktifkan layanan lokasi
                startActivity(intent)
            }
        }
        else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )//melakukan return ketika salah satu atau dua metode pelacakan lokasi aktif atau bernilai true
    }
    private fun checkPermissions( ):Boolean {
        if(ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
            this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
            //cek ijin untuk lokasi perkiraan dan lokasi gps secara akurat
        ){
            return true
        }
        return false
    }
    private fun requestPermissions() {
       ActivityCompat.requestPermissions(this,arrayOf(//meminta ijin lokasi kepasa User
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
       ),
           permissionId
       )
    }

    override fun onRequestPermissionsResult(
        requestCode:Int, permissions: Array<String>,grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if (requestCode == permissionId){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                getLocation()
            }
        }
    }
}