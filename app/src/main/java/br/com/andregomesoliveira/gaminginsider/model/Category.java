package br.com.andregomesoliveira.gaminginsider.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Category implements Parcelable {

    private String name;
    private List<Source> sources;

    public Category() {
    }

    public Category(String name, List<Source> sources) {
        this.name = name;
        this.sources = sources;
    }

    private Category(Parcel parcel) {
        this.name = parcel.readString();
        this.sources = parcel.createTypedArrayList(Source.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeTypedList(sources);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel parcel) {
            return new Category(parcel);
        }

        @Override
        public Category[] newArray(int i) {
            return new Category[i];
        }
    };
}
