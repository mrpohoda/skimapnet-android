package net.skimap.activities;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ListActivity extends FragmentActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list);
    }
}
