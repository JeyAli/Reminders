package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainAndroidTestCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    // Class under test
    private lateinit var repository: RemindersLocalRepository

    @get: Rule
    val mainCoroutineRule = MainAndroidTestCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        remindersDAO = database.reminderDao()
        repository =
            RemindersLocalRepository(
                remindersDAO,
                Dispatchers.Main
            )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun saveReminders_success() = mainCoroutineRule.runBlockingTest {
        val newReminder = ReminderDTO(
        "Visit Maiden Tower",
        "Walk down the Boulevard",
        "Baku, Azerbaijan",
        40.409264,
        49.867092
        )

        repository.saveReminder(newReminder)
        val remindersResult = repository.getReminder(newReminder.id) as Result.Success<ReminderDTO>
        val loadedReminder = remindersResult.data
        assertThat(loadedReminder, notNullValue())
        assertThat(loadedReminder.id == newReminder.id, `is`(true))
        assertThat(loadedReminder.description == newReminder.description, `is`(true))
        assertThat(loadedReminder.location == newReminder.location, `is`(true))
        assertThat(loadedReminder.latitude == newReminder.latitude, `is`(true))
        assertThat(loadedReminder.longitude == newReminder.longitude, `is`(true))    }

    @Test
    fun getReminderID_returnsError() = mainCoroutineRule.runBlockingTest {
        val newReminder = ReminderDTO(
            "Visit Maiden Tower",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )

        repository.saveReminder(newReminder)
        repository.deleteAllReminders()
        val remindersResult = repository.getReminder(newReminder.id) as Result.Error
        assertThat(remindersResult.message, Matchers.notNullValue())
        assertThat(remindersResult.message, `is`("Reminder not found!"))
    }

    @Test
    fun getReminders_returnsSuccess() = mainCoroutineRule.runBlockingTest {
        val newReminder = ReminderDTO(
            "Visit Maiden Tower",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )

        repository.saveReminder(newReminder)
        repository.deleteAllReminders()
        val remindersResult = repository.getReminders() as Result.Success<List<ReminderDTO>>
        val loadedReminder = remindersResult.data

        assertThat(loadedReminder, notNullValue())
        assertThat(loadedReminder.size, `is`(1))
    }
}
