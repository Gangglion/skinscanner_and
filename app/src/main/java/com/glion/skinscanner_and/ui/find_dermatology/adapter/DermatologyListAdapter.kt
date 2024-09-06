package com.glion.skinscanner_and.ui.find_dermatology.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.ui.find_dermatology.data.DermatologyData

class DermatologyListAdapter(
    private val mContext: Context,
    private val itemList: List<DermatologyData>
) : RecyclerView.Adapter<DermatologyListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dermotology_list, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}