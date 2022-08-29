package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditViewModel
import com.example.stores.mainModule.adapters.IOnClickListener
import com.example.stores.mainModule.adapters.StoreAdapter
import com.example.stores.mainModule.viewModels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), IOnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private val mainViewModel: MainViewModel by viewModels()
    private val editViewModel: EditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            fab.setOnClickListener {
                launchEditFragment()
            }
        }
        setupViewModel()
        setupRecyclerView()
        getStores()
    }

    private fun setupViewModel() {
        mainViewModel.stores.observe(this) {
            storeAdapter.setStores(it)
        }
        editViewModel.fabStatus.observe(this) {
            if (it) binding.fab.show()
            else binding.fab.hide()
        }
        editViewModel.currentStore.observe(this){
            storeAdapter.save(it)
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        editViewModel.setFabStatus(false)
        editViewModel.setCurrentStore(storeEntity)
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {
        storeAdapter = StoreAdapter(mutableListOf(), this)
        gridLayoutManager = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = storeAdapter
        }
    }

    private fun getStores() {

    }


    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        mainViewModel.updateStore(storeEntity)
    }

    override fun onDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_options_title)
            .setItems(resources.getStringArray(R.array.array_options_item)) { _, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> gotoWebsite(storeEntity.website)
                }
            }.show()
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

    private fun confirmDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null).show()
    }

    private fun gotoWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

}