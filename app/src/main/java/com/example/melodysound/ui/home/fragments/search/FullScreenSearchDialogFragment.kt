package com.example.melodysound.ui.home.fragments.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.melodysound.R
import com.example.melodysound.databinding.FragmentFullScreenSearchDialogBinding

class FullScreenSearchDialogFragment : DialogFragment() {
    private var _binding: FragmentFullScreenSearchDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullScreenSearchDialogBinding.inflate(inflater, container, false)
        if (binding.edtSearchDialog.length() > 0) {
            binding.previousSearchContent.visibility = View.GONE
        }else{
            binding.previousSearchContent.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            dismiss()
        }
        binding.btnClearText.setOnClickListener {
            binding.edtSearchDialog.setText("")
        }

        binding.edtSearchDialog.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                if (s.isNullOrEmpty()) {
                    binding.btnClearText.visibility = View.GONE
                    binding.previousSearchContent.visibility = View.VISIBLE
                } else {
                    binding.previousSearchContent.visibility = View.GONE
                    binding.btnClearText.visibility = View.VISIBLE
                }
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }
        }
        )

        binding.edtSearchDialog.requestFocus()

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding.edtSearchDialog.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                Toast.makeText(context, "Tìm kiếm: $query", Toast.LENGTH_SHORT).show()
                // TODO: Thực hiện logic tìm kiếm với 'query'
                dismiss() // Đóng dialog sau khi tìm kiếm (tùy chọn)
                true
            }
            false
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.DKGRAY)) // Đảm bảo background là màu trắng
            // Bạn có thể tùy chỉnh thêm như ẩn thanh trạng thái (status bar) nếu muốn
            // addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}