package org.jabref.gui.entryeditor.additionalfields;

import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;

import java.util.*;

public class AdditionalRequiredFields {

    private Map<String, Field[]> additionalRequiredFields;
    private Field[] commonFields = {};

    private static final Field[] REQUIRED_ARTICLE = {StandardField.CREDIT_INSTITUTION};
    private static final Field[] REQUIRED_BOOK = {StandardField.FUNDED_BY};
    private static final Field[] REQUIRED_BOOKLET = {};
    private static final Field[] REQUIRED_COLLECTION= {};
    private static final Field[] REQUIRED_CONFERENCE = {};
    private static final Field[] REQUIRED_INBOOK = {};
    private static final Field[] REQUIRED_INCOLLECTION = {};
    private static final Field[] REQUIRED_INPROCEEDINGS = {};
    private static final Field[] REQUIRED_MANUAL = {};
    private static final Field[] REQUIRED_MASTERTHESIS= {StandardField.CREDIT_INSTITUTION};
    private static final Field[] REQUIRED_MISC = {};
    private static final Field[] REQUIRED_PHDTHESIS = {StandardField.CREDIT_INSTITUTION};
    private static final Field[] REQUIRED_PROCEEDINGS= {};
    private static final Field[] REQUIRED_TECHREPORT = {};
    private static final Field[] REQUIRED_UNPUBLISHED = {};

    public AdditionalRequiredFields() {

        additionalRequiredFields = new HashMap<>();

        additionalRequiredFields.put(StandardEntryType.Article.getDisplayName(),REQUIRED_ARTICLE);
        additionalRequiredFields.put(StandardEntryType.Book.getDisplayName(),REQUIRED_BOOK);
        additionalRequiredFields.put(StandardEntryType.Booklet.getDisplayName(), REQUIRED_BOOKLET);
        additionalRequiredFields.put(StandardEntryType.Collection.getDisplayName(), REQUIRED_COLLECTION);
        additionalRequiredFields.put(StandardEntryType.Conference.getDisplayName(), REQUIRED_CONFERENCE);
        additionalRequiredFields.put(StandardEntryType.InBook.getDisplayName(), REQUIRED_INBOOK);
        additionalRequiredFields.put(StandardEntryType.InCollection.getDisplayName(), REQUIRED_INCOLLECTION);
        additionalRequiredFields.put(StandardEntryType.InProceedings.getDisplayName(), REQUIRED_INPROCEEDINGS);
        additionalRequiredFields.put(StandardEntryType.Manual.getDisplayName(), REQUIRED_MANUAL);
        additionalRequiredFields.put(StandardEntryType.MastersThesis.getDisplayName(), REQUIRED_MASTERTHESIS);
        additionalRequiredFields.put(StandardEntryType.Misc.getDisplayName(), REQUIRED_MISC);
        additionalRequiredFields.put(StandardEntryType.PhdThesis.getDisplayName(), REQUIRED_PHDTHESIS);
        additionalRequiredFields.put(StandardEntryType.Proceedings.getDisplayName(), REQUIRED_PROCEEDINGS);
        additionalRequiredFields.put(StandardEntryType.TechReport.getDisplayName(), REQUIRED_TECHREPORT);
        additionalRequiredFields.put(StandardEntryType.Unpublished.getDisplayName(), REQUIRED_UNPUBLISHED);

    }

    public Field[] getRequiredFields(EntryType entryType) {
        Field[] fields = additionalRequiredFields.get(entryType.getDisplayName());
        return fields;
    }

    public Field[] getCommonFields() {;
        return commonFields;
    }
}
