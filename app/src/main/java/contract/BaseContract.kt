package contract

import android.content.Context

interface BaseContract {
    interface IBaseView {
        fun showErrorMessage(message: String)
        fun getViewContext(): Context
    }
    interface IBasePresenter<T: IBaseView>{
        fun attachView(view: T)
    }
}