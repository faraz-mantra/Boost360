package com.boost.presignup.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.boost.presignup.R
import com.boost.presignup.SignUpActivity
import com.boost.presignup.utils.AppConstants.Companion.GMBClientId
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.curve_popup_layout.view.*


class PopUpDialogFragment : DialogFragment() {

    lateinit var root: View
    val RC_SIGN_IN = 1
    val TAG = "PopUpDialogFragment"
    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.curve_popup_layout, container, false)

        //Login Spannable
        spannableString()
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        root.popup_layout.setOnClickListener {
            dialog!!.dismiss()
        }
        root.view.setOnClickListener {  }
        root.facebook_button.setOnClickListener {

        }
        root.google_button.setOnClickListener {
            googleSignIn()
        }
        root.email_button.setOnClickListener {

        }
        root.popup_login_text.setOnClickListener {

        }

        return root
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth.currentUser
                        AuthorizedUser(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        AuthorizedUser(null)
                    }

                    // ...
                }
    }

    fun spannableString(){
        val ss = SpannableString(getString(R.string.terms_of_use_and_privacy_policy))
        val termsOfUseClicked: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                // navigate to sign up fragment
                Toast.makeText(requireContext(),"Terms of process is clicked...",Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val privacyPolicyClicked: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                // navigate to sign up fragment
                Toast.makeText(requireContext(),"Privacy policy is clicked...",Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        //By creating Boost account, you agree to our Terms of use and Privacy Policy
        ss.setSpan(termsOfUseClicked, 44, 56, 0)
        ss.setSpan(privacyPolicyClicked, 61, ss.length, 0)
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
            44,
            56,
            0
        )
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
            61,
            ss.length,
            0
        )
        ss.setSpan(UnderlineSpan(),44,56, 0)
        ss.setSpan(UnderlineSpan(),61,ss.length, 0)
        root.popup_login_text.setText(ss)
        root.popup_login_text.setMovementMethod(LinkMovementMethod.getInstance())
        root.popup_login_text.setHighlightColor(resources.getColor(android.R.color.transparent))
    }

    fun AuthorizedUser(currentUser: FirebaseUser?) {
        var acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            val personName = acct.displayName.toString()
            val personGivenName = acct.givenName.toString()
            val personFamilyName = acct.familyName.toString()
            val personEmail = acct.email.toString()
            val personIdToken = acct.idToken.toString()
            val personPhoto = acct.photoUrl.toString()

            Log.d(TAG, "updateUI: photo = " + personPhoto);

            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.putExtra("url", personPhoto)
            intent.putExtra("email", personEmail)
            intent.putExtra("person_name", personName)
            intent.putExtra("personGivenName", personGivenName)
            intent.putExtra("personFamilyName", personFamilyName)
            intent.putExtra("personIdToken", personIdToken)
            startActivity(intent);
            mGoogleSignInClient.signOut()
            dialog!!.dismiss()
//            activity!!.finish();
        }
    }

}