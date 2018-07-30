package main.view;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class TickmarkView extends AnchorPane {

    private List<TickmarkPane> paneList = new ArrayList<>();

    public TickmarkView(GlobalInformation gInfo){
        int baseIntervall = 10000;
        double baseLength = 20;
        for(int i = 0; i < 3; i ++){
            paneList.add(new TickmarkPane(gInfo,baseLength/(i+1),baseIntervall/Math.pow(10,i)));

            System.out.println("something");
        }
        this.getChildren().addAll(paneList);
        gInfo.height.addListener((observable, oldValue, newValue) ->{
                for(TickmarkPane childrenPane:paneList){
                    childrenPane.updateHeight(newValue.doubleValue()/oldValue.doubleValue());
                    System.out.println("height changed");
                }
        });
    }

}
