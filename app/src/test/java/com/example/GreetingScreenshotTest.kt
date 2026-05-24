package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.FoodDatabase
import com.example.data.pref.BankPrefManager
import com.example.data.repository.FoodRepository
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoodViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = Room.inMemoryDatabaseBuilder(context, FoodDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    val repository = FoodRepository(database.foodDao())
    val bankPrefManager = BankPrefManager(context)
    val viewModel = FoodViewModel(repository, bankPrefManager)

    composeTestRule.setContent { 
        MyApplicationTheme { 
            MainLayout(viewModel) 
        } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    database.close()
  }
}
