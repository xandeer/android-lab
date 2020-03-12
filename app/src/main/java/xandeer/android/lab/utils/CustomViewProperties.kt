@file:Suppress("unused")

package xandeer.android.lab.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.DeprecationLevel.ERROR

const val NO_GETTER: String = "Property does not have a getter"
fun noGetter(): Nothing = throw Exception("Property does not have a getter")

const val matchParent: Int = ViewGroup.LayoutParams.MATCH_PARENT
const val wrapContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT

var View.backgroundColorResource: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(colorId) = setBackgroundColor(ContextCompat.getColor(context, colorId))

var View.backgroundResource: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) = setBackgroundResource(v)

var View.leftPadding: Int
  inline get() = paddingLeft
  set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

var View.topPadding: Int
  inline get() = paddingTop
  set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

var View.rightPadding: Int
  inline get() = paddingRight
  set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

var View.bottomPadding: Int
  inline get() = paddingBottom
  set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)

var View.verticalPadding: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(value) = setPadding(paddingLeft, value, paddingRight, value)

var View.horizontalPadding: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(value) = setPadding(value, paddingTop, value, paddingBottom)

var View.padding: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  inline set(value) = setPadding(value, value, value, value)

var ViewGroup.MarginLayoutParams.verticalMargin: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) {
    topMargin = v
    bottomMargin = v
  }

var ViewGroup.MarginLayoutParams.horizontalMargin: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) {
    leftMargin = v; rightMargin = v
  }

var ViewGroup.MarginLayoutParams.margin: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) {
    leftMargin = v
    rightMargin = v
    topMargin = v
    bottomMargin = v
  }

var TextView.textColorResource: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(colorId) = setTextColor(ContextCompat.getColor(context, colorId))

var TextView.lines: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) = setLines(v)

var TextView.singleLine: Boolean
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) = setSingleLine(v)

var TextView.textResource: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) = setText(v)

var ImageView.image: Drawable?
  inline get() = drawable
  inline set(value) = setImageDrawable(value)

var ImageView.imageResource: Int
  @Deprecated(NO_GETTER, level = ERROR) get() = noGetter()
  set(v) = setImageResource(v)

@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEach(action)", "androidx.core.view.forEach"))
inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
  for (i in 0 until childCount) {
    action(getChildAt(i))
  }
}