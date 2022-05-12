@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.xandeer.android.lab

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
import com.github.xandeer.android.fab.*
import com.github.xandeer.android.lab.ui.components.LabTabRow
import com.github.xandeer.android.lab.ui.theme.AndroidLabTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { LabApp() }
  }
}

@Composable
fun LabApp() {
  AndroidLabTheme {
    val allScreens = LabScreen.values().toList()
    val navController = rememberNavController()
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = LabScreen.fromRoute(
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

@Composable
fun LabNavHost(
  navController: NavHostController,
  modifier: Modifier
) {
  NavHost(
    navController = navController,
    startDestination = LabScreen.BOOKS.name,
    modifier = modifier
  ) {
    composable(LabScreen.HOME.name) {
      Body(nav = it)
    }
    composable(LabScreen.BOOKS.name) { nav ->
      var fabState by remember {
        mutableStateOf(FabMenuState.COLLAPSED)
      }
      Scaffold(floatingActionButton = {
        val context = LocalContext.current
        FabMenu(
          icon = Icons.Filled.Add,
          items = listOf(
            FabMenuItem(LabScreen.BOOKS, Icons.Filled.Book, "Book"),
            FabMenuItem(LabScreen.Me, Icons.Filled.Man, "Man")
          ),
          state = fabState,
          expandLabel = "Add",
          stateChanged = { fabState = it },
          onFabClicked = {
            toast(context, LabScreen.HOME.name)
          },
          onMenuItemClicked = {
            toast(context, (it.identifier as LabScreen).name)
          }
        )
      }) {
        Body(nav = nav)
        FabMask(state = fabState) {
          fabState = FabMenuState.COLLAPSED
        }
      }
    }
    composable(LabScreen.Me.name) {
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