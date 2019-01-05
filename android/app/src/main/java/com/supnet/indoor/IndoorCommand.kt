package com.supnet.indoor

sealed class IndoorCommand {
    object ShowFriends : IndoorCommand()
    object ShowGadgets : IndoorCommand()
    object ShowSettings : IndoorCommand()
    object ShowConnection : IndoorCommand()
}