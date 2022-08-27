package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), IOnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnSave.setOnClickListener {
                val store = StoreEntity(name = etName.text.toString().trim())
                runBlocking {
                    StoreApplication.database.storeDao().addStore(store)
                }
                storeAdapter.add(store)
            }
        }

        setupRecyclerView()
        getStores()
    }

    private fun setupRecyclerView() {
        storeAdapter = StoreAdapter(mutableListOf(), this)
        gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = storeAdapter
        }
    }

    private fun getStores() {
        runBlocking {
            val stores = StoreApplication.database.storeDao().getAllStores()
            storeAdapter.setStores(stores)
        }
    }


    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        runBlocking {
            StoreApplication.database.storeDao().updateStore(storeEntity)
        }
        storeAdapter.update(storeEntity)
    }

    override fun onDelete(storeEntity: StoreEntity) {
        runBlocking {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
        }
        storeAdapter.delete(storeEntity)
    }
}