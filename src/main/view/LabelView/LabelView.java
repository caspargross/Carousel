package main.view.LabelView;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.AnchorPane;
import main.model.CircularParser;
import main.view.Helper.ViewHelper;

public class LabelView extends AnchorPane {
    public LabelView(){

    }
    public void createFloatingLabel(){
    //A Label that always is at the position of 12o clock in the circle, its text dynmaicyall changes on rotation
        IntegerProperty value = new SimpleIntegerProperty();
        value.bind(ViewHelper.rotationValue.divide(360).multiply(CircularParser.ReferenceSequences.Current.getLength()));
        //This value currently can be multitudes of the referenceSequenceLength since the rotation can be bigger than 360 or smaller than 0
        StringProperty valueText = new SimpleStringProperty();

    }

}
