package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//think what you we need here? First, database! We should create reference for it here

    private lateinit var repository: RemindersDatabase
    private lateinit var dao: RemindersDao
    private lateinit var context: Context

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // inMemoryDbBuilder is not real one, it will only hold memory in RAM
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        repository = Room.inMemoryDatabaseBuilder(
            context,
            RemindersDatabase::class.java
        )
            .build()


        dao = repository.reminderDao()
    }

    @After
    fun teardown() = repository.close()

    @Test
    fun save_reminder_successful() = runBlockingTest {
        val newReminder = ReminderDTO(
            "Visit Maiden Tower",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )
        dao.saveReminder(newReminder)

        val reminders = dao.getReminders()
        assertThat(reminders.isNotEmpty(), `is`(true))
    }

    @Test
    fun getReminderById_successful() = runBlockingTest {
        val newReminder = ReminderDTO(
            "Visit Maiden Tower",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )
        dao.saveReminder(newReminder)

        val loadedReminder = dao.getReminderById(newReminder.id)
        // check that what you added is what you get
        assertThat(loadedReminder as ReminderDTO, notNullValue())
        assertThat(loadedReminder.id == newReminder.id, `is`(true))
        assertThat(loadedReminder.description == newReminder.description, `is`(true))
        assertThat(loadedReminder.location == newReminder.location, `is`(true))
        assertThat(loadedReminder.latitude == newReminder.latitude, `is`(true))
        assertThat(loadedReminder.longitude == newReminder.longitude, `is`(true))
    }

    @Test
    fun deleteAllReminders_successful() = runBlockingTest {
        val newReminder = ReminderDTO(
            "Visit Zaha Hadid-designed complex",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )

        val newReminder2 = ReminderDTO(
            "Visit Maiden Tower",
            "Walk down the Boulevard",
            "Baku, Azerbaijan",
            40.409264,
            49.867092
        )

        dao.saveReminder(newReminder)
        dao.saveReminder(newReminder2)
        dao.deleteAllReminders()

        val reminders = dao.getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }
}

