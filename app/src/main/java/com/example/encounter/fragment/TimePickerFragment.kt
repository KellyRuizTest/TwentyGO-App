package com.example.encounter.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.sax.EndElementListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.encounter.PostActivity
import com.example.encounter.R
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TimePickerFragment : DialogFragment() {

    private var listener : DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(this.activity!!, listener, year, month, day)

    }

    companion object {

        fun newInstance(listener: DatePickerDialog.OnDateSetListener): TimePickerFragment {
            val fragment = TimePickerFragment()
            fragment.listener = listener
            return fragment
        }
    }

    fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }
}