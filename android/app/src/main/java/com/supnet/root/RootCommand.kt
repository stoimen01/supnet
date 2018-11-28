package com.supnet.root

sealed class RootCommand {
    object ShowEntryFlow : RootCommand()
    object ShowIndoorFlow : RootCommand()
    data class ShowErrorMessage(val msg: String) : RootCommand()
}