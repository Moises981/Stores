package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding

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
                val store = Store(name = etName.text.toString().trim())
                storeAdapter.add(store)
            }
        }

        setupRecyclerView()
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


    override fun onClick(store: Store) {

    }
}