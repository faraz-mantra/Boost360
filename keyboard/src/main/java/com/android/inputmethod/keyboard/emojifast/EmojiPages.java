package com.android.inputmethod.keyboard.emojifast;

/**
 * Created by sepehr on 2/2/17.
 */

import com.android.inputmethod.keyboard.emoji.models.Cars;
import com.android.inputmethod.keyboard.emoji.models.Electr;
import com.android.inputmethod.keyboard.emoji.models.Emojicon;
import com.android.inputmethod.keyboard.emoji.models.Food;
import com.android.inputmethod.keyboard.emoji.models.Nature;
import com.android.inputmethod.keyboard.emoji.models.People;
import com.android.inputmethod.keyboard.emoji.models.Sport;
import com.android.inputmethod.keyboard.emoji.models.Symbols;

import java.util.Arrays;
import java.util.List;

import io.separ.neural.inputmethod.indic.R;

public class EmojiPages {
    public static final List<EmojiPageModel> PAGES = Arrays.<EmojiPageModel>asList(
            new StaticEmojiPageModel(R.attr.iconEmojiCategory5Tab, People.DATA, "emojis/emoji_symbols.png"),
            new StaticEmojiPageModel(R.attr.iconEmojiCategory1Tab, Food.DATA, "emojis/emoji_faces.png"),
            new StaticEmojiPageModel(R.attr.iconEmojiCategory2Tab, Nature.DATA, "emojis/emoji_objects.png"),
            new StaticEmojiPageModel(R.attr.iconEmojiCategory3Tab, Symbols.DATA, "emojis/emoji_nature.png"),
            new StaticEmojiPageModel(R.attr.iconEmojiCategory5Tab, Cars.DATA, "emojis/emoji_symbols.png"));
}
