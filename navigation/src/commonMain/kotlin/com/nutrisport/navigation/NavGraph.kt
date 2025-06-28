package com.nutrisport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.nutrisport.auth.AuthScreen
import com.nutrisport.home.HomeGraphScreen
import com.sf.nutrisport.navigation.Screen

@Composable
fun SetupNavGraph(
    startDestination: Screen
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable<Screen.Auth> {
            AuthScreen(navigateToHome = {
                navController.navigate(Screen.HomeGraph){
                    popUpTo<Screen.Auth>{
                        inclusive = true
                    }
                }
            })
        }

        composable<Screen.HomeGraph>{
            HomeGraphScreen(
                navigateToAuth = {
                    navController.navigate(Screen.Auth){
                        popUpTo<Screen.HomeGraph>{
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
    
}