package com.gfreeman.takeme.home.presenter.profile

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.gfreeman.takeme.home.model.ProfileModel
import com.gfreeman.takeme.login.presenter.LoginPresenter.Companion.FAKE_LOGIN_PREFERENCES_KEY
import contract.BaseContract
import contract.ProfileContract

class ProfilePresenter: ProfileContract.ProfilePresenter<ProfileContract.ProfileView<BaseContract.IBaseView>> {
    private lateinit var profileView: ProfileContract.ProfileView<*>
    private lateinit var profileModel: ProfileModel
    override fun attachView(view: ProfileContract.ProfileView<BaseContract.IBaseView>) {
        profileModel = ProfileModel()
        profileView = view
    }

    override fun logOut(context: Context) {

        profileModel.logOut()
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(FAKE_LOGIN_PREFERENCES_KEY, false)
        editor.apply()
    }

    override fun getUserData(): String? {
        return profileModel.getUserData()
    }

}