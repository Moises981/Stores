package com.example.stores.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(
    private var storeEntities: MutableList<StoreEntity>,
    private val listener: IOnClickListener
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = storeEntities[position]

        with(holder) {
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            Glide.with(context).load(store.photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = storeEntities.size

    fun setStores(stores: List<StoreEntity>) {
        this.storeEntities = stores as MutableList<StoreEntity>
        notifyDataSetChanged()
    }

    fun save(storeEntity: StoreEntity) {
        if (storeEntity.id == 0L) return
        if (!storeEntities.contains(storeEntity)) {
            storeEntities.add(storeEntity)
            notifyItemInserted(itemCount - 1)
        } else {
            update(storeEntity)
        }
    }

    private fun update(storeEntity: StoreEntity) {
        val index = storeEntities.indexOf(storeEntity)
        if (index != -1) {
            storeEntities[index] = storeEntity
            notifyItemChanged(index)
        }
    }
}