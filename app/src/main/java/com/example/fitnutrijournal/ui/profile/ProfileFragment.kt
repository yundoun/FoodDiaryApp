package com.example.fitnutrijournal.ui.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fitnutrijournal.databinding.FragmentProfileBinding
import com.example.fitnutrijournal.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            viewModel = profileViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            profileLayout.setOnClickListener {
                Snackbar.make(view, "개발 중 입니다.", Snackbar.LENGTH_SHORT).show()
            }
            alarmLayout.setOnClickListener {
                Snackbar.make(view, "개발 중 입니다.", Snackbar.LENGTH_SHORT).show()
            }
            myWeightLayout.setOnClickListener {
                Snackbar.make(view, "개발 중 입니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.contactLayout.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", "ehdns1133@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body Here")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}