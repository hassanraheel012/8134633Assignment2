package com.s8134633.coursenest.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.s8134633.coursenest.R
import com.s8134633.coursenest.ui.common.WindowInsetsHelper
import com.s8134633.coursenest.util.CourseMapper

/** Shows all information about the course selected on the dashboard, including the description. */
class DetailsFragment : Fragment() {

    // The selected course, passed with safe-args (L4)
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val course = args.course

        WindowInsetsHelper.applyStatusBarPadding(view.findViewById(R.id.layoutTopBar))

        view.findViewById<View>(R.id.buttonBack).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<TextView>(R.id.textDetailsCode).apply {
            text = course.code
            isVisible = course.code != null
        }
        view.findViewById<TextView>(R.id.textDetailsTitle).text = course.title

        val rows = CourseMapper.toDetailRows(course)
        val detailRowAdapter = DetailRowAdapter()
        view.findViewById<RecyclerView>(R.id.recyclerDetails).adapter = detailRowAdapter
        detailRowAdapter.setData(rows)
        // Hide the information card if the course only has a title
        view.findViewById<View>(R.id.layoutInfoSection).isVisible = rows.isNotEmpty()

        view.findViewById<TextView>(R.id.textDescription).text = course.description
    }
}
