package com.webaddicted.kotlinproject.view.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.webaddicted.kotlinproject.global.common.PermissionHelper
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by deepaksharma
 */
abstract class BaseLocation : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener,
    PermissionHelper.Companion.PermissionListener {
    private var isUpdateLocation = false
    private var isShowAddress = false
    private var mGeofenceList: ArrayList<Geofence>? = null
    /**
     * provide user current location single time
     */
    protected fun getLocation() {
        stopLocationUpdates()
        checkPermission()
    }

    /**
     * provide user current location after a perticular time
     *
     * @param timeInterval - location update after time intervel in sec
     * @param fastInterval - fast time interval
     * @param displacement - location update after a perticular distance
     */
    fun getLocation(@NonNull timeInterval: Long, @NonNull fastInterval: Long, @NonNull displacement: Long) {
        stopLocationUpdates()
        this.isUpdateLocation = true
        if (timeInterval > 0) INTERVAL = INTERVAL * timeInterval
        if (fastInterval > 0) FASTEST_INTERVAL = FASTEST_INTERVAL * fastInterval
        //        if (displacement > 0)
        MIN_DISTANCE_CHANGE_FOR_UPDATES = MIN_DISTANCE_CHANGE_FOR_UPDATES * displacement
        checkPermission()
    }

    /**
     * check Gps status & location permission
     */
    private fun checkPermission() {
        mGeofenceList = ArrayList()
        try {
            val locationList = ArrayList<String>()
            locationList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            locationList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (PermissionHelper.requestMultiplePermission(this, locationList, this)) {
                checkGpsLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * check play services & enable gps
     */
    private fun checkGpsLocation() {
        if (checkPlayServices()) {
            buildGoogleApiClient()
        } else {
            Toast.makeText(this, "Play service not available.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * check Google play services status.
     *
     * @return
     */
    private fun checkPlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 1000).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "This device is not supported.",
                    Toast.LENGTH_LONG
                ).show()
            }
            return false
        }
        return true
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
        mLocationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)

        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())

        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(locationSettingsResult: LocationSettingsResult) {

                val status = locationSettingsResult.getStatus()

                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS ->
                        // All location settings are satisfied. The client can initialize location requests here
                        Log.d(TAG, "onResult: SUCCESS")
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.d(TAG, "onResult: RESOLUTION_REQUIRED")
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(this@BaseLocation, 2000)

                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.d(
                        TAG,
                        "onResult: SETTINGS_CHANGE_UNAVAILABLE"
                    )
                    LocationSettingsStatusCodes.CANCELED -> Log.d(TAG, "onResult: CANCELED")
                }//                        getLocation();
            }

        })
    }
    override fun onConnected(arg0: Bundle?) {
        // Once connected with google api, get the location
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        //        Criteria criteria = new Criteria();
        //        criteria.setAltitudeRequired(false);
        //        criteria.setBearingRequired(true);
        //        criteria.setSpeedRequired(false);
        //        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //        criteria.setCostAllowed(true);
        if (mGoogleApiClient!!.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        } else {
            buildGoogleApiClient()
        }
    }

    protected fun stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this as com.google.android.gms.location.LocationListener
            )
        }
        cleanUpLocation()
    }

    override fun onConnectionSuspended(arg0: Int) {
        Log.d(TAG, "onConnectionSuspended: ")
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(
            TAG,
            "login  Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode()
        )
        mGoogleApiClient!!.connect()
    }


    //        [Permission Start]
    override fun onRequestPermissionsResult(@NonNull requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onPermissionGranted(mCustomPermission: List<String>) {
        checkGpsLocation()
    }

    override fun onPermissionDenied(mCustomPermission: List<String>) {

    }
    //       [Permission Stop]

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }


    /**
     * location update interval
     */
    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        if (isUpdateLocation) {
            mLocationRequest.interval = INTERVAL
            mLocationRequest.fastestInterval = FASTEST_INTERVAL
            mLocationRequest.smallestDisplacement= MIN_DISTANCE_CHANGE_FOR_UPDATES
        }
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    override fun onLocationChanged(location: android.location.Location) {
        if (isShowAddress)
            getAddress(location)
        else
            getCurrentLocation(location, null)
    }

    /**
     * provide user current address on the bases of lat long
     *
     * @param location
     */
    private fun getAddress(@NonNull location: android.location.Location) {
        var strAddress = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAddress = strReturnedAddress.toString()
                Log.d(TAG, "Current address - $strReturnedAddress")
            } else {
                Log.d(TAG, "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Can't get Address! - $e")
        }

        getCurrentLocation(location, strAddress)
    }

    protected abstract fun getCurrentLocation(@NonNull location: android.location.Location, @NonNull address: String?)

    protected fun isAddressEnabled(showAddress: Boolean) {
        isShowAddress = showAddress
    }

    fun cleanUpLocation() {
        mGoogleApiClient = null
        mLocationRequest = null
        isShowAddress = false
        isUpdateLocation = false
        INTERVAL = 1000
        FASTEST_INTERVAL = 1000
        MIN_DISTANCE_CHANGE_FOR_UPDATES = 1F
    }

    companion object {
        private val TAG = BaseLocation::class.java.simpleName
        // The minimum distance to change Updates in meters
        private var INTERVAL: Long = 1000 // 1 sec
        // The minimum time between updates in milliseconds
        private var FASTEST_INTERVAL: Long = 1000 // 1 sec
        // The minimum distance to change Updates in meters
        private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1F // 1 meters

        private var mGoogleApiClient: GoogleApiClient? = null

        private var mLocationRequest: LocationRequest? = null

        /**
         * get distance between two lat long
         *
         * @param currlat
         * @param currlng
         * @param givenlat
         * @param givenlng
         * @return distane in miles
         */
        fun checkDistance(
            currlat: Double,
            currlng: Double,
            givenlat: Double,
            givenlng: Double
        ): Double {
            val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output
            val dLat = Math.toRadians(givenlat - currlat)
            val dLng = Math.toRadians(givenlng - currlng)
            val sindLat = Math.sin(dLat / 2)
            val sindLng = Math.sin(dLng / 2)
            val a = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(currlat)) * Math.cos(Math.toRadians(givenlat)))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            return earthRadius * c // output distance, in MILES
        }
    }

}
