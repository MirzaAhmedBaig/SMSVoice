package com.example.dell.firebasephoneauthexample;

public class User {
    private String id,etname,etaddress,etphonenumber,etoccupation,etbankname,etbankbranch;

   // public User(){

    //}
    public User(String id,String etname,String etaddress,String etphonenumber,String etoccupation,String etbankname,String etbankbranch){

        this.id = id;
        this.etname = etname;
        this.etaddress = etaddress;
        this.etphonenumber = etphonenumber;
        this.etoccupation = etoccupation;
        this.etbankname = etbankname;
        this.etbankbranch = etbankbranch;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtname(){return  etname;
    }
    public void setEtname(String etname){
        this.etname = etname;
    }

    public String getEtaddress(){return  etaddress;
    }
    public void setEtaddress(String etaddress){
        this.etaddress = etaddress;
    }


    public String getEtphonenumber(){return  etphonenumber;
    }
    public void setEtphonenumber(String etphonenumber){
        this.etphonenumber = etphonenumber;
    }


    public String getEtoccupation(){return  etoccupation;
    }
    public void setEtoccupation(String etoccupation){
        this.etoccupation = etoccupation;
    }


    public String getEtbankname(){return  etbankname;
    }
    public void setEtbankname(String etbankname){
        this.etbankname = etbankname;
    }


    public String getEtbankbranch(){return  etbankbranch;
    }
    public void setEtbankbranch(String etbankbranch){
        this.etbankbranch = etbankbranch;
    }



}
