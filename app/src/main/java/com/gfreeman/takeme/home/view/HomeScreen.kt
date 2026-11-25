package com.gfreeman.takeme.home.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.view.fav.FavoritesScreen
import com.gfreeman.takeme.home.view.map.MapScreen
import com.gfreeman.takeme.home.view.map.PermissionUtils
import com.gfreeman.takeme.home.view.profile.ProfileScreen
import com.gfreeman.takeme.home.viewmodel.favs.FavoritesViewModel
import com.gfreeman.takeme.home.viewmodel.map.MapViewModel
import com.gfreeman.takeme.home.viewmodel.profile.ProfileViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme

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
    val context = LocalContext.current

    var showRationaleDialog by remember { mutableStateOf(false) }
    var isDeniedPermanently by rememberSaveable { mutableStateOf(false) }

    // 1. Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (PermissionUtils.hasLocationPermission(context)) {
            mapViewModel.getCurrentPosition()
        } else {
            if (!PermissionUtils.shouldShowLocationRationale(context)){
                isDeniedPermanently = true
                //showRationaleDialog = true
            }
            Toast.makeText(
                context,
                "Location permission is needed to show your position and calculate routes.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun checkAndRequestPermissions(){
        when {
            PermissionUtils.hasLocationPermission(context) ->{
                mapViewModel.getCurrentPosition()
                if (!PermissionUtils.hasNotificationPermission(context)) {
                    permissionLauncher.launch(PermissionUtils.getPermissionsToRequest(context))
                }
            }
            PermissionUtils.shouldShowLocationRationale(context) -> {
                isDeniedPermanently = false
                showRationaleDialog = true
            }
            isDeniedPermanently -> {
                showRationaleDialog = true
            }
            else -> {
                permissionLauncher.launch(PermissionUtils.getPermissionsToRequest(context))
            }
        }
    }

    // 2. Request permissions on initial screen load
    LaunchedEffect(Unit) {
        checkAndRequestPermissions()
    }
    if (showRationaleDialog){
        if (isDeniedPermanently){
            PermissionAlertDialog(
                dialogTitle = "Permission required",
                dialogContent = "Location permission is required. In the settings screen:\n\n1. Tap 'Permissions'\n2. Select 'Location'\n3. Choose 'Allow only while using the app'",
                confirmButtonText = "Go to settings",
                onDismissRequest = {
                    showRationaleDialog = false
                },
                onConfirmButton = {
                    isDeniedPermanently = false
                    showRationaleDialog = false
                    PermissionUtils.openAppSettings(context)
                },
            )
        } else {
            PermissionAlertDialog(
                dialogTitle = "Location Access Needed",
                dialogContent = "TakeMe needs your location to show your position on the map and calculate routes to your destination.",
                confirmButtonText = "Grant Permission",
                onDismissRequest = {
                    showRationaleDialog = false
                },
                onConfirmButton = {
                    showRationaleDialog = false
                    permissionLauncher.launch(PermissionUtils.getPermissionsToRequest(context))
                },
            )
        }
    }
    TakeMeTheme() {
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
                                    if (PermissionUtils.hasLocationPermission(context)){
                                        selectedTab = HomeTab.MAP
                                        mapViewModel.getRouteFromFavs(startPlace, destinationPlace)
                                    } else {
                                        checkAndRequestPermissions()
                                    }
                                }
                            )
                        }
                        HomeTab.MAP -> {
                            MapScreen(
                                viewModel = mapViewModel,
                                onNavigateToWeather = onNavigateToWeather,
                                onRequestPermission = {
                                    checkAndRequestPermissions()
                                }
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
}
@Composable
fun PermissionAlertDialog(
    dialogTitle: String,
    dialogContent: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmButton: () -> Unit
){
    AlertDialog(
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = ""
            )
        },
        title = {
            Text(
                text = dialogTitle
            )
        },
        text = {
            Text(
                text = dialogContent
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ElevatedButton(
                onClick = onConfirmButton
            ) {
                Text(
                    text = confirmButtonText
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "Dismiss"
                )
            }
        }
    )
}
