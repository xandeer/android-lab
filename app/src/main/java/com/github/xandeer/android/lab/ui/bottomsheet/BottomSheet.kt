@file:OptIn(ExperimentalMaterialApi::class)

package com.github.xandeer.android.lab.ui.bottomsheet

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BottomSheetBody() {
  val state = rememberBottomSheetBodyState()
  ModalBottomSheetLayout(
    sheetContent = { BottomSheetContent(state) },
    sheetState = state.bottomSheetState,
    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    sheetBackgroundColor = MaterialTheme.colorScheme.primary
  ) {
    Scaffold(
      topBar = {
        TopAppBar {
          Text(
            text = "Bottom Sheet",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp, 0.dp)
          )
        }
      },
      backgroundColor = MaterialTheme.colorScheme.background
    ) {
      MainScreen(state)
    }
  }
}

private class BottomSheetBodyState(
  val bottomSheetState: ModalBottomSheetState,
  val scope: CoroutineScope
)

@Composable
private fun rememberBottomSheetBodyState(
  bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
  scope: CoroutineScope = rememberCoroutineScope()
) = remember {
  BottomSheetBodyState(bottomSheetState, scope)
}

@Composable
private fun MainScreen(state: BottomSheetBodyState) {
  Column(
    Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(
      colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
      onClick = {
        state.scope.launch {
          state.bottomSheetState.show()
        }
      }) {
      Text(
        text = "Open Modal Bottom Sheet Layout",
        color = Color.White
      )
    }
  }
}

@Composable
private fun BottomSheetContent(state: BottomSheetBodyState) {
  val context = LocalContext.current
  fun clickItem(title: String) {
    Toast.makeText(
      context,
      title,
      Toast.LENGTH_SHORT
    ).show()
    state.scope.launch {
      state.bottomSheetState.hide()
    }
  }

  Column {
    BottomSheetListItem(
      icon = Icons.Filled.Share,
      title = "Share",
      onItemClick = { title ->
        clickItem(title)
      })
    BottomSheetListItem(
      icon = Icons.Filled.Link,
      title = "Get link",
      onItemClick = { title ->
        clickItem(title)
      })
    BottomSheetListItem(
      icon = Icons.Filled.Edit,
      title = "Edit name",
      onItemClick = { title ->
        clickItem(title)
      })
    BottomSheetListItem(
      icon = Icons.Filled.Delete,
      title = "Delete collection",
      onItemClick = { title ->
        clickItem(title)
      })
  }
}

@Composable
private fun BottomSheetListItem(icon: ImageVector, title: String, onItemClick: (String) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = { onItemClick(title) })
      .height(56.dp)
      .background(color = MaterialTheme.colorScheme.primary)
      .padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(imageVector = icon, contentDescription = title, tint = Color.White)
    Spacer(modifier = Modifier.width(20.dp))
    Text(text = title, color = Color.White)
  }
}