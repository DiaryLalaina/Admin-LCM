package com.work.cashier.service;

import animatefx.animation.AnimationFX;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class NodeAnimation {

    public void animate(Node node,double delay,AnimationFX animation){
        PauseTransition pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(_ -> {
            animation.setNode(node);
            animation.play();
        });
        pause.play();
    }
    public void setAnimation(AnimationFX animation){

    }
}
