package com.example.jampez.utils

import android.graphics.Bitmap
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
                val file = File(view.context.filesDir, format(userImage, userId))
                val glide = Koin.glide
                if (file.exists()) {
                    val decryptedFile = file.decryptFile(file, view.context)

                    glide.load(decryptedFile).into(view)
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
                                    val out = file.encryptFile(file, view.context)
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