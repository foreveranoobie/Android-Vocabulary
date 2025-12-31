package com.storozhuk.learningvocabulary.ui.subject.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.db.repo.SubjectsRepository
import com.storozhuk.learningvocabulary.dto.data.SubjectDto
import com.storozhuk.learningvocabulary.ui.helper.UiHelper
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.showToast

class SubjectsCustomAdapter(
    private val dataSet: MutableList<SubjectDto>,
    private val subjectsRepository: SubjectsRepository,
    private val window: Window
) : RecyclerView.Adapter<SubjectsCustomAdapter.ViewHolder>() {
    private lateinit var editSubjectPopupView: View
    private lateinit var editSubjectPopupWindow: PopupWindow

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.text_item)
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): SubjectsCustomAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item, viewGroup, false)

        return SubjectsCustomAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == 0) {
            viewHolder.itemView.setPadding(0, 0, 0, 2);
        } else if (position == dataSet.size - 1) {
            viewHolder.itemView.setPadding(0, 2, 0, 0);
        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].subject
        viewHolder.textView.setOnClickListener {
            if (!this::editSubjectPopupView.isInitialized) {
                initEditSubjectPopup(viewHolder)
            }
            showEditSubjectPopup(
                viewHolder, dataSet[position], position
            )
        }
    }

    private fun showEditSubjectPopup(
        viewHolder: ViewHolder, subjectDto: SubjectDto, subjectPosition: Int
    ): Boolean {
        editSubjectPopupWindow.showAtLocation(viewHolder.itemView, Gravity.CENTER, 0, 0)

        UiHelper.dimBackground(window, 0.5f) // Add background dim

        val subjectNameTxt = editSubjectPopupView.findViewById<TextView>(R.id.subject_name_txt)
        editSubjectPopupView.findViewById<EditText>(R.id.edit_subject_input)
            .setText(subjectDto.subject)
        subjectNameTxt.text = subjectDto.subject

        editSubjectPopupView.findViewById<ImageButton>(R.id.close_edit_subject_popup)
            .setOnClickListener {
                editSubjectPopupWindow.dismiss()
            }

        editSubjectPopupView.findViewById<Button>(R.id.edit_subject_btn).setOnClickListener {

            val newSubjectDto = updateSubject(editSubjectPopupView, subjectDto)
            if (newSubjectDto != null) {
                dataSet[subjectPosition] = newSubjectDto
                subjectNameTxt.text = newSubjectDto.subject
                notifyItemChanged(subjectPosition)
            }
            editSubjectPopupWindow.dismiss()
        }

        editSubjectPopupView.findViewById<Button>(R.id.remove_subject_btn).setOnClickListener {
            if (deleteLanguage(dataSet[subjectPosition].id!!) == 1) {
                dataSet.removeAt(subjectPosition)
                notifyItemRemoved(subjectPosition)
                notifyItemRangeChanged(subjectPosition, dataSet.size)
                editSubjectPopupWindow.dismiss()
            }
        }
        return true
    }

    private fun deleteLanguage(subjectId: Int): Int {
        return subjectsRepository.delete(subjectId)
    }

    private fun updateSubject(layoutView: View, subject: SubjectDto): SubjectDto? {
        val subjectValue =
            layoutView.findViewById<EditText>(R.id.edit_subject_input).text.toString()
        if (subjectValue.trim().isNotEmpty()) {

            if (subjectsRepository.fetchForSubjectAndLanguageId(
                    subjectValue,
                    subject.languageId
                ).count == 0
            ) {

                val updateSubjectDto = SubjectDto(subject.id, subjectValue, subject.languageId)
                if (subjectsRepository.update(updateSubjectDto) == 1) {
                    return updateSubjectDto
                }
            } else {
                showToast(window.context, "Subject already exists for this language")
            }
        } else {
            showToast(window.context, "Subject name is empty")
        }
        return null
    }

    private fun initEditSubjectPopup(viewHolder: ViewHolder) {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)
        editSubjectPopupView = inflater.inflate(R.layout.edit_subject_popup, null)

        editSubjectPopupWindow = PopupWindow(
            editSubjectPopupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        editSubjectPopupWindow.setOnDismissListener {
            UiHelper.dimBackground(window, 0f) // Remove dim when dismissed
        }

        editSubjectPopupView.findViewById<ImageButton>(R.id.close_edit_subject_popup)
            .setOnClickListener {
                editSubjectPopupWindow.dismiss()
            }
    }
}