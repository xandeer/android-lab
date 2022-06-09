@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.xandeer.androidlab

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.github.xandeer.androidlab.fab.*
import com.github.xandeer.androidlab.ui.components.LabTabRow
import com.github.xandeer.androidlab.ui.theme.AndroidLabTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { LabApp() }
  }
}

@Composable
fun LabApp() {
  AndroidLabTheme {
    val allScreens = Destination.values().toList()
    val navController = rememberNavController()
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = Destination.fromRoute(
      backstackEntry.value?.destination?.route
    )
    Scaffold(bottomBar = {
      LabTabRow(
        allScreens = allScreens,
        onTabSelected = { navController.navigate(it.name) },
        currentScreen = currentScreen
      )
    }) {
      LabNavHost(navController = navController, modifier = Modifier.padding(it))
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LabNavHost(
  navController: NavHostController,
  modifier: Modifier
) {
  NavHost(
    navController = navController,
    startDestination = Destination.HOME.name,
    modifier = modifier
  ) {
    composable(Destination.HOME.name) {
      Body(nav = it)
    }
    composable(Destination.BOOKS.name) { nav ->
      var fabState by remember {
        mutableStateOf(FabMenuState.COLLAPSED)
      }
      Scaffold(floatingActionButton = {
        val context = LocalContext.current
        FabMenu(
          icon = Icons.Filled.Add,
          items = listOf(
            FabMenuItem(Destination.BOOKS, Icons.Filled.Book, "Book"),
            FabMenuItem(Destination.Me, Icons.Filled.Man, "Man")
          ),
          state = fabState,
          expandLabel = "Add",
          stateChanged = { fabState = it },
          onFabClicked = {
            toast(context, Destination.HOME.name)
          },
          onMenuItemClicked = {
            toast(context, (it.identifier as Destination).name)
          }
        )
      }) {
        Body(nav = nav)
        FabModal(state = fabState) {
          fabState = FabMenuState.COLLAPSED
        }
      }
    }
    composable(Destination.Me.name) {
      Body(nav = it)
    }
  }
}

private fun toast(context: Context, msg: String) {
  Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

@Composable
fun Body(nav: NavBackStackEntry) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
  ) {
    Text(text = nav.destination.route ?: "Android Lab")
  }
}