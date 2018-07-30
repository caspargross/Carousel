package main.view.TickMarkView;

import javafx.scene.layout.AnchorPane;
import main.view.Helper.GlobalInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * class containing the view Elements of the Tickmarks
 * for future use  those are generated in multiple levels/panes so we can begin showing some only when zooming far in
 * @author Felix
 */
public class TickmarkView extends AnchorPane {

    private List<TickmarkPane> paneList = new ArrayList<>();

    /**
     * constructs the TickMarkView with a given globalInfromation about our model-data,
     * adds a listener so that whenever the height is changed (and we zoom in / out) the width/height of our Tickmarks is adjusted
     * @param gInfo globalInformation about our model-data
     */
    public TickmarkView(GlobalInformation gInfo){
        int baseIntervall = 10000;
        double baseLength = 20;
        for(int i = 0; i < 3; i ++){
            paneList.add(new TickmarkPane(gInfo,baseLength/(i+1),baseIntervall/Math.pow(10,i)));
        }
        this.getChildren().addAll(paneList);
        gInfo.height.addListener((observable, oldValue, newValue) ->{
                for(TickmarkPane childrenPane:paneList){
                    childrenPane.updateHeight(newValue.doubleValue()/oldValue.doubleValue());
                    childrenPane.updateWidth(newValue.doubleValue()/oldValue.doubleValue());
                }
        });

    }

    /**
     * Getter of the paneList, currently unused
     * @return the List of Panes containg all the View-Elements
     */
    public List<TickmarkPane> getPaneList(){
        return paneList;
    }
}
