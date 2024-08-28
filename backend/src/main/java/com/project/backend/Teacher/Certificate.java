package com.project.backend.Teacher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Certificate {
    private String Master;
    private String Phd;
    private String University;

    protected Certificate(){

    }

    public Certificate(String Master, String Phd, String University) {
        this.Master = Master;
        this.Phd = Phd;
        this.University = University;
    }
}
