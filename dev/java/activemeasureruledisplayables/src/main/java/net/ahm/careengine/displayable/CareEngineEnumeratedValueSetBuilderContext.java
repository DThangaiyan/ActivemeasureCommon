package net.ahm.careengine.displayable;

import net.ahm.careengine.displayable.dao.SingleSelectionProviderDAO;
import net.ahm.rulesapp.displayables.interfaces.EnumeratedValueSetBuilderContext;

public class CareEngineEnumeratedValueSetBuilderContext implements
        EnumeratedValueSetBuilderContext {

    private SingleSelectionProviderDAO dao;

    public SingleSelectionProviderDAO getDao() {
        return dao;
    }

    public void setDao(SingleSelectionProviderDAO dao) {
        this.dao = dao;
    }
}
