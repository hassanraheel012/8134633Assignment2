package com.s8134633.coursenest.ui.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.s8134633.coursenest.R
import com.s8134633.coursenest.model.DetailRow
import com.s8134633.coursenest.ui.common.CourseIcons

/** Shows the course's fields on the Details screen, each with a matching icon. */
class DetailRowAdapter : RecyclerView.Adapter<DetailRowAdapter.DetailRowViewHolder>() {

    private val rows = mutableListOf<DetailRow>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newRows: List<DetailRow>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailRowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_row, parent, false)
        return DetailRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailRowViewHolder, position: Int) {
        holder.bind(rows[position], isLast = position == rows.lastIndex)
    }

    override fun getItemCount(): Int = rows.size

    class DetailRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageIcon: ImageView = itemView.findViewById(R.id.imageRowIcon)
        private val textLabel: TextView = itemView.findViewById(R.id.textRowLabel)
        private val textValue: TextView = itemView.findViewById(R.id.textRowValue)
        private val divider: View = itemView.findViewById(R.id.dividerRow)

        fun bind(row: DetailRow, isLast: Boolean) {
            imageIcon.setImageResource(CourseIcons.iconFor(row.key))
            textLabel.text = row.label
            textValue.text = row.value
            divider.isVisible = !isLast
        }
    }
}
