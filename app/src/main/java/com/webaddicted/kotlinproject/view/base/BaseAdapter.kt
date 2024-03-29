package com.webaddicted.kotlinproject.view.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.global.common.AppApplication

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val mContext = AppApplication.context
    protected abstract fun getListSize(): Int

    companion object {
        private val TAG = BaseAdapter::class.java.simpleName
    }

    override fun getItemCount(): Int {
        return getListSize()
    }

    protected abstract fun getLayoutId(viewType: Int): Int

    protected abstract fun onBindTo(rowBinding: ViewDataBinding, position: Int)

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowBindingUtil: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getLayoutId(viewType),
            parent,
            false
        )
        return ViewHolder(rowBindingUtil)
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder)
            (holder as ViewHolder).binding(position)
    }

    /**
     * placeholder type for image
     *
     * @param placeholderType position of string array placeholder
     * @return
     */
    protected open fun getPlaceHolder(placeholderType: Int): String {
        val placeholderArray = mContext.getResources().getStringArray(R.array.image_loader)
        return placeholderArray[placeholderType]
    }

    /**
     * view holder
     */
    inner class ViewHolder(private val mRowBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(mRowBinding.getRoot()) {
        /**
         * @param position current item position
         */
        fun binding(position: Int) {
            //            sometime adapter position  is -1 that case handle by position
            if (getAdapterPosition() >= 0) onBindTo(mRowBinding, getAdapterPosition())
            else onBindTo(mRowBinding, position)
        }
    }

    protected open fun onClickListener(view: View?, position: Int){
        view?.setOnClickListener({ getClickEvent(view, position)})
    }

    protected open fun getClickEvent(view: View?, position: Int) {

    }

}