package com.racerush.app.ui.notifications

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.racerush.app.R
import com.racerush.app.data.model.AppNotification
import com.racerush.app.data.model.NotificationType
import com.racerush.app.data.model.colorRes
import com.racerush.app.databinding.ItemNotificationBinding

/**
 * Tarjetas de notificación. Como en el mockup: resultados de carrera en tarjeta blanca
 * con círculo rojo; actualizaciones del campeonato en tarjeta oscura con círculo amarillo.
 */
class NotificationsAdapter(
    private val items: List<AppNotification>
) : RecyclerView.Adapter<NotificationsAdapter.VH>() {

    class VH(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        val ctx = holder.itemView.context
        val dark = n.type == NotificationType.CHAMPIONSHIP
        with(holder.binding) {
            tvTitle.text = n.title
            tvMessage.text = n.message
            tvTime.text = n.timeLabel
            dot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, n.type.colorRes))
            notificationCard.setBackgroundResource(if (dark) R.drawable.bg_card_dark else R.drawable.bg_card_white)
            tvTitle.setTextColor(ContextCompat.getColor(ctx, if (dark) R.color.rr_white else R.color.rr_dark))
            tvMessage.setTextColor(ContextCompat.getColor(ctx, if (dark) R.color.rr_gray_bar else R.color.rr_gray_text))
        }
    }
}
