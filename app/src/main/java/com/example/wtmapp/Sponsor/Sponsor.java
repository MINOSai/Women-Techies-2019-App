package com.example.wtmapp.Sponsor;

public class Sponsor {
    private String sponsorName;
    private String sponsorType;
    private String sponsorWebsite;
    private int sponsorImage;

    public Sponsor(String sponsorName, String sponsorType, String sponsorWebsite, int sponsorImage) {
        this.sponsorName = sponsorName;
        this.sponsorType = sponsorType;
        this.sponsorWebsite = sponsorWebsite;
        this.sponsorImage = sponsorImage;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getSponsorType() {
        return sponsorType;
    }

    public String getSponsorWebsite() {
        return sponsorWebsite;
    }

    public int getSponsorImage() {
        return sponsorImage;
    }
}
