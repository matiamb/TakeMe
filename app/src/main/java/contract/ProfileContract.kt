package contract

import android.content.Context

interface ProfileContract {
    interface ProfileView<T: BaseContract.IBaseView>: FragmentBaseContract.IFragmentBaseView<T>{
        fun logOut()
        fun openLoginScreen()
    }
    interface ProfilePresenter<T: FragmentBaseContract.IFragmentBaseView<*>>: FragmentBaseContract.IFragmentBasePresenter<T>{
        fun logOut(context: Context)
        fun getUserData(): List<String?>
    }
    interface  ProfileModel{
        fun logOut()
        fun getUserData(): List<String?>
    }
}