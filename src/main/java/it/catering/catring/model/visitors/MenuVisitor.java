package it.catering.catring.model.visitors;

import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.entities.SezioneMenu;
import it.catering.catring.model.entities.VoceMenu;

public interface MenuVisitor {
    void visitMenu(Menu menu);
    void visitSezione(SezioneMenu sezione);
    void visitVoce(VoceMenu voce);
}
