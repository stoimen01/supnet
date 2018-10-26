package com.supnet.rooms.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.AlertDialogFragment
import com.supnet.common.BaseFragment
import com.supnet.common.getNavigator
import com.supnet.common.observe
import com.supnet.navigation.BackPressHandler
import com.supnet.signaling.entities.Message
import kotlinx.android.synthetic.main.fragment_room.*
import java.util.UUID

class RoomFragment : BaseFragment(), BackPressHandler, AlertDialogFragment.Listener {

    private val viewModel by lazy {
        val roomId = UUID.fromString(arguments?.getString(ROOM_ID_KEY))
        return@lazy ViewModelProviders
            .of(this, RoomViewModelFactory(roomId, Supnet.signalingClient, getNavigator()))
            .get(RoomViewModel::class.java)
    }

    override fun getViewId() = R.layout.fragment_room

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = MessagesAdapter {}
        listMessages.adapter = adapter
        listMessages.layoutManager = LinearLayoutManager(context)

        toolbarRoom.setNavigationOnClickListener {
            showLeaveConfirmationDialog()
        }

        observe(viewModel.getLiveRoom()) {
            toolbarRoom.title = it.name
        }

        observe(viewModel.getLiveMessages()) {
            adapter.update(it)
        }
    }

    override fun onBackPressed(): Boolean {
        showLeaveConfirmationDialog()
        return true
    }

    private fun showLeaveConfirmationDialog() {
        AlertDialogFragment.newInstance(getString(R.string.room_leave_question))
            .show(childFragmentManager, null)
    }

    override fun onConfirm() {
        viewModel.onLeaveRoom()
    }

    companion object {
        private const val ROOM_ID_KEY = "ROOM_ID_KEY"

        fun newInstance(roomId: UUID): RoomFragment {
            val bundle = Bundle()
            bundle.putString(ROOM_ID_KEY, roomId.toString())
            val fragment = RoomFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    class MessagesAdapter(
        private val listener: (String) -> Unit
    ) : RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

        private var messages = listOf<Message>()

        fun update(messages: List<Message>) {
            this.messages = messages
            notifyDataSetChanged()
        }

        override fun getItemCount() = messages.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_message, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
            holder.userNameView.text = messages[i].author.name
            holder.msgView.text = messages[i].content
            holder.view.setOnClickListener {
                listener(messages[i].author.name)
            }
        }

        class MyViewHolder(
            val view: View,
            val userNameView: TextView = view.findViewById(R.id.txtUserName),
            val msgView: TextView = view.findViewById(R.id.txtMsg)
        ) : RecyclerView.ViewHolder(view)
    }

}