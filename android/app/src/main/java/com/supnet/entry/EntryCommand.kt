package com.supnet.entry

sealed class EntryCommand {
    object ShowSignIn : EntryCommand()
    object ShowSignUp : EntryCommand()
    object ShowBack: EntryCommand()
}