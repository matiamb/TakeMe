package contract

interface LoginContract {
    interface LoginView: BaseContract.IBaseView{
        fun openMapsScreen()
    }

    interface ILoginPresenter<T: BaseContract.IBaseView>: BaseContract.IBasePresenter<T> {
        fun loginWithUserAndPass(user: String, password: String)
        fun loginWithProvider(provider: String)
        fun logOut()
        fun sendPasswordResetEmail(email: String)
    }
    interface ILoginModel{
        suspend fun loginWithUserAndPass(user: String, password: String): Boolean
        fun loginWithProvider(provider: String)
        fun logOut()
        suspend fun sendPasswordResetEmail(email: String): Boolean
    }
}