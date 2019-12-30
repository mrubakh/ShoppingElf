package hu.ait.itemrecylerviewdemo.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.ait.itemrecylerviewdemo.ScrollingActivity
import hu.ait.itemrecylerviewdemo.data.AppDatabase
import hu.ait.itemrecylerviewdemo.data.Item
import hu.ait.itemrecylerviewdemo.touch.ItemTouchHelperCallback
import hu.ait.todorecylerviewdemo.R
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>, ItemTouchHelperCallback {

    var itemList = mutableListOf<Item>()
    var total_expenses = 0

    val context: Context
    constructor(context: Context, items: List<Item>){
        this.context = context

        itemList.addAll(items)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRow = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )
        return ViewHolder(itemRow)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun placeImage(item: Item, holder: ViewHolder){
        val res = context.resources.getStringArray(R.array.category_array)
        when{
            item.category == res[0] -> holder.imCategory.setImageResource(R.drawable.food_icon)
            item.category == res[1] -> holder.imCategory.setImageResource(R.drawable.electronics_icon)
            item.category == res[2] -> holder.imCategory.setImageResource(R.drawable.books)
            item.category == res[3] -> holder.imCategory.setImageResource(R.drawable.clothing)
            item.category == res[4] -> holder.imCategory.setImageResource(R.drawable.office)
            item.category == res[5] -> holder.imCategory.setImageResource(R.drawable.toys)
            item.category == res[6] -> holder.imCategory.setImageResource(R.drawable.sports)
        }

    }

    fun setListeners(item: Item, holder: ViewHolder){
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.cbItem.setOnClickListener {
            item.bought = holder.cbItem.isChecked
            updateItem(item)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(
                item, holder.adapterPosition
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = itemList.get(holder.adapterPosition)

        holder.tvName.text = item.name
        holder.tvDesc.text = item.description
        if (item.price.isNotBlank()){
            holder.tvPrice.text = "$${item.price}"
        }
        holder.cbItem.isChecked = item.bought

        placeImage(item, holder)
        setListeners(item, holder)
    }

    fun updateItem(item: Item) {
        Thread {
            AppDatabase.getInstance(context).ItemDao().updateItem(item)
        }.start()
    }

    fun updateItemOnPosition(item: Item, index: Int) {
        itemList.set(index, item)
        notifyItemChanged(index)
    }

    fun calculateExpenses(): Int {
        for (i in itemList){
            if (i.bought){
                total_expenses += i.price.toInt()
            }
        }
        return total_expenses
    }

    fun deleteItem(index: Int){
        Thread{
            AppDatabase.getInstance(context).ItemDao().deleteItem(itemList[index])

            (context as ScrollingActivity).runOnUiThread {
                itemList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }

    fun deleteAllItems() {
        Thread {
            AppDatabase.getInstance(context).ItemDao().deleteAllItems()

            (context as ScrollingActivity).runOnUiThread {
                itemList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    fun addItem(item: Item) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex)
    }


    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.tvName
        val cbItem = itemView.cbItem
        val imCategory = itemView.imCategory
        val tvDesc = itemView.tvDesc
        val tvPrice = itemView.tvPrice
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
    }

}