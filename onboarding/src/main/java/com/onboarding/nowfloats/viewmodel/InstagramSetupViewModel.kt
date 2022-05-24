package com.onboarding.nowfloats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.utils.getResponse
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse
import com.onboarding.nowfloats.model.supportVideo.FeaturevideoItem
import com.onboarding.nowfloats.model.supportVideo.Videourl
import com.onboarding.nowfloats.rest.repositories.DeveloperBoostKitDevRepository
import com.onboarding.nowfloats.ui.registration.instagram.IGIntStepsFragment

class InstagramSetupViewModel : BaseViewModel() {

    private val supportVideosmLd = MutableLiveData<BaseResponse>()
    val supportVideosLd : LiveData<BaseResponse> get()= supportVideosmLd
    var igGuideVideos:List<FeaturevideoItem?>? = null

    enum class InstagramVideoIdentifier(val value:String){
        ConnectYourFacebookPageRender("feature.ConnectYourFacebookPageRender_instagram"),
        TwoFactorAuthentication("feature.TwoFactorAuthentication_instagram"),
        SwitchtoProfessinalAccount("feature.SwitchtoProfessinalAccount_instagram"),
        CreateInstagrampage("feature.CreateInstagrampage__instagram"),
    }

    fun getVideo(step: IGIntStepsFragment.Step): FeaturevideoItem? {
       return when(step){
            IGIntStepsFragment.Step.STEP1->{
                igGuideVideos?.find { it?.helpsectionidentifier==InstagramVideoIdentifier.CreateInstagrampage.value }
            }
           IGIntStepsFragment.Step.STEP2->{
               igGuideVideos?.find { it?.helpsectionidentifier==InstagramVideoIdentifier.SwitchtoProfessinalAccount.value }
           }
           IGIntStepsFragment.Step.STEP3->{
               igGuideVideos?.find { it?.helpsectionidentifier==InstagramVideoIdentifier.TwoFactorAuthentication.value }
           }
           IGIntStepsFragment.Step.STEP4->{
               igGuideVideos?.find { it?.helpsectionidentifier==InstagramVideoIdentifier.ConnectYourFacebookPageRender.value }
           }
           else->{
               null
           }
        }
    }

    fun getSupportVideos() {
         DeveloperBoostKitDevRepository.getSupportVideos().getResponse{result->
             supportVideosmLd.postValue(result)
         }
    }

    fun setGuideVideos(response: FeatureSupportVideoResponse) {
        igGuideVideos =response.data?.firstOrNull()?.featurevideo?.filter {videoitem->
            when(videoitem?.helpsectionidentifier){
                InstagramVideoIdentifier.CreateInstagrampage.value,
                InstagramVideoIdentifier.SwitchtoProfessinalAccount.value,
                InstagramVideoIdentifier.TwoFactorAuthentication.value,
                InstagramVideoIdentifier.ConnectYourFacebookPageRender.value->true
                else->false
            }
        }
    }
}