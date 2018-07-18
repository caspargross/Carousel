package main.view;

import main.model.Read;

/**
 * Class consisting of a Read and through initializing an archSegment.
 * contains Getter for the ArchSegment&Read  +  functions to updateHeight&StrokeWidth of the archsegment,
 * @author Felix
 */
public class ReadView {


    private Read read;
    private ArchSegment archSegment;

    /**
     * Constructor of the ReadView element, on initialisation it creates a archSegment and sets the value of the read property.
     * @param read the read represented by this View element
     * @param info the information of the reference and general drawing information (center, radius..)
     * @param level the level the read is going to be placed in the ring
     */
    public ReadView(Read read, GlobalInformation info,int level){
        this.read = read;
        this.archSegment = new ArchSegment(read,info,level);
    }

    //GETTER

    /**
     * returns the Read of the ReadView object
     * @return the Read
     */
    public Read getRead() {
        return read;
    }

    /**
     * returns the archSegment of the ReadView object, and thus the option to receive its visual Elements
     * @return a archSegment and its public functions
     */
    public ArchSegment getArchSegment(){
        return archSegment;
    }

    /**
     * updates the Height for Zooming purposes, for further information regarding the functionality consider visiting {@link ArchSegment#updateHeight(double)}
     * @param height the new height(double)
     */
    public void updateHeight(double height){
        this.archSegment.updateHeight(height);
    }

    /**
     * updates the StrokeWidth for Zooming purposes, for further information regarding the functionailty consider visiting{@link ArchSegment#setStrokeWidth(double)}
     * @param newValue the new value of the strokeWidth (double) gets internally divided by 10
     */
    public void updateStrokeWidth(double newValue){
        this.archSegment.setStrokeWidth(newValue);
    }
}
