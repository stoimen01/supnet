package com.supnet.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.Supnet
import kotlinx.android.synthetic.main.fragment_rooms.*

class RoomsFragment : Fragment(), RoomCreatorFragment.Listener {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, RoomsViewModelFactory(Supnet.signalingClient))
            .get(RoomsViewModel::class.java)
    }

    private val roomsAdapter = RoomsAdapter {
        viewModel.onJoinRoom(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listRooms.adapter = roomsAdapter
        listRooms.layoutManager = LinearLayoutManager(context)

        btnAdd.setOnClickListener {
            RoomCreatorFragment().show(childFragmentManager, "")
        }

        viewModel.getRooms().observe(this, Observer {
            roomsAdapter.update(it)
        })
    }

    override fun onCreateRoom(name: String) {
        viewModel.onCreateRoom(name)
    }

    class RoomsAdapter(
        private val listener: (String) -> Unit
    ) : RecyclerView.Adapter<RoomsAdapter.MyViewHolder>() {

        private var rooms = listOf<String>()

        fun update(rooms: List<String>) {
            this.rooms = rooms
            notifyDataSetChanged()
        }

        override fun getItemCount() = rooms.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomsAdapter.MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_room, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
            holder.titleView.text = rooms[i]
            holder.btnView.setOnClickListener {
                listener(rooms[i])
            }
        }

        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.txtTitle)
            val btnView: Button = view.findViewById(R.id.btnJoin)
        }
    }
}