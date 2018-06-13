package playground;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
public class ReadView {


    //Properties
    private DoubleProperty level;
    private DoubleProperty effRadius; // The effective Radius after considering level*height + radius
    private Read read;
    private ArchSegment archSegment;
    private GlobalInformation info;


    public ReadView(Read read, GlobalInformation info,DoubleProperty level){
        this.read = read;
       /* this.info.setCenter(info.getCenter());
        this.info.setGlobalLength(info.getGlobalLength());
        this.info.setHeight(info.getHeight());
        this.info.setRadius(info.getRadius());
        this.level.setValue(level.getValue());*/
        this.archSegment = new ArchSegment(read,info,level);
    }
    //SETTER
    public void setLevel(double level){
        this.level.set(level);
    }
    //GETTER

    public Read getRead() {
        return read;
    }
    public ArchSegment getArchSegment(){
        return archSegment;
    }
}
