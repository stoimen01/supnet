package com.supnet.root

sealed class RootCommand {
    object ShowEntryFlow : RootCommand()
    object ShowIndoorFlow : RootCommand()
}