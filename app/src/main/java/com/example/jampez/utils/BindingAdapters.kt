package com.example.jampez.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.jampez.data.entities.ToDo
import com.example.jampez.features.todo.TodoAdapter
import com.example.jampez.utils.constants.userImage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream

class BindingAdapters {

    object GlideInstance : KoinComponent {
        val glide: RequestManager by inject()
    }

    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView?, imageUrl: String) {
            view?.let {
                val file = File(view.context.filesDir, userImage)

                val glide = GlideInstance.glide
                if (file.exists()) {
                    glide.load(file).into(view)
                } else {
                    val resource = glide.load(imageUrl)
                    resource.into(view)
                    resource.into(object : CustomTarget<Drawable?>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable?>?
                            ) {
                                val bitmap = (resource as BitmapDrawable).bitmap

                                try {
                                    val out = FileOutputStream(file)
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                    out.flush()
                                    out.close()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }
            }
        }

        @BindingAdapter("setAdapter")
        @JvmStatic
        fun setAdapter(recyclerView: RecyclerView, adapter: DataBoundListAdapter<Any, ViewDataBinding>?) {
            adapter?.let {
                recyclerView.adapter = it
            }
        }

        @Suppress("UNCHECKED_CAST")
        @BindingAdapter("submitList")
        @JvmStatic
        fun submitList(recyclerView: RecyclerView, list: List<Any>?) {
            val adapter = recyclerView.adapter as DataBoundListAdapter<Any, ViewDataBinding>?

            adapter?.submitList(list ?: emptyList())
        }

    }
}