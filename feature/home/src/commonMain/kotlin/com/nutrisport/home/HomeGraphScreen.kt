package com.nutrisport.home

import ContentWithMessageBar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nutrisport.home.component.BottomBar
import com.nutrisport.home.component.CustomDrawer
import com.nutrisport.home.domain.BottomBarDestination
import com.nutrisport.home.domain.CustomDrawerState
import com.nutrisport.home.domain.isOpened
import com.nutrisport.home.domain.opposite
import com.sf.nutrisport.FontSize
import com.sf.nutrisport.IconPrimary
import com.sf.nutrisport.Resources
import com.sf.nutrisport.Surface
import com.sf.nutrisport.SurfaceLighter
import com.sf.nutrisport.TextPrimary
import com.sf.nutrisport.bebasNeueFont
import com.sf.nutrisport.navigation.Screen
import com.sf.nutrisport.util.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    navigateToAuth : ()->Unit
) {
    val navController = rememberNavController()

    val currentRoute by navController.currentBackStackEntryAsState()
    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute?.destination?.route.toString()
            when{
                route.contains(BottomBarDestination.ProductsOverview.screen.toString())-> BottomBarDestination.ProductsOverview
                route.contains(BottomBarDestination.Cart.screen.toString())-> BottomBarDestination.Cart
                route.contains(BottomBarDestination.Categories.screen.toString())-> BottomBarDestination.Categories
                else -> BottomBarDestination.ProductsOverview
            }
        }
    }

    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }
    val screenWidth = remember { getScreenWidth() }

    val offsetValue by remember { derivedStateOf {
        (screenWidth/1.5).dp
    } }
    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) SurfaceLighter else Surface
    )
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )
    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )
    val messageBarState = rememberMessageBarState()

    val viewModel = koinViewModel<HomeGraphViewModel>()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ){
        CustomDrawer(
            onProfileClick = {},
            onSignOutClick = {viewModel.signOut(
                onSuccess = {
                    navigateToAuth()
                },
                onError = {message->
                    messageBarState.addError(message)
                }
            )},
            onContactUsClick = {},
            onAdminPanelClick = {},

        )
        Box(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(size = animatedRadius))
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(animatedRadius)
                )
        ){
            Scaffold(
                containerColor = Surface,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedDestination
                            ) {destination->
                                Text(
                                    text = destination.title,
                                    fontFamily = bebasNeueFont(),
                                    fontSize = FontSize.LARGE,
                                    color = TextPrimary
                                )
                            }
                        },
                        navigationIcon = {
                            AnimatedContent(targetState = drawerState) {drawer->
                                if (drawer.isOpened()){
                                    IconButton(onClick = {drawerState = drawerState.opposite()}) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close Menu",
                                            tint = IconPrimary
                                        )
                                    }
                                }else{
                                    IconButton(onClick = {drawerState = drawerState.opposite()}) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Menu),
                                            contentDescription = "Menu Icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Surface,
                            scrolledContainerColor = Surface,
                            titleContentColor = TextPrimary,
                            navigationIconContentColor = IconPrimary,
                            actionIconContentColor = IconPrimary
                        )
                    )
                }
            ) { padding->
                ContentWithMessageBar(
                    messageBarState = messageBarState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()),
                    errorMaxLines = 2,
                    contentBackgroundColor = Surface
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ){
                        NavHost(
                            navController = navController,
                            startDestination = Screen.ProductsOverview,
                            modifier = Modifier.weight(1f)
                        ){
                            composable<Screen.ProductsOverview> { }
                            composable<Screen.Cart> { }
                            composable<Screen.Categories> {  }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.padding(12.dp)){
                            BottomBar(
                                selected = selectedDestination ,
                                onSelect ={ destination->
                                    navController.navigate(destination.screen){
                                        launchSingleTop = true
                                        popUpTo<Screen.ProductsOverview>{
                                            inclusive = false
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}