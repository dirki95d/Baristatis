package com.dr.baristatis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dr.baristatis.ui.elements.CoffeeDetails
import com.dr.baristatis.ui.elements.CoffeeEditor
import com.dr.baristatis.ui.elements.CoffeeMainScreen
import com.dr.baristatis.ui.theme.BaristatisTheme
import com.dr.baristatis.ui.vm.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets {
                Content(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Content(viewModel: MainViewModel) {
    BaristatisTheme {
        var showFAB by remember { mutableStateOf(true) }
        var showBack by remember { mutableStateOf(false) }
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsWithImePadding(),
            topBar =
            {
                // CompositionLocalProvider(LocalElevationOverlay provides null) {
                TopAppBar(
                    title = { Text("Baristatis") },
                    navigationIcon = {
                        AnimatedVisibility(
                            enter = fadeIn(), exit = fadeOut(),
                            visible = showBack
                        ) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
// https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#Scaffold(androidx.compose.ui.Modifier,androidx.compose.material.ScaffoldState,kotlin.Function0,kotlin.Function0,kotlin.Function1,kotlin.Function0,androidx.compose.material.FabPosition,kotlin.Boolean,kotlin.Function1,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,kotlin.Function1)
                                    // beim ??ffnen denk an coroutine!
                                }
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    }
                )
                //   }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    enter = fadeIn(), exit = fadeOut(),
                    visible = showFAB
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("edit/0")
                        }
                    ) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            },
            content = {
                NavHost(navController = navController, startDestination = "coffeeList") {
                    // main list
                    composable("coffeeList") {
                        showFAB = true
                        showBack = false
                        CoffeeMainScreen(viewModel, onMyCoffeeItemClicked = { item ->
                            navController.navigate("edit/${item.id}")
                        })
                    }
                    // editor
                    composable(
                        "edit/{itemId}",
                        arguments = listOf(navArgument("itemId") {
                            defaultValue = 0
                            type = NavType.IntType
                        })
                    ) { backStackEntry ->
                        showFAB = false
                        showBack = true
                        val coffeeData = backStackEntry.arguments?.getInt("itemId")?.let { itemId ->
                            viewModel.getItem(itemId)
                        }
                        CoffeeEditor(myCoffeeData = coffeeData,
                            onCoffeeDataAdded = {
                                it?.let {
                                    viewModel.saveCoffee(it)
                                    navController.popBackStack()
                                }
                            },
                            onCoffeeDataDeleted = {
                                it?.let {
                                    viewModel.deleteCoffeeData(it)
                                }
                                navController.popBackStack()
                            })
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BaristatisTheme {
        //CoffeeCard(myCoffeeData = getCoffeeData())
        //   CoffeeList({})
    }
}