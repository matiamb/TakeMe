package com.gfreeman.takeme.home.view.fav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gfreeman.takeme.home.viewmodel.favs.FavoritesViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import com.google.android.material.transition.platform.MaterialSharedAxis
import contract.HomeContract
import dagger.hilt.android.AndroidEntryPoint

/**
 * FavFragment using Compose Interoperability API (ComposeView inside Fragment) + Hilt Dependency Injection.
 *
 * WHY @AndroidEntryPoint:
 * Marks this Fragment for Hilt dependency injection, allowing by viewModels() to automatically
 * create and inject HiltViewModel instances (FavoritesViewModel).
 */
@AndroidEntryPoint
class FavFragment : Fragment() {

    // Hilt automatically injects FavoritesViewModel!
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                TakeMeTheme {
                    FavoritesScreen(
                        viewModel = favoritesViewModel,
                        onRouteClick = { startPlace, destinationPlace ->
                            getParentView()?.openMapsScreenWithDestination(startPlace, destinationPlace)
                        }
                    )
                }
            }
        }
    }

    private fun getParentView(): HomeContract.HomeView? {
        return activity as? HomeContract.HomeView
    }
}
