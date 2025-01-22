package com.example.promptsharepro22.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class AvaterUtil {
    public static Drawable getInitialsDrawable(String firstname, String lastname) {
        if (firstname == null || firstname.isEmpty()) {
            firstname = "Tommy";
        }
        if (lastname == null || lastname.isEmpty()) {
            lastname = "Trojan";
        }

        // Extract initials from name
        StringBuilder initialsBuilder = new StringBuilder();
        for (String part : new String[]{firstname, lastname}) {
            if (part != null && !part.isEmpty()) { // Check for null or empty parts
                initialsBuilder.append(part.charAt(0));
            }
        }
        String initials = initialsBuilder.toString().toUpperCase();

        float textSize = 40f;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getRandomColor());  // Set random background color
        canvas.drawText(initials, 50f, 60f, paint);

        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    private static int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
