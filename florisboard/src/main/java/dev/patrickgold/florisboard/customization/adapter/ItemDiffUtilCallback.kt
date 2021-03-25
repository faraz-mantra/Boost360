package dev.patrickgold.florisboard.customization.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil


class ItemDiffUtilCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    // Suppressing below warning as the model class is being created using data keyword which implements equals().
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}