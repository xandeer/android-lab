package com.github.xandeer.android.lab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(val icon: ImageVector) {
  HOME(icon = Icons.Filled.Home),
  BOOKS(icon = Icons.Filled.Book),
  BOTTOM_SHEET(icon = Icons.Filled.NightShelter),
  Me(icon = Icons.Filled.Man);

  companion object {
    fun fromRoute(route: String?): Destination =
      when (route?.substringBefore("/")) {
        BOOKS.name -> BOOKS
        BOTTOM_SHEET.name -> BOTTOM_SHEET
        Me.name -> Me
        HOME.name -> HOME
        null -> HOME
        else -> throw IllegalArgumentException("Route $route is not recognized.")
      }
  }
}
