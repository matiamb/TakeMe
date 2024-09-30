package contract

import com.gfreeman.takeme.home.model.congrats.ResultDBOperation
import com.gfreeman.takeme.home.model.map.Place

interface ArrivedToDestinationContract {
    interface  ArrivedToDestinationView: BaseContract.IBaseView{
        fun notifyFavoriteSaved()
    }
    interface IArrivedToDestinationPresenter<T : BaseContract.IBaseView> : BaseContract.IBasePresenter<T> {
        fun saveFavoriteRoute(startPlace: Place?, finishPlace: Place?)
        fun getCurrentDateFormatted(): String
    }

    interface ArrivedToDestinationModel{
        suspend fun saveFavoriteRoute(startPlace: Place, destinationPlace: Place, date: String) : ResultDBOperation
    }
}