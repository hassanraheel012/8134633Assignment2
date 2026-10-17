package com.s8134633.coursenest.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.s8134633.coursenest.R
import com.s8134633.coursenest.ui.common.WindowInsetsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var editStudentId: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var progressLogin: ProgressBar
    private lateinit var textMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editStudentId = view.findViewById(R.id.editStudentId)
        editPassword = view.findViewById(R.id.editPassword)
        buttonLogin = view.findViewById(R.id.buttonLogin)
        progressLogin = view.findViewById(R.id.progressLogin)
        textMessage = view.findViewById(R.id.textMessage)

        WindowInsetsHelper.applyStatusBarPadding(view.findViewById(R.id.layoutLoginContent))

        buttonLogin.setOnClickListener { submitLogin() }
        editPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitLogin()
                true
            } else {
                false
            }
        }

        observeUiState()
    }

    override fun onResume() {
        super.onResume()
        WindowInsetsHelper.setDarkStatusBarIcons(requireActivity(), useDarkIcons = true)
    }

    private fun submitLogin() {
        viewModel.login(editStudentId.text.toString(), editPassword.text.toString())
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: LoginUiState) {
        val isLoading = state == LoginUiState.Loading
        // Kept enabled (just not clickable) so the button stays orange behind the spinner
        buttonLogin.isClickable = !isLoading
        // While loading, the spinner replaces the button's text and arrow
        buttonLogin.text = if (isLoading) "" else getString(R.string.login_button)
        buttonLogin.icon = if (isLoading) {
            null
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_forward)
        }
        progressLogin.isVisible = isLoading
        editStudentId.isEnabled = !isLoading
        editPassword.isEnabled = !isLoading

        when (state) {
            LoginUiState.Idle -> textMessage.isVisible = false
            LoginUiState.Loading -> showMessage(getString(R.string.login_loading), isError = false)
            is LoginUiState.Error -> showMessage(getString(state.messageRes), isError = true)
            LoginUiState.Success -> {
                viewModel.onNavigationHandled()
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToDashboardFragment()
                )
            }
        }
    }

    private fun showMessage(message: String, isError: Boolean) {
        val colour = if (isError) R.color.coursenest_error else R.color.coursenest_grey
        textMessage.setTextColor(ContextCompat.getColor(requireContext(), colour))
        textMessage.text = message
        textMessage.isVisible = true
    }
}
