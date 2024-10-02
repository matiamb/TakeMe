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

    override fun saveFavoriteRoute(startPlace: Place?, finishPlace: Place?, isFav: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            startPlace?.let{ safeStartPlace ->
                finishPlace?.let { safeFinishPlace ->
                    when(isFav){
                        true -> onFavoriteSaved(
                            congratsModel.saveFavoriteRoute(
                                safeStartPlace,
                                safeFinishPlace,
                                getCurrentDateFormatted()
                            )
                        )

                        false -> onFavoriteDeleted(
                            congratsModel.deleteFavoriteRoute(
                                safeStartPlace,
                                safeFinishPlace
                            )
                        )
                    }
                }
            } ?: withContext(Dispatchers.Main){
                arrivedToDestinationView.showErrorMessage("Places are null!")
            }
        }
    }

    private suspend fun onFavoriteDeleted(resultDBOperation: ResultDBOperation) {
        withContext(Dispatchers.Main) {
            when (resultDBOperation){
                ResultOk -> arrivedToDestinationView.notifyFavoriteDeleted()
                ResultError -> arrivedToDestinationView.showErrorMessage("Cannot delete this route")
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