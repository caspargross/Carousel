package main.view;

import javafx.beans.property.DoubleProperty;
import main.model.Read;

import java.util.ArrayList;

/**
 * Class containing readViews, and function to create a levelArray. needs a Array of Reads + globalInformation to be constructed
 * @author Felix
 */
public class CircularView {
    /**
     * the Array of Readviews
     */
    private ReadView[]readViews;
    /**
     * the levelArray contains information on which level each Read is to be drawn
     */
    private int[] levelArray;
    /**
     * the GlobalInformation contains information that is needed for the ArchSegments (center,radius,circumference)
     */
    private GlobalInformation info;

    /**
     * Creates a new CircularView, additionally creates a levelArray so the readViews can be properly drawn
     * @param readArray Array of Reads, selfexplanatory
     * @param info globalInformation, containing radius,center Coordinate, circumference etc
     */
    public CircularView (Read[] readArray, GlobalInformation info){
        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.getHeight(),info.getGlobalLength());
        // Fill the Array of readViews
        readViews = new ReadView[readArray.length];
        levelArray = createLevelArray(readArray);
        for (int i = 0; i < readArray.length;i++) {
            readViews[i] = new ReadView(readArray[i],this.info, levelArray[i]);
        }
    }
    private int[] createLevelArray(Read[] readArray){
        //DO magic- so that we have a level-array


        ArrayList<Integer> occupancyOfLevels = new ArrayList<>();
        ArrayList<Integer> endOfCircularReads= new ArrayList<>(); //this is the true End of the circulars != getAlignmentEnd
        int[] tempArray;
        tempArray = new int[(int)info.getGlobalLength()]; //TODO: clean up global info: ints where ints are needed, double where double are needed
        //This firstly adds in all the circular reads
        for(int index = 0; index < readArray.length;index++){
            if(readArray[index].isCircular()){
                occupancyOfLevels.add(readArray[index].getAlignmentEnd());
                endOfCircularReads.add(readArray[index].getAlignmentStart());
                tempArray[index] = occupancyOfLevels.size()-1; //First element is at level 0 2nd at 1 etc thus -1
            }
        }
        /*
        System.out.println(occupancyOfLevels.toString());
        System.out.println(occupancyOfLevels.size());
        System.out.println(endOfCircularReads.toString());
        System.out.println(endOfCircularReads.size());
        */
        //Now we want to fill with the noncircular reads
        occupancyOfLevels.add(0); //this is currently the way to avoid that non-circular plasmids get a level-array=0 -> NEEDS a proper fix
        endOfCircularReads.add(0);//this is currently the way to avoid that non-circular plasmids get a level-array=0 -> NEEDS a proper fix
        for(int index =0; index <readArray.length;index++){
            if(!readArray[index].isCircular()){
                for(int level = 0; level < occupancyOfLevels.size();level++){
                    if(occupancyOfLevels.get(level)<readArray[index].getAlignmentStart()&&(level<endOfCircularReads.size()&&readArray[index].getAlignmentEnd()<endOfCircularReads.get(level))){//We found a suitable spot for the read: no circularRead on the right side, on the left side no (circular/noncircular)read
                        occupancyOfLevels.set(level,readArray[index].getAlignmentEnd());
                        tempArray[index]=level;
                        break;
                    }
                    if(level == occupancyOfLevels.size()-1){//We traversed all to this-date known levels and found no suitable spot -> increase level by 1 and add it in the occupancy list
                        occupancyOfLevels.add(readArray[index].getAlignmentEnd());
                        tempArray[index]=level+1;
                        break;
                    }
                    if(level >=endOfCircularReads.size() &&occupancyOfLevels.get(level)<readArray[index].getAlignmentStart()){ //level is above the circular Reads, so we don´t have to worry about rightbound circular views
                        occupancyOfLevels.set(level,readArray[index].getAlignmentEnd());
                        tempArray[index]=level;
                        break;
                    }

                }

            }
        }
    return tempArray;
    }

    public ReadView[] getReadViews() {
        return readViews;
    }
    public int[] getLevelArray(){
        return levelArray;
    }
}
