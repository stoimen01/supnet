package com.supnet.entry

sealed class EntryCommand {
    object ShowLogin : EntryCommand()
    object ShowRegister : EntryCommand()
    object BackFromRegister: EntryCommand()
}