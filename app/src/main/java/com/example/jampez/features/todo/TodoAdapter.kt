package com.example.jampez.features.todo

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.jampez.data.entities.ToDo
import com.example.jampez.databinding.TodoItemBinding
import com.example.jampez.utils.binding.DataBoundListAdapter

class TodoAdapter : DataBoundListAdapter<ToDo, TodoItemBinding>(
    TodoItemResultCallback()
) {

    private var onItemClickListener: ((ToDo) -> Unit)? = null
    private var onItemCheckedChangedListener: ((Boolean, ToDo) -> Unit)? = null

    class TodoItemResultCallback : DiffUtil.ItemCallback<ToDo>() {
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo) :
                Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo) :
                Boolean = oldItem == newItem
    }

    fun setItemClickListener(onItemClickListener: ((ToDo) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemCheckedChangedListener(onItemCheckedChangedListener: ((Boolean, ToDo) -> Unit)?) {
        this.onItemCheckedChangedListener = onItemCheckedChangedListener
    }

    fun onTodoCheckedChanged(isChecked: Boolean, toDo: ToDo){
        onItemCheckedChangedListener?.invoke(isChecked, toDo)
    }

    fun onTodoClicked(toDo: ToDo) {
        onItemClickListener?.invoke(toDo)
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): TodoItemBinding {
        return TodoItemBinding.inflate(from(parent.context))
    }

    override fun bind(binding: TodoItemBinding, item: ToDo, position: Int) {
        binding.apply {
            todo = item
            adapter = this@TodoAdapter
        }
    }
}