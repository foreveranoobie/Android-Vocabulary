package com.storozhuk.learningvocabulary.ui.language.adapter

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
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.dto.ui.LanguageDto
import com.storozhuk.learningvocabulary.ui.helper.UiHelper
import com.storozhuk.learningvocabulary.ui.helper.UiHelper.Companion.showToast

class LanguagesCustomAdapter(
    private val dataSet: MutableList<String>,
    private val languagesRepository: LanguagesRepository,
    private val window: Window
) :
    RecyclerView.Adapter<LanguagesCustomAdapter.ViewHolder>() {
    private lateinit var editLanguagePopupView: View
    private lateinit var editLanguagePopupWindow: PopupWindow

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.text_item)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        if (position == 0) {
            viewHolder.itemView.setPadding(0, 0, 0, 2);
        } else if (position == dataSet.size - 1) {
            viewHolder.itemView.setPadding(0, 2, 0, 0);
        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
        viewHolder.textView.setOnClickListener {
            if (!this::editLanguagePopupView.isInitialized) {
                initEditLanguagePopup(viewHolder)
            }
            showEditLanguagePopup(
                viewHolder,
                dataSet[position], position
            )
        }
        //}
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun showEditLanguagePopup(
        viewHolder: ViewHolder,
        langValue: String,
        langPosition: Int
    ): Boolean {
        editLanguagePopupWindow.showAtLocation(viewHolder.itemView, Gravity.CENTER, 0, 0)
        UiHelper.dimBackground(window, 0.5f) // Add background dim

        val languageNameTxt = editLanguagePopupView.findViewById<TextView>(R.id.language_name_txt)
        editLanguagePopupView.findViewById<EditText>(R.id.edit_language_input).setText(langValue)
        languageNameTxt.text = langValue

        editLanguagePopupView.findViewById<ImageButton>(R.id.close_edit_lang_popup)
            .setOnClickListener {
                editLanguagePopupWindow.dismiss()
            }

        editLanguagePopupView.findViewById<Button>(R.id.edit_lang_btn).setOnClickListener {
            val languageDto = updateLanguage(editLanguagePopupView)
            if (languageDto != null) {
                dataSet[langPosition] = languageDto.language
                languageNameTxt.text = languageDto.language
                notifyItemChanged(langPosition)
            }
            editLanguagePopupWindow.dismiss()
        }

        editLanguagePopupView.findViewById<Button>(R.id.remove_lang_btn).setOnClickListener {
            if (deleteLanguage(dataSet[langPosition]) == 1) {
                dataSet.removeAt(langPosition)
                notifyItemRemoved(langPosition)
                notifyItemRangeChanged(langPosition, dataSet.size)
                editLanguagePopupWindow.dismiss()
            }
        }

        return true
    }

    private fun deleteLanguage(languageValue: String): Int {
        val languageId = languagesRepository.fetchId(languageValue)
        return languagesRepository.delete(languageId)
    }

    private fun updateLanguage(layoutView: View): LanguageDto? {
        val languageValue =
            layoutView.findViewById<EditText>(R.id.edit_language_input).text.toString()
        val languageId =
            languagesRepository.fetchId(layoutView.findViewById<TextView>(R.id.language_name_txt).text.toString())
        val languageDto = LanguageDto(languageId, languageValue)
        if (languageValue.trim().isNotEmpty()) {
            if (languagesRepository.update(languageDto) == 1) {
                return languageDto
            }
        } else {
            showToast(window.context, "Language should not be empty")
        }
        return null
    }

    private fun initEditLanguagePopup(viewHolder: ViewHolder) {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)
        editLanguagePopupView = inflater.inflate(R.layout.edit_language_popup, null)

        editLanguagePopupWindow = PopupWindow(
            editLanguagePopupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        editLanguagePopupWindow.setOnDismissListener {
            UiHelper.dimBackground(window, 0f) // Remove dim when dismissed
        }

        editLanguagePopupView.findViewById<ImageButton>(R.id.close_edit_lang_popup)
            .setOnClickListener {
                editLanguagePopupWindow.dismiss()
            }
    }
}