package hu.ait.itemrecylerviewdemo.data

import androidx.room.*
import hu.ait.itemrecylerviewdemo.data.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getAllItems() : List<Item>

    @Insert
    fun addItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem (item: Item)

    @Query("DELETE FROM item")
    fun deleteAllItems()
}