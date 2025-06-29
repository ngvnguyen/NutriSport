package com.sf.nutrisport.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T> (val data:T) : RequestState<T>()
    data class Error<out T> (val message:String): RequestState<T>()

    fun isIdle() = this is Idle
    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull() = if (isSuccess()) getSuccessData() else null
    fun getErrorMessage() = (this as Error).message
}


@Composable
fun <T> RequestState<T>.DisplayResult(
    modifier: Modifier = Modifier,
    onIdle: (@Composable ()->Unit)? = null,
    onLoading:(@Composable ()->Unit)? = null,
    onError : (@Composable (String)->Unit)? = null,
    onSuccess:(@Composable (T)->Unit),
    transitionSpec: ContentTransform? =
        fadeIn(tween(durationMillis = 800))
                + scaleIn(tween(durationMillis = 400))
                togetherWith scaleOut(tween(durationMillis = 400))
                + fadeOut(tween(durationMillis = 800)),
    backgroundColor : Color? = null
){
    AnimatedContent(
        targetState = this,
        transitionSpec = {transitionSpec?: (EnterTransition.None togetherWith ExitTransition.None)},
        label = "Content Animation",
        modifier = modifier
            .background(color = backgroundColor ?: Color.Unspecified)
    ) {state->
        when (state){
            is RequestState.Success ->{
                onSuccess(state.getSuccessData())
            }

            is RequestState.Error -> {
                onError?.invoke(state.getErrorMessage())
            }
            is RequestState.Idle -> {
                onIdle?.invoke()
            }
            is RequestState.Loading -> {
                onLoading?.invoke()
            }
        }

    }
}
