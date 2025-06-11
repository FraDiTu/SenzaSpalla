package com.catring.observer;

import com.catring.model.Menu;

public interface MenuObserver {

    void onMenuCreated(Menu menu);

    void onMenuUpdated(Menu menu);

    void onMenuDeleted(Menu menu);
}