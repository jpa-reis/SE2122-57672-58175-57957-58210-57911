package org.jabref.gui.entryeditor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;

import java.util.*;

public class AdditionalFields {

    private Map<String, Field[]> additionalRequiredFields;
    private Map<String, Field[]> additionalOptionalFields;

    private static final Field[] REQUIRED_ARTICLE = {StandardField.LANGUAGE};
    private static final Field[] REQUIRED_BOOK = {StandardField.FUNDED_BY};
    private static final Field[] REQUIRED_BOOKLET = {};
    private static final Field[] REQUIRED_COLLECTION= {};
    private static final Field[] REQUIRED_CONFERENCE = {};
    private static final Field[] REQUIRED_INBOOK = {};
    private static final Field[] REQUIRED_INCOLLECTION = {};
    private static final Field[] REQUIRED_INPROCEEDINGS = {};
    private static final Field[] REQUIRED_MANUAL = {};
    private static final Field[] REQUIRED_MASTERTHESIS= {};
    private static final Field[] REQUIRED_MISC = {};
    private static final Field[] REQUIRED_PHDTHESIS = {};
    private static final Field[] REQUIRED_PROCEEDINGS= {};
    private static final Field[] REQUIRED_TECHREPORT = {};
    private static final Field[] REQUIRED_UNPUBLISHED = {};

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


    public AdditionalFields() {

        additionalRequiredFields = new HashMap<>();
        additionalOptionalFields = new HashMap<>();

        additionalRequiredFields.put(StandardEntryType.Article.getDisplayName(), REQUIRED_ARTICLE);
        additionalOptionalFields.put(StandardEntryType.Article.getDisplayName(), OPTIONAL_ARTICLE);
        additionalRequiredFields.put(StandardEntryType.Book.getDisplayName(),REQUIRED_BOOK);
        additionalOptionalFields.put(StandardEntryType.Book.getDisplayName(),OPTIONAL_BOOK);
        additionalRequiredFields.put(StandardEntryType.Booklet.getDisplayName(), REQUIRED_BOOKLET);
        additionalOptionalFields.put(StandardEntryType.Booklet.getDisplayName(), OPTIONAL_BOOKLET);
        additionalRequiredFields.put(StandardEntryType.Collection.getDisplayName(), REQUIRED_COLLECTION);
        additionalOptionalFields.put(StandardEntryType.Collection.getDisplayName(), OPTIONAL_COLLECTION);
        additionalRequiredFields.put(StandardEntryType.Conference.getDisplayName(), REQUIRED_CONFERENCE);
        additionalOptionalFields.put(StandardEntryType.Conference.getDisplayName(), OPTIONAL_CONFERENCE);
        additionalRequiredFields.put(StandardEntryType.InBook.getDisplayName(), REQUIRED_INBOOK);
        additionalOptionalFields.put(StandardEntryType.InBook.getDisplayName(), OPTIONAL_INBOOK);
        additionalRequiredFields.put(StandardEntryType.InCollection.getDisplayName(), REQUIRED_INCOLLECTION);
        additionalOptionalFields.put(StandardEntryType.InCollection.getDisplayName(), OPTIONAL_INCOLLECTION);
        additionalRequiredFields.put(StandardEntryType.InProceedings.getDisplayName(), REQUIRED_INPROCEEDINGS);
        additionalOptionalFields.put(StandardEntryType.InProceedings.getDisplayName(), OPTIONAL_INPROCEEDINGS);
        additionalRequiredFields.put(StandardEntryType.Manual.getDisplayName(), REQUIRED_MANUAL);
        additionalOptionalFields.put(StandardEntryType.Manual.getDisplayName(), OPTIONAL_MANUAL);
        additionalRequiredFields.put(StandardEntryType.MastersThesis.getDisplayName(), REQUIRED_MASTERTHESIS);
        additionalOptionalFields.put(StandardEntryType.MastersThesis.getDisplayName(), OPTIONAL_MASTERTHESIS);
        additionalRequiredFields.put(StandardEntryType.Misc.getDisplayName(), REQUIRED_MISC);
        additionalOptionalFields.put(StandardEntryType.Misc.getDisplayName(), OPTIONAL_MISC);
        additionalRequiredFields.put(StandardEntryType.PhdThesis.getDisplayName(), REQUIRED_PHDTHESIS);
        additionalOptionalFields.put(StandardEntryType.PhdThesis.getDisplayName(), OPTIONAL_PHDTHESIS);
        additionalRequiredFields.put(StandardEntryType.Proceedings.getDisplayName(), REQUIRED_PROCEEDINGS);
        additionalOptionalFields.put(StandardEntryType.Proceedings.getDisplayName(), OPTIONAL_PROCEEDINGS);
        additionalRequiredFields.put(StandardEntryType.TechReport.getDisplayName(), REQUIRED_TECHREPORT);
        additionalOptionalFields.put(StandardEntryType.TechReport.getDisplayName(), OPTIONAL_TECHREPORT);
        additionalRequiredFields.put(StandardEntryType.Unpublished.getDisplayName(), REQUIRED_UNPUBLISHED);
        additionalOptionalFields.put(StandardEntryType.Unpublished.getDisplayName(), OPTIONAL_UNPUBLISHED);

    }

    public Field[] getRequiredFields(EntryType entryType) {
        Field[] fields = additionalRequiredFields.get(entryType.getDisplayName());
        return fields;
    }
    public Field[] getOptionalFields(EntryType entryType) {
        Field[] fields = additionalOptionalFields.get(entryType.getDisplayName());
        return fields;
    }
}
