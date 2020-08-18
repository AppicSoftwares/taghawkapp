package com.taghawk.adapters;

/**
 * Created by appinventiv on 18/5/18.
 */

public class SpinnerItem {
    private int valueId=0;
    private String valueText="";
    private boolean isSelected=false;

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
