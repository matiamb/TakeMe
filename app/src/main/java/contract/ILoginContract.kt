package contract

interface ILoginContract {
    interface LoginView{
        fun showErrorMessage(message: String)
        fun openMapsScreen()
    }

    interface ILoginPresenter{
        fun loginWithUserAndPass(user: String, password: String)
        fun loginWithProvider(provider: String)
        fun attachView(loginView: LoginView)
    }
    interface ILoginModel{
        fun loginWithUserAndPass(user: String, password: String):Boolean
        fun loginWithProvider(provider: String)
    }
}