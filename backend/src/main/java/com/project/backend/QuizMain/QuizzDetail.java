package com.project.backend.QuizMain;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizzDetail {
    private Double score = 0.0;
    private List<String> history;
    boolean passOrnot = false;
    private Integer timeleft;
    protected QuizzDetail() {}

    public QuizzDetail(
            Double score,
            List<String> history,
            boolean passOrnot
    ){
        this.score = score;
        this.history = history;
        this.passOrnot = passOrnot;
        this.timeleft = 3;
    }
}
