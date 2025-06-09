package it.catering.catring.model.observers;

import it.catering.catring.model.entities.Menu;

public interface MenuObserver {
    void onMenuCreated(Menu menu);
    void onMenuUpdated(Menu menu);
    void onMenuDeleted(Menu menu);
}