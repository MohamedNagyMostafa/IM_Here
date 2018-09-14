package com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects;

/**
 * Created by Mohamed Nagy on 9/14/2018 .
 * Project IM_Here
 * Time    9:22 AM
 */
public class Track {

    private Double lat;
    private Double lng;
    private Long date;

    public Track(){}

    public Track(Double lat, Double lng, Long date){
        this.lat = lat;
        this.lng = lng;
        this.date = date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Long getDate() {
        return date;
    }
}
