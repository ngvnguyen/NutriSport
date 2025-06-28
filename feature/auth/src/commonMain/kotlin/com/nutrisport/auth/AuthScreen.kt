package com.nutrisport.auth

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nutrisport.auth.component.GoogleButton
import com.sf.nutrisport.Alpha
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.Surface
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.TextSecondary
import com.sf.nutrisport.bebasNeueFont
import rememberMessageBarState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.sf.nutrisport.SurfaceBrand
import com.sf.nutrisport.SurfaceError
import com.sf.nutrisport.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    navigateToHome:()->Unit
){
    val authViewModel = koinViewModel<AuthViewModel>()
    val scope = rememberCoroutineScope()

    val messageBarState = rememberMessageBarState()
    var loadingState by remember { mutableStateOf(false) }

    Scaffold { padding->
        ContentWithMessageBar(
            modifier = Modifier.padding(padding),
            messageBarState = messageBarState,
            errorMaxLines = 2,
            contentBackgroundColor = Surface,
            successContainerColor = SurfaceBrand,
            errorContainerColor = SurfaceError,
            successContentColor = TextPrimary,
            errorContentColor = TextWhite
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "NUTRISPORT",
                        textAlign =  TextAlign.Center,
                        fontFamily = bebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextSecondary
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(Alpha.HALF),
                        text = "Sign in to continue",
                        textAlign =  TextAlign.Center,
                        fontFamily = bebasNeueFont(),
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextPrimary
                    )
                }

                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = {result->
                        result.onSuccess { user->
                            authViewModel.createCustomer(
                                user = user,
                                onSuccess = {
                                    scope.launch {
                                        messageBarState.addSuccess("Login successful!")
                                        delay(1000)
                                        navigateToHome()
                                    }
                                },
                                onError = {message->messageBarState.addError(message)}
                            )
                            loadingState = false
                        }.onFailure { error->
                            messageBarState.addError(error.message.toString())
                            loadingState = false
                        }
                    }
                ) {
                    GoogleButton(
                        loading = loadingState,
                        onClick = {
                            loadingState = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        }
                    )
                }


            }
        }

    }
}