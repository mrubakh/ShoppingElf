package hu.ait.itemrecylerviewdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.itemrecylerviewdemo.adapter.ItemAdapter
import hu.ait.itemrecylerviewdemo.data.AppDatabase
import hu.ait.itemrecylerviewdemo.data.Item
import hu.ait.itemrecylerviewdemo.touch.ItemReyclerTouchCallback
import hu.ait.todorecylerviewdemo.R
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler {

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
    }

    lateinit var itemAdapter: ItemAdapter
    var isNight = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)

        initRecyclerView()

        fab.setOnClickListener {
            showAddItemDialog()
        }


        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(getString(R.string.new_item_tutorial))
                .setSecondaryText(getString(R.string.create_item_tutorial))
                .show()
            saveWasStarted()

            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText(getString(R.string.new_item_tutorial))
                .setSecondaryText(getString(R.string.create_item_tutorial))
                .show()
            saveWasStarted()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.action_deleteAll -> {
                itemAdapter.deleteAllItems()
            }
            R.id.action_nightmode -> {
               toggleNight()
            }
            R.id.action_showTotalExpenses -> {
                Toast.makeText(this, getString(R.string.total_expenses).format(itemAdapter.calculateExpenses()), Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun toggleNight(){
        if (isNight) {
            isNight = false
            main_layout.setBackgroundColor(Color.WHITE)
        } else{
            isNight = true
            main_layout.setBackgroundColor(Color.GRAY)
        }
    }

    fun saveWasStarted() {
        var sharedPref =
            PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    fun wasStartedBefore() : Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        return sharedPref.getBoolean(KEY_STARTED, false)
    }



    private fun initRecyclerView() {
        Thread {
            var items = AppDatabase.getInstance(this@ScrollingActivity).ItemDao().getAllItems()

            runOnUiThread {
                itemAdapter = ItemAdapter(this, items)
                recyclerItem.adapter = itemAdapter

                var itemDecorator = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerItem.addItemDecoration(itemDecorator)

                val callback = ItemReyclerTouchCallback(itemAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerItem)
            }
        }.start()

    }

    fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager,
            TAG_ITEM_DIALOG
        )
    }


    var editIndex: Int = -1

    fun showEditItemDialog(itemToEdit: Item, idx: Int) {
        editIndex = idx
        val editDialog = ItemDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager,
            TAG_ITEM_EDIT
        )
    }


    fun saveItem(item: Item) {
        Thread {
            var newId =
                AppDatabase.getInstance(this@ScrollingActivity).ItemDao().addItem(item)

            item.itemId = newId
            runOnUiThread {
                itemAdapter.addItem(item)
            }

        }.start()
    }

    override fun itemCreated(item: Item) {
        saveItem(item)
    }

    override fun itemUpdated(item: Item) {
        Thread {
            AppDatabase.getInstance(
                this@ScrollingActivity).
                ItemDao().updateItem(item)

            runOnUiThread {
                itemAdapter.updateItemOnPosition(item, editIndex)
            }
        }.start()
    }
}
