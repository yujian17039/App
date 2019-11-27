package com.lantu.mychart;

public class userDaily {
    private String userID;
    private String userName;
    private String userLName;
    private String userTel;
    private String testDate;
    private double glucoseDose;
    private double insulinDose;
    private double insulinPredict;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public userDaily()
    {
        userID = ApplicationClass.userID;
        userName = ApplicationClass.userfname;
        userLName = ApplicationClass.userlname;
        userTel = ApplicationClass.userTel;
    }

    public userDaily(String userID, String userName, String userTel, String testDate, double glucoseDose, double insulinDose, double insulinPredict) {
        this.userID = userID;
        this.userName = userName;
        this.userTel = userTel;
        this.testDate = testDate;
        this.glucoseDose = glucoseDose;
        this.insulinDose = insulinDose;
        this.insulinPredict = insulinPredict;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public double getGlucoseDose() {
        return glucoseDose;
    }

    public void setGlucoseDose(double glucoseDose) {
        this.glucoseDose = glucoseDose;
    }

    public double getInsulinDose() {
        return insulinDose;
    }

    public void setInsulinDose(double insulinDose) {
        this.insulinDose = insulinDose;
    }

    public double getInsulinPredict() {
        return insulinPredict;
    }

    public void setInsulinPredict(double insulinPredict) {
        this.insulinPredict = insulinPredict;
    }
}
