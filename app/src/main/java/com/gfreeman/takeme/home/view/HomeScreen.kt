package com.gfreeman.takeme.home.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.view.fav.FavoritesScreen
import com.gfreeman.takeme.home.view.map.MapScreen
import com.gfreeman.takeme.home.view.profile.ProfileScreen
import com.gfreeman.takeme.home.viewmodel.favs.FavoritesViewModel
import com.gfreeman.takeme.home.viewmodel.map.MapViewModel
import com.gfreeman.takeme.home.viewmodel.profile.ProfileViewModel

/**
 * Enumeration of main bottom navigation destinations.
 */
enum class HomeTab(val labelRes: Int, val iconRes: Int) {
    FAVORITES(R.string.nav_home_btn1, R.drawable.favorite),
    MAP(R.string.nav_home_btn2, R.drawable.map_arrow_square),
    PROFILE(R.string.nav_home_btn3, R.drawable.profile_circle)
}

/**
 * HomeScreen Composable replacing activity_home.xml and Fragment transactions.
 *
 * WHY THIS APPROACH:
 * Replaces legacy Fragment transactions & BottomNavigationView with pure Compose Scaffold & NavigationBar,
 * featuring smooth, direction-aware tab transition animations via AnimatedContent.
 */
@Composable
fun HomeScreen(
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToWeather: (Point) -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(HomeTab.MAP) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = stringResource(id = tab.labelRes),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(stringResource(id = tab.labelRes)) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // AnimatedContent provides smooth direction-aware slide & fade transitions between tabs
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    val slideIn = slideInHorizontally(animationSpec = tween(300))
                    {
                        width -> if (forward) width else -width
                    } + fadeIn(animationSpec = tween(300))
                    val slideOut = slideOutHorizontally(animationSpec = tween(300))
                    {
                        width -> if (forward) -width else width
                    } + fadeOut(animationSpec = tween(300))
                    slideIn togetherWith slideOut
                },
                label = "TabScreenTransition"
            ) { tab ->
                when (tab) {
                    HomeTab.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = favoritesViewModel,
                            onRouteClick = { startPlace: Place, destinationPlace: Place ->
                                selectedTab = HomeTab.MAP
                                mapViewModel.getRouteFromFavs(startPlace, destinationPlace)
                            }
                        )
                    }
                    HomeTab.MAP -> {
                        MapScreen(
                            viewModel = mapViewModel,
                            onNavigateToWeather = onNavigateToWeather
                        )
                    }
                    HomeTab.PROFILE -> {
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onLogoutClick = onLogoutClick
                        )
                    }
                }
            }
        }
    }
}
