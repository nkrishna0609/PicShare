package ca.nkrishnaswamy.picshare.ui.recyclerviewAdapters

import ca.nkrishnaswamy.picshare.data.models.roomModels.UserModel

interface OnItemClickListener {
    fun onItemClick(account : UserModel)
}