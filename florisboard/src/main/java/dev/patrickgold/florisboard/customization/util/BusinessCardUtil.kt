package dev.patrickgold.florisboard.customization.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.framework.BaseApplication
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.*
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import dev.patrickgold.florisboard.customization.model.response.DigitalCardDataKeyboard

object BusinessCardUtil {

    fun bitmapOfBusinessCard0(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardOneBinding>(LayoutInflater.from(BaseApplication.instance),
        R.layout.item_visiting_card_one,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }


    fun bitmapOfBusinessCard1(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardTwoBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_two,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard2(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardThreeBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_three,null,false)
        binding.businessName.text = cardData?.businessName
        binding.number.text = cardData?.number
        cardData?.cardIcon?.let {
            binding.imgLogo1.setImageResource(it)
            binding.imgLogo2.setImageResource(it)
        }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.viewTop.gone()
            binding.channels.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.viewTop.visible()
            binding.channels.visible()
        }

        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard3(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardFourBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_four,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard4(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardFiveBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_five,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard5(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardSixBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_six,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard6(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardSevenBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_seven,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        ChannelAccessStatusResponse.visibleChannels(binding.itemChannelsGroup.containerChannels)
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard7(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardEightBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_eight,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard8(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardNineBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_eight,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    fun bitmapOfBusinessCard9(data: DigitalCardDataKeyboard?): Bitmap? {
        val cardData = data?.cardData
        val binding= DataBindingUtil.inflate<ItemVisitingCardTenBinding>(LayoutInflater.from(BaseApplication.instance),
            R.layout.item_visiting_card_ten,null,false)
        binding.businessName.text =cardData?.businessName
        binding.number.text = cardData?.number
        cardData?.cardIcon?.let { binding.imgLogo.setImageResource(it) }
        if (cardData?.businessLogo.isNullOrEmpty().not()) {
            binding.profileView.visible()
            binding.imgLogo.gone()
            binding.imgBusinessLogo.context.glideLoad(binding.imgBusinessLogo, cardData?.businessLogo!!)
        } else {
            binding.profileView.gone()
            binding.imgLogo.visible()
        }
        binding.email.text = fromHtml("<u>${cardData?.email}</u>")
        binding.website.text = fromHtml("<u>${cardData?.website}</u>")
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val view = binding.root
        view.measure(spec,spec)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //background image
        val bgDrawable = view.background

        //background image is selected
        if (bgDrawable != null){
            bgDrawable.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }



}