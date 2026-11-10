package com.racerush.app.ui.championship

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.racerush.app.R
import com.racerush.app.data.model.Standing
import com.racerush.app.databinding.ItemStandingBinding

/** Filas de la tabla; la fila del jugador se resalta en amarillo. */
class StandingsAdapter : RecyclerView.Adapter<StandingsAdapter.VH>() {

    private var items: List<Standing> = emptyList()

    fun submit(list: List<Standing>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(val binding: ItemStandingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStandingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvPosition.text = s.position.toString()
            tvDriver.text = s.driver
            tvPoints.text = ctx.getString(R.string.points_value, s.points)
            standingCard.setBackgroundResource(if (s.isPlayer) R.drawable.bg_card_player else R.drawable.bg_card)
            tvPoints.setTextColor(ContextCompat.getColor(ctx, if (s.isPlayer) R.color.rr_dark else R.color.rr_gray_text))
        }
    }
}
