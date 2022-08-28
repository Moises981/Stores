package com.example.stores

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), IOnClickListener, MainAux {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            fab.setOnClickListener {
                launchEditFragment()
            }
        }
        setupRecyclerView()
        getStores()
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()

        if (args != null) {
            fragment.arguments = args
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        hideFab(true)
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
        runBlocking {
            val stores = StoreApplication.database.storeDao().getAllStores()
            storeAdapter.setStores(stores)
        }
    }


    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)
        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        runBlocking {
            StoreApplication.database.storeDao().updateStore(storeEntity)
        }
        updateStore(storeEntity)
    }

    override fun hideFab(isVisible: Boolean) {
        if (isVisible) binding.fab.show() else binding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        storeAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        storeAdapter.update(storeEntity)
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
                runBlocking {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                }
                storeAdapter.delete(storeEntity)
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