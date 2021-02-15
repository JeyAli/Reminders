package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var context: Context

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = FakeDataSource()
        saveReminderViewModel =
                SaveReminderViewModel(context as Application, dataSource)
    }

    @After
    fun tearDownKoin() {
        stopKoin()
    }

    @Test
    fun check_loading_success() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()
        val newReminder = ReminderDataItem(
                "Visit Maiden Tower",
                "Walk down the Boulevard",
                "Baku, Azerbaijan",
                40.409264,
                49.867092
        )

        saveReminderViewModel.saveReminder(newReminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue() == context.getString(R.string.reminder_saved), `is`(true))
        assertThat(saveReminderViewModel.navigationCommand.getOrAwaitValue() == NavigationCommand.Back, `is`(true))
    }

    @Test
    fun validateAndSaveReminder_returnsNull_whenTitleIsNull() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        val newReminder = ReminderDataItem(
                null,
                "Walk down the Boulevard",
                "Baku, Azerbaijan",
                40.409264,
                49.867092
        )

        assertThat(saveReminderViewModel.validateAndSaveReminder(newReminder).toString(), `is`("null"))
    }

    @Test
    fun validateAndSaveReminder_returnsNull_whenTitleIsEmpty() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        val newReminder = ReminderDataItem(
                "",
                "Walk down the Boulevard",
                "Baku, Azerbaijan",
                40.409264,
                49.867092
        )
        assertThat(saveReminderViewModel.validateAndSaveReminder(newReminder).toString(), `is`("null"))
    }


    @Test
    fun validateAndSaveReminder_returnsNull_whenLocationIsNull() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        val newReminder = ReminderDataItem(
                "Visit Maiden Tower",
                "Walk down the Boulevard",
                null,
                40.409264,
                49.867092
        )

        assertThat(saveReminderViewModel.validateAndSaveReminder(newReminder).toString(), `is`("null"))
    }

    @Test
    fun validateAndSaveReminder_returnsNull_whenLocationIsEmpty() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        val newReminder = ReminderDataItem(
                "Visit Maiden Tower",
                "Walk down the Boulevard",
                "",
                40.409264,
                49.867092
        )
        assertThat(saveReminderViewModel.validateAndSaveReminder(newReminder).toString(), `is`("null"))
    }
}