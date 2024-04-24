package contract

interface LoginContract {
    interface LoginView: BaseContract.IBaseView{
        fun openMapsScreen()
    }

    interface ILoginPresenter<T: BaseContract.IBaseView>: BaseContract.IBasePresenter<T> {
        fun loginWithUserAndPass(user: String, password: String)
        fun loginWithProvider(provider: String)
    }
    interface ILoginModel{
        fun loginWithUserAndPass(user: String, password: String):Boolean
        fun loginWithProvider(provider: String)
    }
}