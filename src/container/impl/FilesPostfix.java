package container.impl;

public enum FilesPostfix {
    DATA("_data.bin"),
    METADATA("_meta.bin");

    private final String text;

    FilesPostfix(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
