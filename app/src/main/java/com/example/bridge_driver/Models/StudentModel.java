package com.example.bridge_driver.Models;

public class StudentModel {

    private String Name;
    private String Admission_no;
    private String Standard;
    private String Sec;

    public StudentModel(){}

    public StudentModel(String Name, String Admission_no, String Standard, String Sec) {
        this.Name = Name;
        this.Admission_no=Admission_no;
        this.Standard=Standard;
        this.Sec=Sec;
    }

    public String getName() {
        return Name;
    }
    public String getAdmission_no() {
        return Admission_no;
    }
    public String getStandard() {
        return Standard;
    }
    public String getSec(){
        return Sec;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setAdmission_no(String admission_no) {
        this.Admission_no = admission_no;
    }

    public void setStandard(String standard) {
        this.Standard = standard;
    }

    public void setSec(String sec) {
        this.Sec = sec;
    }
}
