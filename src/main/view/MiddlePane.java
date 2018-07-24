package main.view;

import javafx.scene.CacheHint;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class MiddlePane extends AnchorPane {
    private MainView mainView;
    private LabelView labelView;
    public MiddlePane(){
        mainView = new MainView();
        this.setHeight(800);
        this.setWidth(1400);
        setupRescale();
        this.getChildren().add(mainView);

    }

    private void setupRescale(){
        this.widthProperty().addListener((observable,oldValue,newValue)->{
            System.out.println("width listener oldvalue" + oldValue + " newvalue: "+ newValue);
            double DeltaX = newValue.doubleValue()-oldValue.doubleValue();
            mainView.setTranslateX(mainView.getTranslateX()+DeltaX/2);
        });
        this.heightProperty().addListener((observable,oldValue,newValue)->{
            System.out.println("height listener: oldvalue" + oldValue + " newvalue: "+ newValue);
            double DeltaY = newValue.doubleValue()-oldValue.doubleValue();
            mainView.setTranslateY(mainView.getTranslateY()+DeltaY/2);
        });
    }
    public void CacheTempSpeed(){
        mainView.CacheTempSpeed();
    }
    public void CacheTempQuality(){
        mainView.CacheTempQuality();
    }
}
