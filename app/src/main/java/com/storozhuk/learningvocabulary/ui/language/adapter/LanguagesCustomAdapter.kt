package com.storozhuk.learningvocabulary.ui.language.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import com.storozhuk.learningvocabulary.R
import com.storozhuk.learningvocabulary.db.repo.LanguagesRepository
import com.storozhuk.learningvocabulary.dto.LanguageDto
import com.storozhuk.learningvocabulary.ui.language.AddLanguageFragment

class LanguagesCustomAdapter(
    private val dataSet: MutableList<String>,
    private val languagesRepository: LanguagesRepository
) :
    RecyclerView.Adapter<LanguagesCustomAdapter.ViewHolder>() {

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

        //Ignore edit for the very first position because it encompasses all languages
        //if (position > 0) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
        viewHolder.textView.setOnLongClickListener {
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
        val inflater = LayoutInflater.from(viewHolder.itemView.context)
        val editLanguagePopup = inflater.inflate(R.layout.edit_language_popup, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(editLanguagePopup, width, height, focusable)
        popupWindow.showAtLocation(viewHolder.itemView, Gravity.CENTER, 0, 0)

        val languageNameTxt = editLanguagePopup.findViewById<TextView>(R.id.language_name_txt)
        editLanguagePopup.findViewById<EditText>(R.id.edit_language_input).setText(langValue)
        languageNameTxt.text = langValue

        editLanguagePopup.findViewById<ImageButton>(R.id.close_edit_lang_popup).setOnClickListener {
            popupWindow.dismiss()
        }

        editLanguagePopup.findViewById<Button>(R.id.edit_lang_btn).setOnClickListener {
            val languageDto = updateLanguage(editLanguagePopup)
            if (languageDto != null) {
                dataSet[langPosition] = languageDto.language
                languageNameTxt.text = languageDto.language
                notifyItemChanged(langPosition)
            }
        }

        editLanguagePopup.findViewById<Button>(R.id.remove_lang_btn).setOnClickListener {
            if (deleteLanguage(dataSet[langPosition]) == 1) {
                dataSet.removeAt(langPosition)
                notifyItemRemoved(langPosition)
                notifyItemRangeChanged(langPosition, dataSet.size)
                popupWindow.dismiss()
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
        if (languagesRepository.update(languageDto) == 1) {
            return languageDto
        }
        return null
    }
}