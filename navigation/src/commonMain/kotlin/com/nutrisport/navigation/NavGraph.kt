package com.nutrisport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nutrisport.admin_panel.AdminPanelScreen

import com.nutrisport.auth.AuthScreen
import com.nutrisport.category_search.CategorySearchScreen
import com.nutrisport.details.DetailsScreen
import com.nutrisport.home.HomeGraphScreen
import com.nutrisport.manage_product.ManageProductScreen
import com.nutrisport.profile.ProfileScreen
import com.sf.nutrisport.domain.ProductCategory
import com.sf.nutrisport.navigation.Screen
import com.sf.nutrisport.navigation.Screen.CategorySearch

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
                },
                navigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                navigateToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                navigateToDetails = {id->
                    navController.navigate(Screen.Details(id))
                },
                navigateToCategorySearchScreen = {categoryName->
                    navController.navigate(Screen.CategorySearch(
                        categoryName = categoryName
                    ))
                }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(navigateBack = {
                navController.navigateUp()
            })
        }

        composable<Screen.AdminPanel> {
            AdminPanelScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToManagerProduce = {id->
                    navController.navigate(Screen.ManageProduct(id))
                }
            )
        }

        composable<Screen.ManageProduct> {it->
            val id = it.toRoute<Screen.ManageProduct>().id
            ManageProductScreen(
                id = id,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.Details> {it->
            DetailsScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.CategorySearch> {
            val category = ProductCategory.valueOf(it.toRoute<CategorySearch>().categoryName)
            CategorySearchScreen(
                navigateToDetails = {id->
                    navController.navigate(Screen.Details(id))
                },
                navigateBack = {
                    navController.navigateUp()
                },
                category = category
            )
        }
    }
    
}