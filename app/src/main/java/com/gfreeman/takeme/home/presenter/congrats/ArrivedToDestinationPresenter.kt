package com.gfreeman.takeme.home.presenter.congrats

import com.gfreeman.takeme.home.model.congrats.ArrivedToDestinationModel
import com.gfreeman.takeme.home.model.congrats.ResultDBOperation
import com.gfreeman.takeme.home.model.congrats.ResultError
import com.gfreeman.takeme.home.model.congrats.ResultOk
import com.gfreeman.takeme.home.model.map.Place
import contract.ArrivedToDestinationContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArrivedToDestinationPresenter(val congratsModel: ArrivedToDestinationContract.ArrivedToDestinationModel) : ArrivedToDestinationContract.IArrivedToDestinationPresenter<ArrivedToDestinationContract.ArrivedToDestinationView> {
    private lateinit var arrivedToDestinationView: ArrivedToDestinationContract.ArrivedToDestinationView
    override fun saveFavoriteRoute(startPlace: Place?, finishPlace: Place?) {
        CoroutineScope(Dispatchers.IO).launch {
            startPlace?.let{ safeStartPlace ->
                finishPlace?.let { safeFinishPlace ->
                    congratsModel.saveFavoriteRoute(safeStartPlace, safeFinishPlace, getCurrentDateFormatted()).let {
                        onFavoriteSaved(it)
                    }
                }
            } ?: withContext(Dispatchers.Main){
                arrivedToDestinationView.showErrorMessage("Places are null!")
            }
        }
    }

    override fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private suspend fun onFavoriteSaved(result: ResultDBOperation) {
        withContext(Dispatchers.Main) {
            when (result) {
                ResultOk -> arrivedToDestinationView.notifyFavoriteSaved()
                ResultError -> arrivedToDestinationView.showErrorMessage("No se pudo guardar la ruta como favorita")
            }
        }
    }

    override fun attachView(view: ArrivedToDestinationContract.ArrivedToDestinationView) {
        arrivedToDestinationView = view
    }
}