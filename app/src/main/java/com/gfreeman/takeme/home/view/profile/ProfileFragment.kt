package com.gfreeman.takeme.home.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gfreeman.takeme.home.viewmodel.profile.ProfileViewModel
import com.gfreeman.takeme.login.view.LoginActivity
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

/**
 * ProfileFragment using Compose Interoperability API (ComposeView inside Fragment) + Hilt DI.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
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
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogoutClick = {
                            activity?.finish()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}
