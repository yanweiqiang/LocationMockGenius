package com.yan.locationmockgenius

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.yan.locationmockgenius.constant.MockData
import com.yan.locationmockgenius.entity.LMGPoi
import com.yan.locationmockgenius.mocker.LocationMockUtils
import com.yan.locationmockgenius.widget.ListPopupWindow
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {
    override fun showSearchResultList(list: List<LMGPoi>) {
        listPopupWindow.showAsDropDown(list, searchView)
    }

    override fun getContext(): Context {
        return this
    }

    override fun getMapView(): MapView {
        return mapView
    }

    override fun getMap(): BaiduMap {
        return map
    }

    private lateinit var searchView: SearchView
    private lateinit var mapView: MapView
    private lateinit var map: BaiduMap
    private lateinit var presenter: MainPresenter
    private var permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
    private lateinit var listPopupWindow: ListPopupWindow<LMGPoi>
    private var binder: LocationMockService.Binder? = null
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //获取地图控件引用
        mapView = map_view
        map = mapView.map
        map.isMyLocationEnabled = true

        presenter = MainPresenter(this)
        listPopupWindow = ListPopupWindow(this,
                object : ListPopupWindow.KeyPickProxy<LMGPoi> {
                    override fun pick(t: LMGPoi): String {
                        return t.key
                    }
                },
                object : ListPopupWindow.OnPickListener<LMGPoi> {
                    override fun onPick(t: LMGPoi) {
                        MockData.lat = t.lat!!
                        MockData.lon = t.lon!!

                        searchView.isIconified = true

                        when {
                            !toggle.isChecked -> {
                                toggle.isChecked = true
                                startMockService()
                            }
                        }

                        searchView.clearFocus()
                    }
                })

        toggle.setOnClickListener {
            if (toggle.isChecked) {
                startMockService()
            } else {
                stopMockService()
            }
        }

        if (!LocationMockUtils.isAllowMockLocation(this)) {
            LocationMockUtils.openDevSettings(this)
        }

        //悬浮窗权限判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }

        requestAMapPermission()
        startMockService()
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause()
    }

    override fun onDestroy() {
        presenter.stopLocate()
        map.isMyLocationEnabled = false
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy()
        //stopMockService()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.startLocate()
        }
    }

    private fun requestAMapPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, permissions, 0xf1)//自定义的code
        } else {
            presenter.startLocate()
        }
    }

    private fun checkPermission(): Boolean {
        return permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun startMockService() {
        val intent = Intent(baseContext, LocationMockService::class.java)
        startService(intent)
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as LocationMockService.Binder
                toggle.isChecked = binder?.isStarted() ?: false
            }
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopMockService() {
        if (binder != null) {
            unbindService(serviceConnection)
        }
        val intent = Intent(baseContext, LocationMockService::class.java)
        stopService(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView = menu?.findItem(R.id.menu_search)?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    return false
                }

                presenter.searchPoi(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    return false
                }

                presenter.searchPoi(p0)
                return true
            }
        })

        searchView.setOnCloseListener {
            listPopupWindow.dismiss()
            return@setOnCloseListener false
        }

        return super.onCreateOptionsMenu(menu)
    }
}
