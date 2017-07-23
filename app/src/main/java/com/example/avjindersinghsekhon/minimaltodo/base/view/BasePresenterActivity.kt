package com.example.avjindersinghsekhon.minimaltodo.base.view

import android.os.Bundle
import com.example.avjindersinghsekhon.minimaltodo.base.presenter.BasePresenter
import com.example.avjindersinghsekhon.minimaltodo.base.presenter.BaseView

/**
 * Created by taehwankwon on 7/22/17.
 */
abstract class BasePresenterActivity<in VIEW : BaseView, PRESENTER : BasePresenter<VIEW>> : BaseActivity(), BaseView {

    protected lateinit var presenter: PRESENTER
        private set

    abstract fun onCreatePresenter(): PRESENTER

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = onCreatePresenter()
        presenter.attachView(this as VIEW)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}