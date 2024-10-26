package container.impl;

public enum MetaDataFields {
    CURRENT_KEY("currentKey"),
    CURRENT_SIZE("currentSize");

    private final String text;

    MetaDataFields(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
