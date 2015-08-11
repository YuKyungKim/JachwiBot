package com.soma.albert.jachwibot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by wlsdn on 2015-08-11.
 */
public class Phone_Call
{
    public void Call(Context context, String number)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            context.startActivity(intent);
        }
        catch(ActivityNotFoundException e)
        {
            Log.d("Phone_Call", e.getMessage());
        }
    }
}
