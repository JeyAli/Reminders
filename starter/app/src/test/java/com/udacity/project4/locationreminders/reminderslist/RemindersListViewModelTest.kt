package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.EventLog
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

private lateinit var dataSource: FakeDataSource
private lateinit var remindersListViewModel: RemindersListViewModel
private lateinit var context: Context


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setUpViewModel() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = FakeDataSource()
        remindersListViewModel =
                RemindersListViewModel(context as Application, dataSource)
    }

    @After
    fun tearDownKoin() {
        stopKoin()
    }

    @Test
    fun remindersList_notEmpty() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        val newReminder = ReminderDTO(
                "Visit Maiden Tower",
                "Walk down the Boulevard",
                "Baku, Azerbaijan",
                40.409264,
                49.867092
        )

        dataSource.saveReminder(newReminder)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isNotEmpty(), `is`(true))    }

    @Test
    fun remindersList_isEmpty() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so you can verify initial values.

        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, `is`(0))
    }

    @Test
    fun shouldReturnError_exceptionSnackBar() = mainCoroutineRule.runBlockingTest {
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue() == "Test exception", `is`(true))
    }
}
