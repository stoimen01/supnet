package com.supnet.rooms.list

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.supnet.R
import kotlinx.android.synthetic.main.dialog_room_creator.view.*
import java.lang.IllegalArgumentException

class RoomCreatorFragment : DialogFragment() {

    interface Listener {
        fun onCreateRoom(name: String)
    }

    private val listener by lazy {
        (parentFragment as? Listener) ?:
        throw IllegalArgumentException("Parent fragment must implement RoomCreator listener.")
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val view = LayoutInflater.from(it)
                .inflate(R.layout.dialog_room_creator, null)

            val builder = AlertDialog.Builder(it)
            builder.setView(view)
                .setPositiveButton(R.string.create) { _, _ ->
                    listener.onCreateRoom(view.txtRoomName.text.toString())
                }
                .setNegativeButton(R.string.cancel, null)
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}