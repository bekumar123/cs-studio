package org.csstudio.dct.ui.editor.tables;

/**
 * Describes a single column for a {@link ConvenienceTableWrapper}.
 * 
 * @author Sven Wende
 * 
 */
public final class ColumnConfig {
    private String id;
    private String title;
    private int width;
    private boolean isCheckBox;

    /**
     * Constructor.
     * 
     * @param id
     *            an unique ID
     * @param title
     *            a title
     * @param width
     *            the column width
     */
    public ColumnConfig(String id, String title, int width) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.isCheckBox = false;
    }

    /**
     * Constructor.
     * 
     * @param id
     *            an unique ID
     * @param title
     *            a title
     * @param width
     *            the column width
     * @param isCheckBox
     *            display column as checkbox (true or false)
     */
    public ColumnConfig(String id, String title, int width, boolean isCheckBox) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.isCheckBox = isCheckBox;
    }

    /**
     * Returns an unique id for the column.
     * 
     * @return an unique id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns a title for the column.
     * 
     * @return a title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the column width.
     * 
     * @return the column width
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * @return true if the colum should be displayed as a checkbox
     */
    public boolean isCheckBox() {
        return isCheckBox;
    }

}
