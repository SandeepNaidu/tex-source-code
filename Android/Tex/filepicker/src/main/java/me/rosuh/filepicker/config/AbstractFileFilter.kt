package me.rosuh.filepicker.config

import me.rosuh.filepicker.bean.FileItemBeanImpl

abstract class AbstractFileFilter {
    abstract fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl>
}