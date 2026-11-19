package com.gfreeman.takeme.home.view.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.viewmodel.profile.ProfileViewModel

/**
 * ProfileScreen Composable.
 *
 * REPLACING CUSTOM VIEWS:
 * In XML, ProfileSectionItem was a custom ConstraintLayout view. In Compose, we replace it with
 * lightweight, stateless, reusable Composable functions (ProfileOptionItem & ProfileCardSection).
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Screen Title
        Text(
            text = "MY PROFILE",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Header Card
        UserProfileCard(
            name = uiState.name.ifEmpty { "User Name" },
            email = uiState.email.ifEmpty { "user@example.com" }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // General Settings Card
        ProfileCardSection(title = "GENERAL") {
            ProfileOptionItem(
                iconRes = R.drawable.user_settings_item,
                title = "Edit Profile",
                subtitle = "Name, phone, email",
                onClick = { /* Handle Edit Profile */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            ProfileOptionItem(
                iconRes = R.drawable.key_password_settings,
                title = "Change Password",
                subtitle = "Change account password",
                onClick = { /* Handle Change Password */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            ProfileOptionItem(
                iconRes = R.drawable.user_terms_settings,
                title = "Terms of Use",
                subtitle = "Legal and privacy",
                onClick = { /* Handle Terms */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            ProfileOptionItem(
                iconRes = R.drawable.credit_card_settings,
                title = "Add Card",
                subtitle = "Manage payment methods",
                onClick = { /* Handle Add Card */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences Settings Card
        ProfileCardSection(title = "PREFERENCES") {
            ProfileOptionItem(
                iconRes = R.drawable.notification_settings,
                title = "Notifications",
                subtitle = "Push and email notifications",
                onClick = { /* Handle Notifications */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            ProfileOptionItem(
                iconRes = R.drawable.help_settings,
                title = "FAQ & Support",
                subtitle = "Help center and contact us",
                onClick = { /* Handle FAQ */ }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            ProfileOptionItem(
                iconRes = R.drawable.user_logout_settings,
                title = "Log Out",
                subtitle = "Log out from this device",
                onClick = {
                    viewModel.logOut()
                    onLogoutClick()
                }
            )
        }
    }
}

/**
 * Header card showing User picture, name, and email.
 */
@Composable
fun UserProfileCard(
    name: String,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_circle_profile_com),
                contentDescription = "User Profile Picture",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Reusable Card container for a section of options.
 */
@Composable
fun ProfileCardSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            content()
        }
    }
}

/**
 * Reusable Composable replacing legacy ProfileSectionItem custom view.
 */
@Composable
fun ProfileOptionItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
