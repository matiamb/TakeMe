package contract

import com.gfreeman.takeme.home.model.map.Place

interface HomeContract {
    interface HomeView : BaseContract.IBaseView {
        fun openMapsScreenWithDestination(startPlace: Place, destinationPlace: Place)
    }
}