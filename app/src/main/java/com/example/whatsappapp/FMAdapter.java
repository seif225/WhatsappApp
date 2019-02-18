package com.example.whatsappapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FMAdapter extends FragmentPagerAdapter {
    public FMAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
               return "chats";

            case 1: return "groups";

            case 2: return  "contacts";

            case 3: return  "requests";


            default: return null;
        }

    }

    @Override
    public Fragment getItem(int i) {

        switch(i) {
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
               return chatsFragment;

            case 1:GroupsFragment groupsFragment= new GroupsFragment();
                return groupsFragment;

            case 2: ContactsFragment contactsFragment=new ContactsFragment();
                    return contactsFragment;

            case 3: RequestFragment requestFragment= new RequestFragment();
                    return requestFragment;

            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }


}
