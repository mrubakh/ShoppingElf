package hu.ait.itemrecylerviewdemo


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.DialogFragment
import hu.ait.itemrecylerviewdemo.data.Item
import hu.ait.todorecylerviewdemo.R
import kotlinx.android.synthetic.main.new_item_dialog.*

class ItemDialog : DialogFragment() {

    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.ItemHandlerInterfaceExpception)
            )
        }
    }

    private lateinit var spItemCategory: Spinner
    private lateinit var etItemName: EditText
    private lateinit var etItemDesc: EditText
    private lateinit var etItemPrice: EditText
    private lateinit var cbBought: CheckBox
    var isEditMode = false


    fun editItem(builder: AlertDialog.Builder ){
        isEditMode = ((arguments != null) && arguments!!.containsKey(
            ScrollingActivity.KEY_ITEM
        ))

        if (isEditMode) {
            builder.setTitle(getString(R.string.edit_shopping))
            builder.setPositiveButton(getString(R.string.finish)) { dialog, witch -> }

            var item: Item = (arguments?.getSerializable(ScrollingActivity.KEY_ITEM) as Item)
            etItemName.setText(item.name)
            etItemDesc.setText(item.description)
            etItemPrice.setText(item.price)
            cbBought.isChecked = item.bought
            val res = context!!.resources.getStringArray(R.array.category_array)

            spItemCategory.setSelection(res.indexOf(item.category))


        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_shopping_item))
        builder.setPositiveButton(getString(R.string.add_item)) { dialog, witch -> }

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null)

        spItemCategory = rootView.findViewById(R.id.spCategory)
        etItemName = rootView.findViewById(R.id.etName)
        etItemDesc = rootView.findViewById(R.id.etDesc)
        etItemPrice = rootView.findViewById(R.id.etPrice)
        cbBought = rootView.findViewById(R.id.cbBought)

        builder.setView(rootView)
        editItem(builder)

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etItemPrice.text!!.isNotEmpty()) {
                if (etItemName.text!!.isNotEmpty()) {
                    if (etItemName.text!!.length <= 10) {
                        if (isEditMode) {
                            handleItemEdit()
                        } else {
                            handleItemCreate()
                        }
                        dialog.dismiss()

                    } else { etItemName.error = getString(R.string.name_limit) }

                } else { etItemName.error = getString(R.string.empty_field) }

            } else{ etItemPrice.error = getString(R.string.price_error) }
        }
    }

    private fun handleItemCreate() {

        itemHandler.itemCreated(
            Item(
                null,
                spItemCategory.selectedItem.toString(),
                etItemName.text.toString(),
                etItemDesc.text.toString(),
                etItemPrice.text.toString(),
                cbBought.isChecked
            )
        )
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM
        ) as Item
        itemToEdit.category = spItemCategory.selectedItem.toString()
        itemToEdit.name = etItemName.text.toString()
        itemToEdit.description = etItemDesc.text.toString()
        itemToEdit.price = etItemPrice.text.toString()
        itemToEdit.bought = cbBought.isChecked

        itemHandler.itemUpdated(itemToEdit)
    }
}