package playground;

public class CircularView {
    // contains an Array of Readviews
    private ReadView[]readViews;
    private Double[] levelArray;
    private GlobalInformation info;
    //CONSTRUCTOR
    public CircularView (Read[] readArray,GlobalInformation info ){
        this.info.setCenter(info.getCenter());
        this.info.setGlobalLength(info.getGlobalLength());
        this.info.setHeight(info.getHeight());
        this.info.setRadius(info.getRadius());

        // Fill the Array of readViews
        for (int i = 0; i < readArray.length;i++) {
            readViews[i] = new ReadView(readArray[i],this.info, levelArray[i]);
        }
    }
    public void createLevelArray(){
        //DO magic- so that we have a level-array
        //TODO: maybe move this levelArray to GlobalInformation
    }
}
