package com.sgwares.android.validators;

import android.graphics.Color;

public class UserValidator {

    /**
     * Check the name is valid
     * @param name the user name to vaildate
     * @return true if valid, false if it is not
     */
    public static boolean isNameValid(String name) {
        if ((name != null) && name.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check the colour is valid
     * @param colour the colour to vaildate
     * @return true if valid, false if it is not
     */
    public static boolean isColourValid(String colour) {
        try {
            Color.parseColor(colour);
            return true;
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }

}
