package com.github.xandeer.android.fab

enum class FabMenuState {
  COLLAPSED, EXPANDED;

  val isExpanded get() = this == EXPANDED
}