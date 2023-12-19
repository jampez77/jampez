package com.example.jampez.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.os.FileUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.jampez.utils.constants.userImage
import com.scottyab.rootbeer.BuildConfig
import com.scottyab.rootbeer.RootBeer
import okhttp3.internal.format
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.jvm.Throws


fun String.isEquals(string: String) = this == string

fun String.isEmailValid(): Boolean {
    if (isEmpty()) {
        return false
    }
    val p = Pattern.compile(
        "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}\$", CASE_INSENSITIVE
    )
    val m = p.matcher(this)
    return m.matches()
}

fun AppCompatImageView.startLoadingAnimation() {
    val animationDrawable = drawable as AnimationDrawable
    animationDrawable.callback = this
    animationDrawable.setVisible(true, true)
    post {
        animationDrawable.start()
    }
}

fun AppCompatImageView.stopLoadingAnimation() {
    val animationDrawable = drawable as AnimationDrawable
    post {
        animationDrawable.stop()
    }
}

fun File.encryptFile(context: Context, bitmap: Bitmap) {

    val encryptedFile = EncryptedFile.Builder(
        this,
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()

    val out = encryptedFile.openFileOutput()

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    out.flush()
    out.close()
}

fun Long.deleteTmpFiles(context: Context){
    context.cacheDir?.let { cacheDir ->
        if (cacheDir.exists()) {

            cacheDir.listFiles()?.forEach {
                if (it.name.startsWith("_user_${this}") && it.extension == "tmp") {
                    it.delete()
                }
            }
        }
    }
}

fun File.decryptFile(context: Context, tmpName: String) : File {
    val inputStream = EncryptedFile.Builder(
        this,
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build().openFileInput()
    val bytes = inputStream.readBytes()
    val tmp = File.createTempFile("_${tmpName}", null, context.cacheDir)
    tmp.writeBytes(bytes)
    return tmp
}

fun Context.isRooted() = RootBeer(this).isRootedWithoutBusyBoxCheck && !BuildConfig.DEBUG