package com.example.jampez.utils.extensions

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

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

fun File.encryptFile(file: File, context: Context) = EncryptedFile.Builder(
    file,
    context,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
).build().openFileOutput()

fun File.decryptFile(file: File, context: Context) : File {
    val inputStream = EncryptedFile.Builder(
        file,
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build().openFileInput()
    val bytes = inputStream.readBytes()
    file.writeBytes(bytes)
    return file
}