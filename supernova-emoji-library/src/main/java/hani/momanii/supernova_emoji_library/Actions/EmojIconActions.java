/*
 * Copyright 2016 Hani Al Momani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hani.momanii.supernova_emoji_library.Actions;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import hani.momanii.supernova_emoji_library.R;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;


/**
 * @author Hani Al Momani (hani.momanii@gmail.com)
 */
public class EmojIconActions {

    private boolean useSystemEmoji = false;
    private EmojiconsPopup popup;
    private Context context;
    private int KeyBoardIcon = R.drawable.ic_action_keyboard;
    private int SmileyIcons = R.drawable.smiley;
    private KeyboardListener keyboardListener;


    /**
     * Constructor
     *
     * @param ctx The context of current activity
     */
    public EmojIconActions(Context ctx) {
        this.context = ctx;
        this.popup = new EmojiconsPopup(ctx, useSystemEmoji);
    }


    /**
     * Constructor
     *
     * @param ctx              The context of current activity.
     * @param iconPressedColor The color of icons on tab
     * @param tabsColor        The color of tabs background
     * @param backgroundColor  The color of emoji background
     */
    public EmojIconActions(Context ctx, String iconPressedColor, String tabsColor,
                           String backgroundColor) {
        this.context = ctx;
        this.popup = new EmojiconsPopup(ctx, useSystemEmoji, iconPressedColor,
                tabsColor, backgroundColor);
    }

    public void setIconsIds(int keyboardIcon, int smileyIcon) {
        this.KeyBoardIcon = keyboardIcon;
        this.SmileyIcons = smileyIcon;
    }


    private void refresh() {
        popup.updateUseSystemDefault(useSystemEmoji);
    }

    public void ShowEmojIcon() {
        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();


        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup
                .OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
                if (keyboardListener != null)
                    keyboardListener.onKeyboardOpen();
            }

            @Override
            public void onKeyboardClose() {
                if (keyboardListener != null)
                    keyboardListener.onKeyboardClose();
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojicon == null) {
                    return;
                }

                //Send the Emojicon via callback

                /*int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }*/
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup
                .OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                //Send the back kSpace key via Callback
            }
        });


    }


    public void closeEmojIcon() {
        if (popup != null && popup.isShowing())
            popup.dismiss();

    }

    public void setKeyboardListener(KeyboardListener listener) {
        this.keyboardListener = listener;
    }

    public interface KeyboardListener {
        void onKeyboardOpen();

        void onKeyboardClose();
    }

}
