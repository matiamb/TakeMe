package com.gfreeman.takeme.home.presenter.profile

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.gfreeman.takeme.login.presenter.LoginPresenter.Companion.FAKE_LOGIN_PREFERENCES_KEY
import contract.BaseContract
import contract.ProfileContract

class ProfilePresenter: ProfileContract.ProfilePresenter<ProfileContract.ProfileView<BaseContract.IBaseView>> {
    private lateinit var profileView: ProfileContract.ProfileView<*>
    override fun attachView(view: ProfileContract.ProfileView<BaseContract.IBaseView>) {
        profileView = view
    }

    override fun logOut(context: Context) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(FAKE_LOGIN_PREFERENCES_KEY, false)
        editor.apply()
    }
}