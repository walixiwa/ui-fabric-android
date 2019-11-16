/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.officeuifabric.bottomsheet

import android.content.Context
import android.support.annotation.StyleRes
import android.view.View
import android.view.Window
import com.microsoft.officeuifabric.R
import com.microsoft.officeuifabric.drawer.DrawerDialog
import com.microsoft.officeuifabric.listitem.ListItemView
import com.microsoft.officeuifabric.listitem.ListSubHeaderView
import com.microsoft.officeuifabric.util.createImageView
import com.microsoft.officeuifabric.util.isVisible
import kotlinx.android.synthetic.main.dialog_drawer.view.*
import kotlinx.android.synthetic.main.view_bottom_sheet.view.*

/**
 * [BottomSheetDialog] is used to display a list of menu items in a modal dialog.
 */
class BottomSheetDialog : DrawerDialog, BottomSheetItem.OnClickListener {
    var onItemClickListener: BottomSheetItem.OnClickListener? = null

    private var clickedItem: BottomSheetItem? = null

    @JvmOverloads
    constructor(context: Context, items: ArrayList<BottomSheetItem>, headerItem: BottomSheetItem? = null, @StyleRes theme: Int = 0) : super(context, theme) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = layoutInflater.inflate(R.layout.view_bottom_sheet, container.drawer_content, false)

        val adapter = BottomSheetAdapter(context, items, theme)
        adapter.onBottomSheetItemClickListener = this
        view.bottom_sheet_items.adapter = adapter
        view.bottom_sheet_items.addItemDecoration(BottomSheetItemDivider(context))

        headerItem?.let {
            view.bottom_sheet_header_content.addView(createHeader(it))
            view.bottom_sheet_header_content.visibility = View.VISIBLE
            view.bottom_sheet_header_divider.isVisible = !isSingleLineHeader(it)
        }

        setContentView(view)
    }

    private fun createHeader(headerItem: BottomSheetItem): View {
        return if (isSingleLineHeader(headerItem))
            createSingleLineHeader(headerItem)
        else
            createDoubleLineHeader(headerItem)
    }

    private fun createSingleLineHeader(headerItem: BottomSheetItem): View {
        val headerView = ListSubHeaderView(context)
        headerView.titleColor = ListSubHeaderView.TitleColor.SECONDARY
        headerView.title = headerItem.title
        return headerView
    }

    private fun createDoubleLineHeader(headerItem: BottomSheetItem): View {
        val headerView = ListItemView(context)
        if (headerItem.imageId != BottomSheetItem.NO_ID)
            headerView.customView = context.createImageView(headerItem.imageId, headerItem.imageTint)
        headerView.title = headerItem.title
        headerView.subtitle = headerItem.subtitle
        return headerView
    }

    private fun isSingleLineHeader(headerItem: BottomSheetItem): Boolean {
        return headerItem.imageId == BottomSheetItem.NO_ID && headerItem.subtitle.isEmpty()
    }

    override fun onBottomSheetItemClick(item: BottomSheetItem) {
        clickedItem = item
        collapse()
    }

    override fun dismiss() {
        clickedItem?.let {
            onItemClickListener?.onBottomSheetItemClick(it)
            clickedItem = null
        }

        super.dismiss()
    }
}