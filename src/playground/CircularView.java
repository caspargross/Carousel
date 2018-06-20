package playground;

import javafx.beans.property.DoubleProperty;

public class CircularView {
    // contains an Array of Readviews
    private ReadView[]readViews;
    private DoubleProperty[] levelArray;
    private GlobalInformation info;
    //CONSTRUCTOR
    public CircularView (Read[] readArray,GlobalInformation info, DoubleProperty[] levelArray ){
        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.getHeight(),info.getGlobalLength());
        // Fill the Array of readViews
        readViews = new ReadView[readArray.length];
        for (int i = 0; i < readArray.length;i++) {
            readViews[i] = new ReadView(readArray[i],this.info, levelArray[i]);
        }
    }
    public void createLevelArray(){
        //DO magic- so that we have a level-array
        //TODO: maybe move this levelArray to GlobalInformation
    }

    public ReadView[] getReadViews() {
        return readViews;
    }
}
