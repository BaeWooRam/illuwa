package com.geek.soft.illuwa.category;

public class CategoryBin {
    private int drawableid;
    private String title;
    private boolean selected;

    public CategoryBin() {

    }
    public CategoryBin(int drawableid, String title, boolean selected) {
        this.drawableid = drawableid;
        this.title = title;
        this.selected = selected;
    }

    public int getDrawableid() {
        return drawableid;
    }

    public void setDrawableid(int drawableid) {
        this.drawableid = drawableid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
