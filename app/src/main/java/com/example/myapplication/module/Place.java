package com.example.myapplication.module;


import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String place_id;
    private String reference;
    private String imageBase64;
    private String photo_reference;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String phone;
    private double distance;

    public Place(String place_id, String reference, String imageBase64, String photo_reference, String name, String address, double lat, double lng, String phone, double distance) {
        this.place_id = place_id;
        this.reference = reference;
        this.imageBase64 = imageBase64;
        this.photo_reference = photo_reference;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
        this.distance = distance;
    }

    public Place(String place_id, String reference, String photo_reference, String name, String address, double lat, double lng, double distance, String phone) {
        this.place_id = place_id;
        this.reference = reference;
        this.photo_reference = photo_reference;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.phone = phone;

    }

    protected Place(Parcel in) {
        place_id = in.readString();
        reference = in.readString();
        imageBase64 = in.readString();
        photo_reference = in.readString();
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        phone = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getPlaceId() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPhotoReference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String photo) {
        this.imageBase64 = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place_id);
        dest.writeString(reference);
        dest.writeString(imageBase64);
        dest.writeString(photo_reference);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(phone);
        dest.writeDouble(distance);
    }
}
