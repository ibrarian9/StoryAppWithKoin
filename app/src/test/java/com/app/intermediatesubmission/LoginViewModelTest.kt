package com.app.intermediatesubmission

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.RequestLogin
import com.app.intermediatesubmission.di.models.UserModel
import com.app.intermediatesubmission.ui.listStory.ListStoryViewModel
import com.app.intermediatesubmission.ui.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var listStoryViewModel: ListStoryViewModel

    // Dummy data for testing
    private val dummyRequestLogin = RequestLogin("test@gmail.com", "12345678")

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
        listStoryViewModel = ListStoryViewModel(storyRepository)
    }

    @Test
    fun `when Login Success Should Return Success`() = runTest {
        // Given
        val expectedResponse = Result.success("Login Successful")

        // When
        Mockito.`when`(storyRepository.postLogin(dummyRequestLogin)).thenReturn(expectedResponse)

        // Action
        loginViewModel.postLogin(dummyRequestLogin)
        val actualResult = loginViewModel.loginResult.getOrAwaitValue()

        // Verify
        Mockito.verify(storyRepository).postLogin(dummyRequestLogin)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.isSuccess)
        Assert.assertEquals("Login Successful", actualResult.getOrNull())
    }

    @Test
    fun `when Login Fails Should Return Error`() = runTest {
        // Given
        val expectedResponse = Result.failure<String>(Exception("Login Failed"))

        // When
        Mockito.`when`(storyRepository.postLogin(dummyRequestLogin)).thenReturn(expectedResponse)

        // Action
        loginViewModel.postLogin(dummyRequestLogin)
        val actualResult = loginViewModel.loginResult.getOrAwaitValue()

        // Verify
        Mockito.verify(storyRepository).postLogin(dummyRequestLogin)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult.isFailure)
        Assert.assertEquals("Login Failed", actualResult.exceptionOrNull()?.message)
    }

    @Test
    fun `when Logout Should Clear Session`() = runTest {
        // Given
        val dummySession = UserModel(isLogin = false, name = "", token = "")

        // When
        Mockito.`when`(storyRepository.getSession()).thenReturn(MutableLiveData(dummySession).asFlow())

        // Action
        listStoryViewModel.logout()
        val actualSession = loginViewModel.getSession().getOrAwaitValue()

        // Verify
        Mockito.verify(storyRepository).logout()
        Assert.assertNotNull(actualSession)
        Assert.assertFalse(actualSession.isLogin)
    }
}