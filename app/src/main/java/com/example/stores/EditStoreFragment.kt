package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking


class EditStoreFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentEditStoreBinding
    private lateinit var mainActivity: MainActivity
    private var isEditMode: Boolean = false
    private lateinit var storeEntity: StoreEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity

        val id = arguments?.getLong(getString(R.string.arg_id), 0)

        mainActivity.hideFab(false)

        if (id != null && id != 0L) {
            isEditMode = true
            runBlocking {
                storeEntity = StoreApplication.database.storeDao().getStoreById(id)
            }
            setUIStore(storeEntity)
        } else {
            isEditMode = false
            storeEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()
        setupTextFields()
    }

    private fun setupActionBar() {
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.title =
            if (isEditMode) getString(R.string.edit_store_title_edit) else getString(R.string.edit_store_title_add)
        mainActivity.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadImage(url: String) {
        Glide.with(mainActivity)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgPhoto)
    }

    private fun setupTextFields() {
        with(binding) {
            etPhotoUrl.addTextChangedListener {
                validateTextFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
            etName.addTextChangedListener { validateTextFields(tilName) }
            etPhone.addTextChangedListener { validateTextFields(tilPhone) }
        }
    }

    private fun setUIStore(storeEntity: StoreEntity) {
        with(binding) {
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
            etWebsite.text = storeEntity.website.editable()
            loadImage(storeEntity.photoUrl)
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onDestroy() {
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mainActivity.supportActionBar?.title = getString(R.string.app_name)
        mainActivity.hideFab(true)
        super.onDestroy()
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_save, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                mainActivity.onBackPressed()
                true
            }
            R.id.action_save -> {

                with(binding) {

                    if (!validateTextFields(tilPhotoUrl, tilPhone, tilName)) {
                        return false
                    }

                    storeEntity.apply {
                        name = etName.toText()
                        phone = etPhone.toText()
                        website = etWebsite.toText()
                        photoUrl = etPhotoUrl.toText()
                    }
                }

                hideKeyboard()

                if (isEditMode) {
                    runBlocking {
                        StoreApplication.database.storeDao().updateStore(storeEntity)
                        mainActivity.updateStore(storeEntity)
                    }
                    Snackbar.make(
                        binding.root,
                        R.string.edit_store_message_update_success,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    runBlocking {
                        storeEntity.id = StoreApplication.database.storeDao().addStore(storeEntity)
                    }
                    mainActivity.addStore(storeEntity)
                    Toast.makeText(
                        mainActivity,
                        R.string.edit_store_message_save_success,
                        Toast.LENGTH_SHORT
                    ).show()

                }
                mainActivity.onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun TextInputEditText.toText(): String = this.text.toString().trim()

    private fun validateTextFields(vararg textFields: TextInputLayout): Boolean {
        var success = true
        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.editText?.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                success = false
            } else {
                textField.error = null
            }
        }
        if (!success) Snackbar.make(
            binding.root,
            R.string.edit_store_message_valid,
            Snackbar.LENGTH_SHORT
        ).show()
        return success
    }

}