package org.jabref.gui.entryeditor.additionalfields;

import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;

import java.util.HashMap;
import java.util.Map;

public class AdditionalOptionalFields {

    private Map<String, Field[]> additionalOptionalFields;
    private Field[] commonFields = {StandardField.LANGUAGE};

    private static final Field[] OPTIONAL_ARTICLE = {StandardField.FUNDED_BY};
    private static final Field[] OPTIONAL_BOOK = {};
    private static final Field[] OPTIONAL_BOOKLET = {};
    private static final Field[] OPTIONAL_COLLECTION= {};
    private static final Field[] OPTIONAL_CONFERENCE = {};
    private static final Field[] OPTIONAL_INBOOK = {};
    private static final Field[] OPTIONAL_INCOLLECTION = {};
    private static final Field[] OPTIONAL_INPROCEEDINGS = {};
    private static final Field[] OPTIONAL_MANUAL = {};
    private static final Field[] OPTIONAL_MASTERTHESIS= {};
    private static final Field[] OPTIONAL_MISC = {};
    private static final Field[] OPTIONAL_PHDTHESIS = {};
    private static final Field[] OPTIONAL_PROCEEDINGS= {};
    private static final Field[] OPTIONAL_TECHREPORT = {};
    private static final Field[] OPTIONAL_UNPUBLISHED = {};

    public AdditionalOptionalFields() {

        additionalOptionalFields = new HashMap<>();

        additionalOptionalFields.put(StandardEntryType.Article.getDisplayName(), OPTIONAL_ARTICLE);
        additionalOptionalFields.put(StandardEntryType.Book.getDisplayName(),OPTIONAL_BOOK);
        additionalOptionalFields.put(StandardEntryType.Booklet.getDisplayName(), OPTIONAL_BOOKLET);
        additionalOptionalFields.put(StandardEntryType.Collection.getDisplayName(), OPTIONAL_COLLECTION);
        additionalOptionalFields.put(StandardEntryType.Conference.getDisplayName(), OPTIONAL_CONFERENCE);
        additionalOptionalFields.put(StandardEntryType.InBook.getDisplayName(), OPTIONAL_INBOOK);
        additionalOptionalFields.put(StandardEntryType.InCollection.getDisplayName(), OPTIONAL_INCOLLECTION);
        additionalOptionalFields.put(StandardEntryType.InProceedings.getDisplayName(), OPTIONAL_INPROCEEDINGS);
        additionalOptionalFields.put(StandardEntryType.Manual.getDisplayName(), OPTIONAL_MANUAL);
        additionalOptionalFields.put(StandardEntryType.MastersThesis.getDisplayName(), OPTIONAL_MASTERTHESIS);
        additionalOptionalFields.put(StandardEntryType.Misc.getDisplayName(), OPTIONAL_MISC);
        additionalOptionalFields.put(StandardEntryType.PhdThesis.getDisplayName(), OPTIONAL_PHDTHESIS);
        additionalOptionalFields.put(StandardEntryType.Proceedings.getDisplayName(), OPTIONAL_PROCEEDINGS);
        additionalOptionalFields.put(StandardEntryType.TechReport.getDisplayName(), OPTIONAL_TECHREPORT);
        additionalOptionalFields.put(StandardEntryType.Unpublished.getDisplayName(), OPTIONAL_UNPUBLISHED);

    }

    public Field[] getOptionalFields(EntryType entryType) {
        Field[] fields = additionalOptionalFields.get(entryType.getDisplayName());
        return fields;
    }

    public Field[] getCommonFields() {;
        return commonFields;
    }
}
