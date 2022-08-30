package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.TypeError
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditViewModel
import com.example.stores.mainModule.adapters.IOnClickListener
import com.example.stores.mainModule.adapters.StoreListAdapter
import com.example.stores.mainModule.viewModels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), IOnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storeAdapter: StoreListAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private val mainViewModel: MainViewModel by viewModels()
    private val editViewModel: EditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        mainViewModel.stores.observe(this) {
            mainViewModel.setProgressBarStatus(false)
            storeAdapter.submitList(it)
        }

        mainViewModel.progressBarStatus.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        mainViewModel.typeError.observe(this) { typeError ->
            val msgRes = when (typeError) {
                TypeError.GET -> "Error at requesting data"
                TypeError.INSERT -> "Error at inserting data"
                TypeError.UPDATE -> "Error at updating data"
                TypeError.DELETE -> "Error at removing data"
                else -> "Unknown error"
            }
            Snackbar.make(binding.root, msgRes, Snackbar.LENGTH_SHORT).show()
        }

        editViewModel.fabStatus.observe(this) {
            if (it) binding.fab.show()
            else binding.fab.hide()
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        editViewModel.setFabStatus(false)
        editViewModel.setCurrentStoreById(storeEntity.id)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, EditStoreFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {
        storeAdapter = StoreListAdapter(this)
        gridLayoutManager = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = storeAdapter
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        editViewModel.setFabStatus(true)
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