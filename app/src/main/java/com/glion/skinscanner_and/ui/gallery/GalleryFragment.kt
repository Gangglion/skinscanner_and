package com.glion.skinscanner_and.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.databinding.FragmentGalleryBinding
import com.glion.skinscanner_and.ui.MainActivity
import com.glion.skinscanner_and.ui.base.BaseFragment
import com.glion.skinscanner_and.ui.enums.ScreenType
import com.glion.skinscanner_and.util.Utility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding, MainActivity>(R.layout.fragment_gallery) {
    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> {
                val uri = result.data?.data
                if (uri != null) {
                    val bitmap = Utility.convertUriToBitmap(uri, mContext)
                    bitmap?.let {
                        Utility.saveBitmapInCache(bitmap, mContext)
                        mParentActivity.changeFragment(ScreenType.Resize)
                    }
                } else {
                    with(mParentActivity) {
                        showToast(mContext.getString(R.string.fail_get_image))
                        hideProgress()
                        changeFragment(ScreenType.Home)
                    }
                }
            }
            else -> {
                with(mParentActivity){
                    hideProgress()
                    changeFragment(ScreenType.Home)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        galleryResultLauncher.launch(intent)
    }
}