package com.dashboard.controller.ui.profile.sheet

import android.os.Bundle
import androidx.fragment.app.Fragment


fun Fragment.startProfileEditEmailSheet(oldemail:String?){
    val dialog = EditChangeEmailSheet().apply {
        arguments = Bundle().apply {  putString(EditChangeEmailSheet.IK_EMAIL,oldemail)}
    }
    dialog.show(parentFragmentManager, EditChangeEmailSheet::javaClass.name)
}

fun Fragment.startProfileEditMobSheet(oldphone:String?){
    val dialog = EditChangeMobileNumberSheet().apply {
        arguments = Bundle().apply {  putString(EditChangeMobileNumberSheet.IK_MOB,oldphone)}
    }
    dialog.show(parentFragmentManager, EditChangeMobileNumberSheet::javaClass.name)
}

fun Fragment.startVerifyMobEmailSheet(type:String,emailOrMob:String?){
    val dialog = VerifyOtpEmailMobileSheet().apply {
        arguments = Bundle().apply {
            putString(VerifyOtpEmailMobileSheet.IK_TYPE,type)
            putString(VerifyOtpEmailMobileSheet.IK_EMAIL_OR_MOB,emailOrMob)

        }
    }
    dialog.show(parentFragmentManager,VerifyOtpEmailMobileSheet::javaClass.name)
}

fun Fragment.startVerifiedMobEmailSheet(type:String,emailOrMob:String?){
    val dialog = VerifiedEmailMobileSheet().apply {
        arguments = Bundle().apply {
            putString(VerifiedEmailMobileSheet.IK_TYPE,type)
            putString(VerifiedEmailMobileSheet.IK_EMAIL_OR_MOB,emailOrMob)

        }
    }
    dialog.show(parentFragmentManager,VerifiedEmailMobileSheet::javaClass.name)
}

fun Fragment.startProfileEditWhatsappSheet(oldNumber:String?){
    val dialog = EditChangeWhatsappNumberSheet().apply {
        arguments = Bundle().apply {  putString(EditChangeWhatsappNumberSheet.IK_MOB,oldNumber)}
    }
    dialog.show(parentFragmentManager, EditChangeWhatsappNumberSheet::javaClass.name)
}

fun Fragment.startLogoutSheet(){
    val dialog = LogoutSheet().apply {
    }
    dialog.show(parentFragmentManager, LogoutSheet::javaClass.name)
}