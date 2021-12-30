package org.jabref.gui.entryeditor.fileannotationtab;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;

import java.util.*;

public class AdditionalFields {

    private Map<String, Set<Field>> additionalRequiredFields;
    private Map<String, Set<Field>> additionalOptionalFields;

    private static Field[] REQUIRED_ARTICLE = {1,2,3};
    private static Field[] OPTIONAL_ARTICLE = {1,2,3};
    private static Field[] REQUIRED_BOOK = {1,2,3};
    private static Field[] OPTIONAL_BOOK = {1,2,3};


    private void AdditionalFields() {

        additionalRequiredFields = new HashMap<>();
        additionalOptionalFields = new HashMap<>();

        for(StandardEntryType entryType: StandardEntryType.values()) {
            additionalRequiredFields.put(entryType.getDisplayName(), new LinkedHashSet<Field>());
            additionalOptionalFields.put(entryType.getDisplayName(), new LinkedHashSet<Field>());

            additionalRequiredFields.get(entryType.getDisplayName()).addAll(Arrays.asList(ARTICLE));
            additionalOptionalFields.get(entryType.getDisplayName()).addAll(Arrays.asList(ARTICLE));

        }

    }

    public void getAdditionalFields(StandardEntryType entryType) {
    return;
}
}
