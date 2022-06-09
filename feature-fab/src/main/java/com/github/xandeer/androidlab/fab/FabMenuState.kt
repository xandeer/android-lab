package com.github.xandeer.androidlab.fab

enum class FabMenuState {
  COLLAPSED, EXPANDED;

  val isExpanded get() = this == EXPANDED
}