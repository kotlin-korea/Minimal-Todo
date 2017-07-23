package com.example.avjindersinghsekhon.minimaltodo.base.presenter

/**
 * Created by taehwankwon on 7/22/17.
 */
abstract class CommonPresenter<VIEW : BaseView> : BasePresenter<VIEW> {

    protected var view: VIEW? = null
        private set

    override fun attachView(view: VIEW) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }
}