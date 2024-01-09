package com.example.jampez.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.data.models.User
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.utils.constants.EMAIL
import com.example.jampez.utils.constants.FIRST_NAME
import com.example.jampez.utils.constants.ID
import com.example.jampez.utils.constants.IMAGE
import com.example.jampez.utils.constants.PASSPHRASE
import com.example.jampez.utils.constants.PASSWORD
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class UserRepository(private val dummyJsonApi: DummyJsonApi) : IUserRepository {

    private val prefs: SharedPreferences by inject(
        SharedPreferences::class.java
    )

    override fun authenticatedUserId(emailInput: String, passwordInput: String): Long? {
        val response = ApiResponse(dummyJsonApi.fetchUsers())

        return if(response.success) {
            val users = response.body?.users

            val user = users?.find { it.email == emailInput && it.password == passwordInput }

            if (saveDatabasePassPhrase() && saveUser(user)) {
                user?.id
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun saveDatabasePassPhrase() : Boolean {
        var passPhrasedSaved: Boolean
        prefs.run {
            passPhrasedSaved = contains(PASSPHRASE)
            if (!passPhrasedSaved) {
                edit {
                    putString(PASSPHRASE, UUID.randomUUID().toString())
                    passPhrasedSaved = true
                }
            }
        }
        return passPhrasedSaved
    }

    override fun saveUser(user: User?) : Boolean {
        var userSaved = false
        prefs.edit {
            if (user != null) {
                putLong(ID, user.id)
                putString(FIRST_NAME, user.firstName)
                putString(EMAIL, user.email)
                putString(PASSWORD, user.password)
                putString(IMAGE, user.image)
                userSaved = true
            }
        }
        return userSaved
    }

    override fun deleteUser() : Boolean {
        var userDeleted = false
        prefs.edit {
            remove(ID)
            remove(FIRST_NAME)
            remove(EMAIL)
            remove(PASSWORD)
            remove(IMAGE)
            userDeleted = true
        }
        return userDeleted
    }

    override fun getUser() : User? {
        var user: User? = null
        prefs.run {
            if (contains(ID) && contains(FIRST_NAME) && contains(EMAIL) && contains(PASSWORD) && contains(
                    IMAGE
                )) {
                user = User(
                    getLong(ID, 0),
                    getString(FIRST_NAME, "").toString(),
                    getString(EMAIL, "").toString(),
                    getString(PASSWORD, "").toString(),
                    getString(IMAGE, "").toString()
                )
            }
        }
        return user
    }
}