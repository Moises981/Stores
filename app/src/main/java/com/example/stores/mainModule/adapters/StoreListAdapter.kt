package com.example.stores.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding

class StoreListAdapter(
    private val listener: IOnClickListener
) :
    ListAdapter<StoreEntity, RecyclerView.ViewHolder>(StoreDiffCallback()) {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)
        fun setListener(storeEntity: StoreEntity) {
            with(binding.root) {
                setOnClickListener {
                    listener.onClick(storeEntity)
                }
                setOnLongClickListener {
                    listener.onDelete(storeEntity)
                    true
                }
            }
            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store = getItem(position)
        with(holder as ViewHolder) {
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            Glide.with(context).load(store.photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().into(binding.imgPhoto)
        }
    }

    class StoreDiffCallback : DiffUtil.ItemCallback<StoreEntity>() {
        override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem == newItem
        }
    }
}