package com.jacoboaks.wandermobile.util;

import com.jacoboaks.wandermobile.R;
import com.jacoboaks.wandermobile.graphics.Font;

/**
 * Encompasses various global values.
 */
public class Global {

    //Colors
    public static final Color white = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color black = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    //Other Data
    public static Font defaultFont = new Font(R.drawable.font_default, R.raw.fontcuttoffs_default, 10, 10, ' ');
}
