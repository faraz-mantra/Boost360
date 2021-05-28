package com.boost.presignup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.boost.presignup.R
import kotlinx.android.synthetic.main.pop_up_web_view_fragment.*

class PopUpWebViewFragment : DialogFragment() {

    lateinit var root: View

    var link: String? = null
    var title: String? = "Web Link"

    companion object {
        fun newInstance() = PopUpWebViewFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.pop_up_web_view_fragment, container, false)
        link = requireArguments().getString("link")?:""
        title = requireArguments().getString("title")
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments !=null && requireArguments().containsKey("title")){
            browser_title.text = requireArguments().getString("title")
        }

        if (link != null) {
            webview.loadUrl(link!!)
        } else {
            Toast.makeText(requireContext(), getString(R.string.link_is_empty), Toast.LENGTH_LONG).show()
            dismiss()
        }

        web_addons_back.setOnClickListener {
            dismiss()
        }
    }

}