package com.pipi.pipix.src.deleteitem

import android.content.Intent
import android.os.Bundle
import com.pipi.pipix.config.BaseActivity
import com.pipi.pipix.databinding.ActivityDeleteitemBinding
import com.pipi.pipix.src.main.Fragment.ProfileFragment
import com.pipi.pipix.src.main.Fragment.ProfileRecyclerviewAdapter.Companion.dataList

class DeleteItemActivity : BaseActivity<ActivityDeleteitemBinding>(ActivityDeleteitemBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.deleteitemImageviewBtnBack.setOnClickListener {
            finish()
        }

        var position = intent.getIntExtra("delete",999)

        binding.deleteitemButtonCheck.setOnClickListener {
            ProfileFragment.mUserViewModel.deletePureResult(dataList[position])
            finish()
        }
    }
}