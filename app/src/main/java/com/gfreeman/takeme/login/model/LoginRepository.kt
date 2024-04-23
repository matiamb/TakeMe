package com.gfreeman.takeme.login.model

import contract.ILoginContract

class LoginRepository: ILoginContract.ILoginModel {
    override fun loginWithUserAndPass(user: String, password: String): Boolean {
        return false
    }

    override fun loginWithProvider(provider: String) {
        TODO("Not yet implemented")
    }
}