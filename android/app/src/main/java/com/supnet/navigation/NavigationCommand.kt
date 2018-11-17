package com.supnet.navigation

import java.util.*

sealed class NavigationCommand {
    object ShowLoading : NavigationCommand()
    object ShowRooms : NavigationCommand()
    object ShowError : NavigationCommand()
    object ShowRoom : NavigationCommand()
    data class LogMessage(val data: String): NavigationCommand()
}