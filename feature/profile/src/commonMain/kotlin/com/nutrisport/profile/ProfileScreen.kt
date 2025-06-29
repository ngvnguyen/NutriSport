package com.nutrisport.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.component.ErrorCard
import com.sf.nutrisport.component.LoadingCard
import com.sf.nutrisport.component.PrimaryButton
import com.sf.nutrisport.component.ProfileForm
import com.sf.nutrisport.domain.Country
import com.sf.nutrisport.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack:()->Unit
){
    val viewModel = koinViewModel<ProfileViewModel>()
    val screenReady = viewModel.screenReady
    val screenState = viewModel.screenState

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontFamily = bebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = IconPrimary,
                    actionIconContentColor = IconPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Close Menu",
                            tint = IconPrimary
                        )
                    }
                }
            )
        }
    ) {padding->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = 24.dp)

        ) {
            screenReady.DisplayResult(
                onError = {
                    ErrorCard(
                        message = it,
                        fontSize = FontSize.REGULAR
                    )
                },
                onSuccess = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ProfileForm(
                            modifier = Modifier.weight(1f),
                            country = screenState.country,
                            onCountrySelect = viewModel::updateCountry,
                            firstName = screenState.firstName,
                            onFirstNameChange = viewModel::updateFirstName,
                            lastName = screenState.lastName,
                            onLastNameChange = viewModel::updateLastName,
                            email = screenState.email,
                            city = screenState.city,
                            onCityChange = viewModel::updateCity,
                            postalCode = screenState.postalCode,
                            onPostalCodeChange = viewModel::updatePostalCode,
                            address = screenState.address,
                            onAddressChange = viewModel::updateAddress,
                            phoneNumber = screenState.phoneNumber?.number,
                            onPhoneNumberChange = viewModel::updatePhoneNumber
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        PrimaryButton(
                            text = "Update",
                            icon = Resources.Icon.Checkmark,
                            enabled = true,
                            onClick = {}
                        )
                    }
                },
                onIdle = {},
                onLoading = {
                    LoadingCard(modifier = Modifier.fillMaxSize())
                }
            )

        }

    }
}