package hu.ait.itemrecylerviewdemo.touch

interface ItemTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}