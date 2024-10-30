package contract

interface SignupContract {
    interface SignupView: BaseContract.IBaseView{
    }
    interface SignupPresenter<T: BaseContract.IBaseView>: BaseContract.IBasePresenter<T>{
        fun signUpUser(email: String, password: String)
    }
    interface SignupModel {
        suspend fun signUpUser(email: String, password: String): Boolean
    }
}