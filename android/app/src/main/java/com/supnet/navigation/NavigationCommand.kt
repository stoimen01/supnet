package com.supnet.navigation

sealed class NavigationCommand {
    object ShowLogin : NavigationCommand()
    object ShowLoading : NavigationCommand()
    object ShowRooms : NavigationCommand()
    object ShowError : NavigationCommand()
    object ShowRoom : NavigationCommand()
}