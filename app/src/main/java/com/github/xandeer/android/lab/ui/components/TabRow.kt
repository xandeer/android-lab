package com.github.xandeer.android.lab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.github.xandeer.android.lab.LabScreen

@Composable
fun LabTabRow(
  allScreens: List<LabScreen>,
  onTabSelected: (LabScreen) -> Unit,
  currentScreen: LabScreen
) {
  Surface(
    Modifier
      .height(TabHeight)
      .fillMaxWidth()
      .alpha(.8f)
  ) {
    Column {
      Divider(
        color = MaterialTheme.colorScheme.onSurface.copy(.2f),
        thickness = 0.5.dp
      )
      Row(
        Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        allScreens.forEach { screen ->
          LabTab(
            text = screen.name,
            icon = screen.icon,
            onSelected = { onTabSelected(screen) },
            selected = currentScreen == screen
          )
        }
      }
    }
  }
}

@Composable
private fun RowScope.LabTab(
  text: String,
  icon: ImageVector,
  onSelected: () -> Unit,
  selected: Boolean
) {
  val tabTintColor by animateColorAsState(
    targetValue = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface.copy(.6f)
  )
  Column(
    modifier = Modifier
      .weight(1f)
      .selectable(
        selected = selected,
        onClick = onSelected,
        role = Role.Tab,
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      )
      .animateContentSize()
      .padding(4.dp)
      .height(TabHeight)
      .clearAndSetSemantics { contentDescription = text },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = icon, contentDescription = text, tint = tabTintColor,
    )
    Text(
      text = text, style = MaterialTheme.typography.labelSmall,
    )
  }
}

private val TabHeight = 56.dp
