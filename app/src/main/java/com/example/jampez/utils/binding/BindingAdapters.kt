package com.example.jampez.utils.binding

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.jampez.utils.constants.userImage
import com.example.jampez.utils.extensions.decryptFile
import com.example.jampez.utils.extensions.deleteTmpFiles
import com.example.jampez.utils.extensions.encryptFile
import okhttp3.internal.format
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class BindingAdapters {

    object Koin : KoinComponent {
        val glide: RequestManager by inject()
    }

    companion object {
        @BindingAdapter("imageUrl", "userID")
        @JvmStatic
        fun loadImage(view: ImageView?, imageUrl: String, userId: Long) {
            view?.let {
                val fileName = format(userImage, userId)
                val file = File(view.context.cacheDir, fileName)
                val glide = Koin.glide
                if (file.exists()) {
                    glide.load(file.decryptFile(view.context, fileName)).into(view)
                } else {
                    val resource = Koin.glide.load(imageUrl)
                    resource.into(view)
                    resource.into(object : CustomTarget<Drawable?>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {

                            try {
                                file.encryptFile(view.context, (resource as BitmapDrawable).bitmap)
                            } catch (e: Exception) {
                                if (file.exists()) {
                                    file.delete()
                                }
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