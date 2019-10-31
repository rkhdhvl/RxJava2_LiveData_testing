package com.practice.rxjava2_livedata_testing.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.practice.rxjava2_livedata_testing.R
import com.practice.rxjava2_livedata_testing.api.GithubApi
import com.practice.rxjava2_livedata_testing.api.Network
import com.practice.rxjava2_livedata_testing.model.GithubAccount
import com.practice.rxjava2_livedata_testing.viewmodel.GithubActivityViewModel
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_github.*

class GithubActivity : AppCompatActivity() {

    lateinit var viewModel : GithubActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github)

        val factory = GithubActivityViewModelFactory(Network.getGitHubApi(this))
        viewModel = ViewModelProviders.of(this,factory).get(GithubActivityViewModel::class.java)
        viewModel.fetchGithubAccountInfo("google")
        initObserver()
    }

    private fun initObserver()
    {
        viewModel.gitHubAccountDetails.observe(this, androidx.lifecycle.Observer {
            githubAccount ->  textView.text = githubAccount!!.login + "\n" + githubAccount.createdAt
        })
    }

    /* Since we have a constructor paramter passed to the view Model , creating a factory class that would return an
    * instance of GithubActivityViewModel*/

    // this is for passing the constructor parameter into the ViewModel
    class GithubActivityViewModelFactory(private val githubApi: GithubApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GithubActivityViewModel::class.java)) {
                return GithubActivityViewModel(githubApi) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
