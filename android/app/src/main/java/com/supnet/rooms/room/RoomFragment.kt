package com.supnet.rooms.room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.Supnet
import com.supnet.common.AlertDialogFragment
import com.supnet.common.BaseFragment
import com.supnet.common.observe
import com.supnet.common.showToast
import com.supnet.navigation.BackPressHandler
import com.supnet.signaling.entities.Message
import kotlinx.android.synthetic.main.fragment_room.*

class RoomFragment : BaseFragment(), BackPressHandler, AlertDialogFragment.Listener {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, RoomViewModelFactory(Supnet.roomsManager))
            .get(RoomViewModel::class.java)
    }

    override fun provideViewId() = R.layout.fragment_room

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = MessagesAdapter { showToast("message clicked from $it") }
        listMessages.adapter = adapter
        listMessages.layoutManager = LinearLayoutManager(context)

        toolbarRoom.setNavigationOnClickListener { showLeaveConfirmationDialog() }

        btnSend.setOnClickListener { viewModel.sendMessage(txtMsgContent.text.toString()) }

        observe(viewModel.getLiveRoomData()) { (room, messages) ->
            if (toolbarRoom.title?.toString() != room.name) {
                toolbarRoom.title = room.name
            }
            adapter.update(messages.toList())
        }
    }

    override fun onBackPressed(): Boolean {
        showLeaveConfirmationDialog()
        return true
    }

    private fun showLeaveConfirmationDialog() {
        AlertDialogFragment
            .newInstance(getString(R.string.room_leave_question))
            .show(childFragmentManager, null)
    }

    override fun onConfirm() = viewModel.onLeaveRoom()

    class MessagesAdapter(
        private val listener: (String) -> Unit
    ) : RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

        private val messages = mutableListOf<Message>()

        fun update(newMessages: List<Message>) {
            val diffResult = DiffUtil.calculateDiff(MsgDiffCallback(messages, newMessages));
            messages.clear()
            messages.addAll(newMessages)
            diffResult.dispatchUpdatesTo(this)
        }

        override fun getItemCount() = messages.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_message, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
            holder.userNameView.text = messages[i].author
            holder.msgView.text = messages[i].content
            holder.view.setOnClickListener {
                listener(messages[i].author)
            }
        }

        class MyViewHolder(
            val view: View,
            val userNameView: TextView = view.findViewById(R.id.txtUserName),
            val msgView: TextView = view.findViewById(R.id.txtMsg)
        ) : RecyclerView.ViewHolder(view)

        class MsgDiffCallback(
            private val oldMessages: List<Message>,
            private val newMessages: List<Message>
        ) : DiffUtil.Callback() {

            override fun getOldListSize() = oldMessages.size

            override fun getNewListSize() = newMessages.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldMessages[oldItemPosition].content == newMessages[newItemPosition].content

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldMessages[oldItemPosition] == newMessages[newItemPosition]
        }

    }

}