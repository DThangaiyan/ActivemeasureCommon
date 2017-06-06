package net.ahm.careengine.displayable.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.ahm.careengine.ruleeditor.SelectionItem;
import net.ahm.careengine.ruleeditor.provider.dao.AbstractProviderElementDAO;
import net.ahm.rulesapp.displayables.type.SingleSelection;

import org.springframework.jdbc.core.RowMapper;

public class SingleSelectionProviderDAO extends
        AbstractProviderElementDAO<SingleSelection> {

    static final RowMapper<SingleSelection> MAPPER            = new SingleSelectionRowMapper();
    static final RowMapper<SelectionItem>   DELIGATING_MAPPER = SELECTION_ITEM_ROW_MAPPER;

    public SingleSelectionProviderDAO() {
        super(MAPPER);
    }

    private static class SingleSelectionRowMapper implements
            RowMapper<SingleSelection> {

        @Override
        public SingleSelection mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            SelectionItem item = DELIGATING_MAPPER.mapRow(rs, rowNum);
            return new SingleSelection(item.getValue(),
                    item.getDisplayedValueWithValueAppended());
        }

    }
}
