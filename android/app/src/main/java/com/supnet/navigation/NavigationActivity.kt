package com.supnet.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.hide
import com.supnet.common.observeCommands
import com.supnet.common.show
import com.supnet.navigation.NavigationCommand.*
import com.supnet.rooms.room.RoomFragment
import com.supnet.rooms.list.RoomsListFragment
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, NavigationViewModelFactory(Supnet.roomsManager))
            .get(NavigationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        observeCommands(viewModel.getCommands(), this::onNavigationCommand)
    }

    private fun onNavigationCommand(cmd: NavigationCommand) = when (cmd) {

        ShowLoading -> {
            removeFragments()
            barLoading.show()
            txtConnectionError.hide()
        }

        ShowError -> {
            removeFragments()
            barLoading.hide()
            txtConnectionError.show()
        }

        ShowRooms -> {
            showFragment(RoomsListFragment())
            barLoading.hide()
            txtConnectionError.hide()
        }

        ShowRoom -> {
            showFragment(RoomFragment())
            barLoading.hide()
            txtConnectionError.hide()
        }

    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is BackPressHandler && fragment.onBackPressed()) return
        super.onBackPressed()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, null)
            .commit()
    }

    private fun removeFragments() = with (supportFragmentManager) {
        fragments.forEach { fragment ->
            if (fragment != null) {
                beginTransaction()
                    .remove(fragment)
                    .commit()
            }
        }
    }

}
