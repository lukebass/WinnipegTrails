package ca.winnipegtrails.winnipegtrails;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView
{
    public CustomTextView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/pressStart2P.ttf");
        this.setTypeface(tf);
    }
}
