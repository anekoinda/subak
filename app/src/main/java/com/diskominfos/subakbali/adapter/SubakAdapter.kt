package com.diskominfos.subakbali.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.api.DataSubak
import com.diskominfos.subakbali.databinding.ItemSubakBinding

class SubakAdapter(private val subakList: MutableList<DataSubak>) :
    RecyclerView.Adapter<SubakAdapter.SubakViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class SubakViewHolder(private val binding: ItemSubakBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subak: DataSubak) {
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(subak)
            }

            binding.apply {
//                Glide.with(itemView).load(subak.photoUrl)
//                    .transition(DrawableTransitionOptions.withCrossFade()).centerCrop()
//                    .into(photo)
                nama.text = subak.nama
                jenis.text = subak.jenis_subak

                itemView.setOnClickListener {
//                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
//                    intent.putExtra("DataSubak", subak)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(nama, "nama"),
                            Pair(jenis, "jenis_subak"),
                        )
//                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubakViewHolder {
        val view = ItemSubakBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubakViewHolder((view))
    }

    override fun onBindViewHolder(holder: SubakViewHolder, position: Int) {
        holder.bind(subakList[position])
    }

    private val limit = 2
    override fun getItemCount(): Int {
        return if (subakList.size > limit) {
            limit;
        } else {
            subakList.size;
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataSubak)
    }

    fun setOnClickCallBack(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}

