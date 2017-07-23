package com.example.avjindersinghsekhon.minimaltodo.base.presenter

/**
 * Created by taehwankwon on 7/22/17.
 */
interface BasePresenter<in VIEW : BaseView> {

    /**
     * View Attach.
     */
    fun attachView(view: VIEW)

    /**
     * View detach
     */
    fun detachView()
}