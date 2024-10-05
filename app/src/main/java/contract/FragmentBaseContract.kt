package contract

interface FragmentBaseContract {
    interface IFragmentBaseView<T : BaseContract.IBaseView>{
        fun getParentView(): T?
    }
    interface IFragmentBasePresenter<T: IFragmentBaseView<*>>{
        fun attachView(view: T)
    }
}