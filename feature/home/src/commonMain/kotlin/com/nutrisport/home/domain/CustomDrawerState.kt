package com.nutrisport.home.domain

enum class CustomDrawerState {
    Opened,
    Closed
}

fun CustomDrawerState.isOpened() = this == CustomDrawerState.Opened

fun CustomDrawerState.opposite() : CustomDrawerState{
    return if (isOpened()) CustomDrawerState.Closed
    else CustomDrawerState.Opened
}