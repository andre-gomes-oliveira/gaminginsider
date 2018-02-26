package br.com.andregomesoliveira.gaminginsider.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Source implements Parcelable {

    private String name;
    private String url;

    public Source(){

    }

    public Source(String name, String url) {
        this.name = this.name;
        this.url = this.url;
    }

    private Source(Parcel parcel) {
        this.name = parcel.readString();
        this.url = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(url);
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {

        @Override
        public Source createFromParcel(Parcel parcel) {
            return new Source(parcel);
        }

        @Override
        public Source[] newArray(int i) {
            return new Source[i];
        }
    };
}
