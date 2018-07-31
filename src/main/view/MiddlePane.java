package main.view;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.view.LabelView.LabelView;

/**
 * Class that wraps the MainView, since rescaling behaved highly weirdly when this was not done (high fluctations of the height /width properties)
 * basically the same as the mainview
 * @author Felix
 */
public class MiddlePane extends AnchorPane {
    private MainView mainView;
    private LabelView labelView;
    public MiddlePane(){
        mainView = new MainView();
        this.setHeight(1000);
        this.setWidth(1800);
        setupRescale();
        this.getChildren().add(mainView);

    }

    private void setupRescale(){
        this.widthProperty().addListener((observable,oldValue,newValue)->{
            double DeltaX = newValue.doubleValue()-oldValue.doubleValue();
            mainView.setTranslateX(mainView.getTranslateX()+DeltaX/2);
        });
        this.heightProperty().addListener((observable,oldValue,newValue)->{
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
    public void EnableCache(){ mainView.EnableCache();
    }
    public void changeColor(Color colorGapCloser, Color colorReversed, Color colorNormal){
        mainView.changeColor(colorGapCloser,colorReversed,colorNormal);
    }
}
