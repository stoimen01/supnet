package com.supnet.navigation

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.hide
import com.supnet.common.show
import com.supnet.navigation.NavigationViewModel.NavigationCommand.*
import com.supnet.rooms.room.RoomFragment
import com.supnet.rooms.list.RoomsListFragment
import com.supnet.xirsys.Xirsys
import com.supnet.xirsys.XirsysResponse
import kotlinx.android.synthetic.main.activity_navigation.*
import org.webrtc.PeerConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NavigationActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, NavigationViewModelFactory(Supnet.signalingClient))
            .get(NavigationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        barLoading.hide()
        txtConnectionError.hide()

        viewModel.getCommands().observe(this, Observer {
            onNavigationCommand(it.getData())
        })
    }

    private fun onNavigationCommand(cmd: NavigationViewModel.NavigationCommand?) = when (cmd) {
        ShowLoading -> {
            barLoading.show()
            txtConnectionError.hide()
        }
        ShowRooms -> {
            showFragment(RoomsListFragment())
            barLoading.hide()
            txtConnectionError.hide()
            Toast.makeText(this, "CONNECTED", Toast.LENGTH_SHORT).show()
        }
        ShowError -> {
            fragmentContainer.hide()
            barLoading.hide()
            txtConnectionError.show()
        }
        is ShowRoom -> {
            showFragment(RoomFragment.newInstance(cmd.roomId))
        }
        null -> { /* no-op */ }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, null)
            .commit()
    }

    private fun getIceServers() {

        val data = "Supnet:xxx".toByteArray(Charsets.UTF_8)

        val authToken = "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)

        Xirsys.client.getIceServers(authToken).enqueue(object : Callback<XirsysResponse> {
            override fun onFailure(call: Call<XirsysResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<XirsysResponse>, response: Response<XirsysResponse>) {
                val body = response.body()
                val servers = body?.let {
                    val iceServers = body.data
                    iceServers.servers.map { iceServer ->
                        if (iceServer.credential == null) {
                            PeerConnection.IceServer.builder(iceServer.url)
                                .createIceServer()
                        } else {
                            PeerConnection.IceServer.builder(iceServer.url)
                                .setUsername(iceServer.username)
                                .setPassword(iceServer.credential)
                                .createIceServer()
                        }
                    }
                }

                if (servers == null) {
                    Log.d("ACTIVITY", "SERVERS NOT FOUND")
                } else {
                    Log.d("ACTIVITY", "SERVERS FOUND")
                }

            }
        })
    }

}
