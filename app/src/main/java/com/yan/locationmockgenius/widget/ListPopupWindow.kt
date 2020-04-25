package com.yan.locationmockgenius.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.TextView
import com.yan.locationmockgenius.R
import kotlinx.android.synthetic.main.item_popup_window_list.view.*
import kotlinx.android.synthetic.main.popup_window_list.view.*

class ListPopupWindow<T>(context: Context, var keyPickProxy: KeyPickProxy<T>, val listener: OnPickListener<T>) : PopupWindow(context) {
    private var rootView: View
    private val list = ArrayList<T>()
    private var adapter: ListAdapter<T>

    init {
        width = MATCH_PARENT
        height = context.resources.displayMetrics.heightPixels / 3
        setBackgroundDrawable(null)

        rootView = LayoutInflater.from(context).inflate(R.layout.popup_window_list, null)

        val rvList = rootView.rv_list
        rvList.layoutManager = LinearLayoutManager(context)
        adapter = ListAdapter<T>()
        rvList.adapter = adapter
        contentView = rootView
    }

    fun showAsDropDown(list: List<T>, anchor: View?) {
        super.showAsDropDown(anchor)
        refresh(list)
    }

    private fun refresh(list: List<T>) {
        this.list.clear()
        if (list.isNotEmpty()) {
            this.list.addAll(list)
        }
        adapter.notifyDataSetChanged()
    }

    inner class ListAdapter<T>() : RecyclerView.Adapter<ListHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListHolder {
            return ListHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_popup_window_list, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(p0: ListHolder, p1: Int) {
            val t = list[p0.adapterPosition]

            p0.tv.text = keyPickProxy.pick(t)

            p0.itemView.setOnClickListener {
                listener.onPick(t)
                dismiss()
            }
        }
    }

    class ListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.tv
    }

    interface KeyPickProxy<T> {
        fun pick(t: T): String
    }

    interface OnPickListener<T> {
        fun onPick(t: T)
    }
}