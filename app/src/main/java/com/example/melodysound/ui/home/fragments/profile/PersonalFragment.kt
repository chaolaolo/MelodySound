package com.example.melodysound.ui.home.fragments.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.melodysound.databinding.FragmentPersonalBinding
import com.example.melodysound.ui.auth.AuthActivity
import com.example.melodysound.ui.common.AuthTokenManager

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Đăng xuất")
                .setMessage("Bạn sẽ không thể nghe nhạc khi đăng xuất ,bạn có chắc chắn đăng xuất?")
                .setPositiveButton("Đăng xuất") { dialog, which ->
                    AuthTokenManager.clearTokens(requireActivity())
                    // Chuyển đến màn hình đăng nhập
                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Hủy") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}