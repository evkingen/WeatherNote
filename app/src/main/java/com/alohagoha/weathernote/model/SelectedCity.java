package com.alohagoha.weathernote.model;

public class SelectedCity {
    private String city;
    private Boolean isSelected;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean isSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
