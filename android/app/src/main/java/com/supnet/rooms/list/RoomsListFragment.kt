package com.supnet.rooms.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.*
import com.supnet.rooms.list.RoomsListViewModel.RoomsListCommand.*
import com.supnet.rooms.list.RoomsListViewModel.RoomsListState.*
import com.supnet.signaling.entities.Room
import kotlinx.android.synthetic.main.fragment_rooms_list.*
import java.util.*

class RoomsListFragment : BaseFragment(),
    RoomCreatorDialogFragment.Listener {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, RoomsListViewModelFactory(Supnet.roomsManager))
            .get(RoomsListViewModel::class.java)
    }

    private val roomsAdapter by lazy { RoomsAdapter(viewModel::onJoinRoom) }

    override fun getViewId() = R.layout.fragment_rooms_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listRooms.adapter = roomsAdapter
        listRooms.layoutManager = LinearLayoutManager(context)

        barLoading.hide()
        txtRoomsEmpty.hide()
        listRooms.hide()

        btnAdd.setOnClickListener {
            RoomCreatorDialogFragment().show(childFragmentManager, "")
        }

        observe(viewModel.getLiveState(), this::onLiveState)
        observe(viewModel.getLiveCommands(), this::onLiveCommand)
    }

    private fun onLiveState(state: RoomsListViewModel.RoomsListState) = when (state) {
        Loading -> {
            barLoading.show()
            txtRoomsEmpty.hide()
            listRooms.hide()
        }
        Empty -> {
            txtRoomsEmpty.show()
            listRooms.hide()
            barLoading.hide()
        }
        is Available -> {
            listRooms.show()
            barLoading.hide()
            txtRoomsEmpty.hide()
            roomsAdapter.update(state.rooms)
        }
    }

    private fun onLiveCommand(cmd: Command<RoomsListViewModel.RoomsListCommand?>) {
        when (cmd.getData()) {
            SHOW_ROOM_CREATE_ERROR -> {
                showToast("Cannot create room at the moment")
                listRooms.show()
                barLoading.hide()
                txtRoomsEmpty.hide()
            }
            SHOW_ROOM_JOIN_ERROR ->  {
                showToast("Cannot join room at the moment")
                listRooms.show()
                barLoading.hide()
                txtRoomsEmpty.hide()
            }
            null -> {/*no-op*/}
        }
    }

    override fun onCreateRoom(name: String) {
        viewModel.onCreateRoom(name)
    }

    class RoomsAdapter(
        private val listener: (UUID) -> Unit
    ) : RecyclerView.Adapter<RoomsAdapter.MyViewHolder>() {

        private var rooms = listOf<Room>()

        fun update(rooms: List<Room>) {
            this.rooms = rooms
            notifyDataSetChanged()
        }

        override fun getItemCount() = rooms.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_room, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
            holder.titleView.text = rooms[i].name
            holder.btnView.setOnClickListener {
                listener(rooms[i].id)
            }
        }

        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.txtTitle)
            val btnView: Button = view.findViewById(R.id.btnJoin)
        }
    }
}
