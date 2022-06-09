package com.github.xandeer.androidlab.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun FabMenu(
  icon: ImageVector,
  items: List<FabMenuItem>,
  state: FabMenuState,
  expandLabel: String? = null,
  showLabels: Boolean = true,
  stateChanged: (fabState: FabMenuState) -> Unit,
  onFabClicked: () -> Unit,
  onMenuItemClicked: (item: FabMenuItem) -> Unit
) {
  val transition = updateTransition(targetState = state, label = "")
  val scale by transition.animateFloat(label = "") {
    if (it.isExpanded) 1f else 0f
  }
  val alpha by transition.animateFloat(
    transitionSpec = {
      tween(durationMillis = 50)
    }, label = ""
  ) {
    if (it.isExpanded) 1f else 0f
  }
  val rotation by transition.animateFloat(label = "") {
    if (it.isExpanded) 90f else 0f
  }

  fun dispatchStateChange() {
    stateChanged(
      if (transition.currentState.isExpanded) {
        FabMenuState.COLLAPSED
      } else FabMenuState.EXPANDED
    )
  }

  Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items.forEach { item ->
      if (transition.targetState.isExpanded || state.isExpanded) {
        FabMenuItem(item, alpha, scale, showLabels) {
          dispatchStateChange()
          onMenuItemClicked(it)
        }
      }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (transition.targetState.isExpanded || state.isExpanded) {
        expandLabel?.let {
          FabLabel(text = it, alpha = alpha)
          Spacer(modifier = Modifier.width(16.dp))
        }
      }
      FloatingActionButton(
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = if (state.isExpanded) Color.White
        else MaterialTheme.colorScheme.primary,
        onClick = {
          if (state == FabMenuState.EXPANDED) {
            onFabClicked()
          }
          dispatchStateChange()
        }) {
        Icon(
          imageVector = icon,
          contentDescription = "",
          modifier = Modifier.rotate(rotation),
          tint = if (state.isExpanded) MaterialTheme.colorScheme.primary
          else Color.White
        )
      }
    }
  }
}

@Composable
private fun FabLabel(text: String, alpha: Float) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(2.dp))
      .alpha(alpha)
      .background(Color.White.copy(alpha))
      .padding(6.dp, 4.dp),
  ) {
    Text(
      text = text, style = MaterialTheme.typography.labelSmall, color = Color.Black
    )
  }
}

@Composable
fun FabModal(
  state: FabMenuState,
  onClick: () -> Unit
) {
  AnimatedVisibility(
    visible = state.isExpanded,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(.2f))
        .clickable(
          enabled = state.isExpanded, indication = null,
          interactionSource = remember { MutableInteractionSource() },
          onClick = onClick
        )
    )
  }
}

private val FAB_ITEM_SIZE = 36.dp
@Composable
private fun FabMenuItem(
  item: FabMenuItem,
  alpha: Float,
  scale: Float,
  showLabel: Boolean,
  onFabItemClicked: (item: FabMenuItem) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(end = 12.dp)
      .clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) { onFabItemClicked(item) }
  ) {
    if (showLabel) {
      FabLabel(text = item.label, alpha = alpha)
      Spacer(modifier = Modifier.width(24.dp))
    }

    val fabColor = MaterialTheme.colorScheme.primary
    val painter = rememberVectorPainter(image = item.icon)
    Canvas(
      modifier = Modifier.requiredSize(FAB_ITEM_SIZE)
    ) {
      drawCircle(
        Color.Black.copy(.1f),
        center = Offset(this.center.x + 2f, this.center.y + 7f),
        radius = (FAB_ITEM_SIZE / 2 * scale).toPx()
      )
      drawCircle(color = fabColor, (FAB_ITEM_SIZE / 2 * scale).toPx())
      translate(
        left = center.x - painter.intrinsicSize.width / 2,
        top = center.y - painter.intrinsicSize.height / 2
      ) {
        with(painter) {
          draw(painter.intrinsicSize, alpha, ColorFilter.tint(Color.White))
        }
      }
    }
  }
}
