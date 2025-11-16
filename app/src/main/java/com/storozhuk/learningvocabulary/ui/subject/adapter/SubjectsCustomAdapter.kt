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
import com.storozhuk.learningvocabulary.ui.language.adapter.LanguagesCustomAdapter

class SubjectsCustomAdapter(
    private val dataSet: MutableList<SubjectDto>,
    private val subjectsRepository: SubjectsRepository,
    private val window: Window
) :
    RecyclerView.Adapter<LanguagesCustomAdapter.ViewHolder>()  {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.text_item)
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LanguagesCustomAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item, viewGroup, false)

        return LanguagesCustomAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(viewHolder: LanguagesCustomAdapter.ViewHolder, position: Int) {
        if(position == 0){
            viewHolder.itemView.setPadding(0, 0, 0, 2);
        } else if(position == dataSet.size - 1){
            viewHolder.itemView.setPadding(0, 2, 0, 0);
        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].subject
        viewHolder.textView.setOnClickListener {
            showEditSubjectPopup(
                viewHolder,
                dataSet[position],
                position
            )
        }
    }

    private fun showEditSubjectPopup(
        viewHolder: LanguagesCustomAdapter.ViewHolder,
        subjectDto: SubjectDto,
        subjectPosition: Int
    ): Boolean {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)
        val editSubjectPopup = inflater.inflate(R.layout.edit_subject_popup, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(editSubjectPopup, width, height, focusable)
        popupWindow.showAtLocation(viewHolder.itemView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            UiHelper.dimBackground(window, 0f) // Remove dim when dismissed
        }

        UiHelper.dimBackground(window, 0.5f) // Add background dim

        val subjectNameTxt = editSubjectPopup.findViewById<TextView>(R.id.subject_name_txt)
        editSubjectPopup.findViewById<EditText>(R.id.edit_subject_input).setText(subjectDto.subject)
        subjectNameTxt.text = subjectDto.subject

        editSubjectPopup.findViewById<ImageButton>(R.id.close_edit_subject_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        editSubjectPopup.findViewById<Button>(R.id.edit_subject_btn).setOnClickListener {

            val newSubjectDto = updateSubject(editSubjectPopup, subjectDto)
            if (newSubjectDto != null) {
                dataSet[subjectPosition] = newSubjectDto
                subjectNameTxt.text = newSubjectDto.subject
                notifyItemChanged(subjectPosition)
            }
            popupWindow.dismiss()
        }

        editSubjectPopup.findViewById<Button>(R.id.remove_subject_btn).setOnClickListener {
            if (deleteLanguage(dataSet[subjectPosition].id!!) == 1) {
                dataSet.removeAt(subjectPosition)
                notifyItemRemoved(subjectPosition)
                notifyItemRangeChanged(subjectPosition, dataSet.size)
                popupWindow.dismiss()
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
        val updateSubjectDto = SubjectDto(subject.id, subjectValue, subject.languageId)
        if (subjectsRepository.update(updateSubjectDto) == 1) {
            return updateSubjectDto
        }
        return null
    }
}