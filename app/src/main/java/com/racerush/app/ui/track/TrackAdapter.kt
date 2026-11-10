package com.racerush.app.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.racerush.app.R
import com.racerush.app.data.model.Track
import com.racerush.app.databinding.ItemTrackBinding

/** Lista de pistas; la seleccionada se pinta en rojo como en el mockup. */
class TrackAdapter(
    private val tracks: List<Track>,
    private val onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var selectedId: Int = -1

    fun setSelected(id: Int) {
        val old = tracks.indexOfFirst { it.id == selectedId }
        selectedId = id
        val new = tracks.indexOfFirst { it.id == id }
        if (old >= 0) notifyItemChanged(old)
        if (new >= 0) notifyItemChanged(new)
    }

    class TrackViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(
        ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        val ctx = holder.itemView.context
        val selected = track.id == selectedId
        with(holder.binding) {
            tvTrackName.text = track.name
            tvTrackInfo.text = ctx.getString(R.string.track_info, track.laps, track.bestTime)
            trackCard.setBackgroundResource(if (selected) R.drawable.bg_card_selected else R.drawable.bg_card)
            val titleColor = if (selected) R.color.rr_white else R.color.rr_dark
            val infoColor = if (selected) R.color.rr_white else R.color.rr_gray_text
            tvTrackName.setTextColor(ContextCompat.getColor(ctx, titleColor))
            tvTrackInfo.setTextColor(ContextCompat.getColor(ctx, infoColor))
            trackCard.setOnClickListener { onClick(track) }
        }
    }
}
