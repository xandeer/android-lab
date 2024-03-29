package com.github.xandeer.androidlab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.github.xandeer.androidlab.Destination

@Composable
fun LabTabRow(
  allScreens: List<Destination>,
  onTabSelected: (Destination) -> Unit,
  currentScreen: Destination
) {
  Surface(
    Modifier
      .height(TabHeight)
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
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
      text = text, style = MaterialTheme.typography.labelSmall, color = tabTintColor,
      maxLines = 1
    )
  }
}

private val TabHeight = 56.dp
