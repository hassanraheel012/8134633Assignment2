package com.s8134633.coursenest.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.s8134633.coursenest.R
import com.s8134633.coursenest.model.Course
import com.s8134633.coursenest.ui.common.WindowInsetsHelper
import com.s8134633.coursenest.util.CourseMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var courseAdapter: CourseAdapter
    private lateinit var recyclerCourses: RecyclerView
    private lateinit var textCourseCount: TextView
    private lateinit var layoutStatus: View
    private lateinit var progressDashboard: ProgressBar
    private lateinit var imageStatus: ImageView
    private lateinit var textStatus: TextView
    private lateinit var buttonRetry: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerCourses = view.findViewById(R.id.recyclerCourses)
        textCourseCount = view.findViewById(R.id.textCourseCount)
        layoutStatus = view.findViewById(R.id.layoutStatus)
        progressDashboard = view.findViewById(R.id.progressDashboard)
        imageStatus = view.findViewById(R.id.imageStatus)
        textStatus = view.findViewById(R.id.textStatus)
        buttonRetry = view.findViewById(R.id.buttonRetry)

        WindowInsetsHelper.applyStatusBarPadding(view.findViewById(R.id.layoutHeader))

        view.findViewById<TextView>(R.id.textStudent).text =
            getString(R.string.dashboard_student, viewModel.studentId)
        view.findViewById<TextView>(R.id.textTopic).text =
            CourseMapper.formatLabel(viewModel.topic)

        courseAdapter = CourseAdapter(navigationFunction = ::openDetails)
        recyclerCourses.adapter = courseAdapter

        buttonRetry.setOnClickListener { viewModel.loadCourses() }
        view.findViewById<View>(R.id.buttonLogout).setOnClickListener { viewModel.logout() }

        observeUiState()
    }

    override fun onResume() {
        super.onResume()
        // The header is dark, so the status bar icons need to be light here
        WindowInsetsHelper.setDarkStatusBarIcons(requireActivity(), useDarkIcons = false)
    }

    override fun onPause() {
        super.onPause()
        WindowInsetsHelper.setDarkStatusBarIcons(requireActivity(), useDarkIcons = true)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: DashboardUiState) {
        progressDashboard.isVisible = state == DashboardUiState.Loading
        recyclerCourses.isVisible = state is DashboardUiState.Success
        layoutStatus.isVisible = state !is DashboardUiState.Success
        buttonRetry.isVisible = state is DashboardUiState.Error
        imageStatus.isVisible = state == DashboardUiState.Empty || state is DashboardUiState.Error
        // Invisible (not gone) so the header doesn't jump when the count appears
        textCourseCount.isInvisible = state !is DashboardUiState.Success

        when (state) {
            DashboardUiState.Loading -> showStatus(getString(R.string.dashboard_loading))
            DashboardUiState.Empty -> {
                imageStatus.setImageResource(R.drawable.ic_school)
                showStatus(getString(R.string.dashboard_empty))
            }
            is DashboardUiState.Error -> {
                imageStatus.setImageResource(R.drawable.ic_cloud_off)
                showStatus(getString(state.messageRes))
            }
            is DashboardUiState.Success -> {
                textCourseCount.text = resources.getQuantityString(
                    R.plurals.dashboard_course_count, state.courses.size, state.courses.size
                )
                courseAdapter.setData(state.courses)
            }
            DashboardUiState.SessionExpired -> findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToLoginFragment()
            )
        }
    }

    private fun showStatus(message: String) {
        textStatus.text = message
        textStatus.isVisible = true
    }

    private fun openDetails(course: Course) {
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToDetailsFragment(course)
        )
    }
}
