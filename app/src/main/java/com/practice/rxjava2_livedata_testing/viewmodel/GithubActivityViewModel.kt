package com.practice.rxjava2_livedata_testing.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practice.rxjava2_livedata_testing.api.GithubApi
import com.practice.rxjava2_livedata_testing.model.GithubAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class GithubActivityViewModel(private val githubApi: GithubApi) : ViewModel() {
    private val compositeDisposable = CompositeDisposable();
    var gitHubAccountDetails = MutableLiveData<GithubAccount>();

    internal fun fetchGithubAccountInfo(username :String){
        val disposable = githubApi.getGithubAccountObservable(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<GithubAccount>>(){
                override fun onComplete() {
                }

                override fun onNext(response : Response<GithubAccount>) {
                    gitHubAccountDetails.value = response.body();
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace();
                }

            })

        compositeDisposable.add(disposable);
    }

    // Invoked when the Activity is destroyed
    public override fun onCleared() {
        compositeDisposable.dispose();
        super.onCleared()
    }

}