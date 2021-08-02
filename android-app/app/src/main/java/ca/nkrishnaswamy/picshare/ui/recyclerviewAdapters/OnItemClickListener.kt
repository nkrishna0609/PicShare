package ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters

import ca.nkrishnaswamy.picshare.data.models.UserModel

interface OnItemClickListener {
    fun onItemClick(account : UserModel)
}