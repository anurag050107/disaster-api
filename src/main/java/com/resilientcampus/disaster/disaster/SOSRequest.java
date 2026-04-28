package com.resilientcampus.disaster.disaster;

public class SOSRequest {
    private String studentId;
    private double latitude;
    private double longitude;
    private String time;

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}