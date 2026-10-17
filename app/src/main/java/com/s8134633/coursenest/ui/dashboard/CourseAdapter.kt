package com.s8134633.coursenest.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.s8134633.coursenest.R
import com.s8134633.coursenest.model.Course
import com.s8134633.coursenest.util.CourseMapper

/**
 * Shows a summary card for each course (everything except the description).
 *
 * @param navigationFunction called with the course the user tapped.
 */
class CourseAdapter(
    private val navigationFunction: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private val courses = mutableListOf<Course>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position], navigationFunction)
    }

    override fun getItemCount(): Int = courses.size

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textCode: TextView = itemView.findViewById(R.id.textCourseCode)
        private val textTitle: TextView = itemView.findViewById(R.id.textCourseTitle)
        private val textSummary: TextView = itemView.findViewById(R.id.textCourseSummary)

        fun bind(course: Course, navigationFunction: (Course) -> Unit) {
            // The code badge only appears when the API sent a code for this course
            textCode.text = course.code
            textCode.isVisible = course.code != null

            textTitle.text = course.title

            val summary = CourseMapper.cardSummary(course)
            textSummary.text = summary
            textSummary.isVisible = summary.isNotEmpty()

            itemView.setOnClickListener { navigationFunction(course) }
        }
    }
}
