package net.ahm.careengine.displayable;

import java.util.List;
import java.util.Set;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.displayable.dao.SingleSelectionProviderDAO;
import net.ahm.careengine.function.Function;
import net.ahm.rulesapp.displayables.type.SingleEnumeratedValueSetBuilder;
import net.ahm.rulesapp.displayables.type.SingleSelection;

public interface CommonSingleEnumeratedValueSetBuilderIF
        extends
        SingleEnumeratedValueSetBuilder<CareEngineEnumeratedValueSetBuilderContext>,
        Function<SingleSelectionProviderDAO, List<SingleSelection>> {

    public static final Set<Character> WORD_DELIMITER_SET = CECollectionsUtil
                                                                  .unmodifiableSet('_');
}
