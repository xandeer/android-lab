package xandeer.android.lab.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xandeer.android.lab.R

class ActivityAdapter : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {
  var clickListener: ItemClickListener ?= null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.main_activity_item, parent, false)

    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return Activities.array.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(Activities.array[position])
  }

  interface ItemClickListener {
    fun onItemClick(className: String)
  }

  inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(model: Model) {
      val nameView = view.findViewById<TextView>(R.id.activityNameView)
      nameView.text = model.name
      view.setOnClickListener {
        clickListener?.onItemClick(model.clazz)
      }
    }
  }
}