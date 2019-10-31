package com.practice.rxjava2_livedata_testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.practice.rxjava2_livedata_testing.api.GithubApi
import com.practice.rxjava2_livedata_testing.model.GithubAccount
import com.practice.rxjava2_livedata_testing.viewmodel.GithubActivityViewModel
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class GithubActivityViewModelTest {
    /*
    A JUnit test rule that swaps the background executor used by the Architecture components
    with a different one which executes each task synchronously
    Rule used for host side tests that use the Architecture Components
    * */
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    // Test rule for making the RxJava run synchronously in unit test
    companion object {
        @ClassRule
        @JvmField
        val schedulers =  RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var githubApi: GithubApi

    @Mock
    lateinit var observer: Observer<GithubAccount>

    lateinit var githubViewModel: GithubActivityViewModel

    @Before
    fun setup()
    {
        // initialize the view model with a mocked github api
        githubViewModel  =  GithubActivityViewModel(githubApi)
    }

    @Test
    fun shouldReturnGithubAccountLoginName()
    {
        // Arrange
        // mock data
        val githubAccountName = "google"
        val githubAccount = GithubAccount(githubAccountName)
        // making the github api return back the mock data
        Mockito.`when`(githubApi.getGithubAccountObservable(githubAccountName)).
            thenReturn(Observable.just(Response.success(githubAccount)))
        // Act
        githubViewModel.gitHubAccountDetails.observeForever(observer)
        githubViewModel.fetchGithubAccountInfo(githubAccountName)
        // Assert
        // now we check that the account name should match
        assert(githubViewModel.gitHubAccountDetails.value!!.login == githubAccountName)
    }

}