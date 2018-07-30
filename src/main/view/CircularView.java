package main.view;

import javafx.scene.CacheHint;
import javafx.scene.paint.Color;
import main.model.CircularParser;
import main.model.Read;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class containing readViews, and function to create a levelArray. needs a Array of Reads + globalInformation to be constructed
 * Has 2 different constructors (and level-methods), which is based on the developemnt of our model-datastructure and our approach how to stack a level.
 * @author Felix
 */
public class CircularView {
    /**
     * the levelArray contains information on which level each Read is to be drawn
     */
    private Integer[] levelArrayAsInteger; // for the second LevelMethod
    /**
     * the GlobalInformation contains information that is needed for the ArchSegments (center,radius,circumference)
     */
    public GlobalInformation info;
    /**
     * the Array of Readviews
     */
    private ReadView[]readViews;
    private int[] levelArray;
    private Read[] readArrayOfSecondLevelCreation; // for the second LevelMethod
    /**
     * First used when the received data-structure was an Array and our approach of stacking highly primitive, remains here but can later be restructured / removed
     * @param readArray Array of the reads, of which a CircularView is to be created
     * @param info GlobalInformation with the needed information
     */
    public CircularView (Read[] readArray, GlobalInformation info){
        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.height.getValue(),info.getReferenceLength());
        // Fill the Array of readViews
        readViews = new ReadView[readArray.length];
        levelArray = createLevelArray(readArray);
        for (int i = 0; i < readArray.length;i++) {
            readViews[i] = new ReadView(readArray[i],this.info, levelArray[i]);
        }
        this.info.height.addListener((observable, oldValue, newValue) -> {
            for (ReadView readView : readViews) {
                readView.updateHeight((double) newValue);
                readView.updateStrokeWidth((double) newValue);

            }
        });

    }

    /**
     * Creates a CircularView, creates a levelArray with a different method since this method is given a different datatype.
     * Adds a Listener on the Height-property of the GlobalInformation - if that happens to change (e.g when zooming) we adjust each arch´s strokewidth and their placement
     * @param listOfReadLists data from our model about the Reads, for information regarding the structure visit the CircularParser class
     * @param info globalInformation with center/radius/base height/referenceLength
     */
    public CircularView(List< List< Read > >  listOfReadLists, GlobalInformation info){

        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.height.getValue(),info.getReferenceLength());
        readViews = new ReadView[CircularParser.Reads.getReadAmount()];
        levelArrayAsInteger = createLevelArray(listOfReadLists, info.getReferenceLength() /4);//Actually suprised that setting the startindex worked
        for(int i = 0; i < readArrayOfSecondLevelCreation.length;i++){
            readViews[i] = new ReadView(readArrayOfSecondLevelCreation[i],this.info,levelArrayAsInteger[i]);
            }
        this.info.height.addListener((observable, oldValue, newValue) -> {
            for (ReadView readView : readViews) {
                readView.updateHeight((double) newValue);
            }
        });
    }

    /**
     * Super primitive form of creating a level-array. This method prioritizes to firstly put all circulars on top of each other, then fill in the rest.
     * PRO: all circulars bundles together and close to the center
     * CON: skews data since this isnt exactly representative of the circular density of all the reads
     * @param readArray The array of reads given
     * @return  returns a levelArray
     */
    private int[] createLevelArray(Read[] readArray) {
        //DO magic- so that we have a level-array
        ArrayList<Integer> occupancyOfLevels = new ArrayList<>();
        ArrayList<Integer> endOfCircularReads = new ArrayList<>(); //this is the true End of the circulars != getAlignmentEnd
        int[] tempArray;
        tempArray = new int[info.getReferenceLength()];
        //This firstly adds in all the circular reads
        for (int index = 0; index < readArray.length; index++) {
            if (readArray[index].isCrossBorder()) {
                occupancyOfLevels.add(readArray[index].getAlignmentEnd());
                endOfCircularReads.add(readArray[index].getAlignmentStart());
                tempArray[index] = occupancyOfLevels.size() - 1; //First element is at level 0 2nd at 1 etc thus -1
            }
        }
        //Now we want to fill with the noncircular reads
        occupancyOfLevels.add(0); //this is currently the way to avoid that non-circular plasmids get a level-array=0 -> NEEDS a proper fix
        endOfCircularReads.add(0);//this is currently the way to avoid that non-circular plasmids get a level-array=0 -> NEEDS a proper fix
        for (int index = 0; index < readArray.length; index++) {
            if (!readArray[index].isCrossBorder()) {
                for (int level = 0; level < occupancyOfLevels.size(); level++) {
                    if (occupancyOfLevels.get(level) < readArray[index].getAlignmentStart() && (level < endOfCircularReads.size() && readArray[index].getAlignmentEnd() < endOfCircularReads.get(level))) {//We found a suitable spot for the read: no circularRead on the right side, on the left side no (circular/noncircular)read
                        occupancyOfLevels.set(level, readArray[index].getAlignmentEnd());
                        tempArray[index] = level;
                        break;
                    }
                    if (level == occupancyOfLevels.size() - 1) {//We traversed all to this-date known levels and found no suitable spot -> increase level by 1 and add it in the occupancy list
                        occupancyOfLevels.add(readArray[index].getAlignmentEnd());
                        tempArray[index] = level + 1;
                        break;
                    }
                    if (level >= endOfCircularReads.size() && occupancyOfLevels.get(level) < readArray[index].getAlignmentStart()) { //level is above the circular Reads, so we don´t have to worry about rightbound circular views
                        occupancyOfLevels.set(level, readArray[index].getAlignmentEnd());
                        tempArray[index] = level;
                        break;
                    }

                }

            }
        }
        return tempArray;
    }

    /**
     * Creates a levelArray: the principle of this Method is bound to the given datatype:
     * We are given a List of List of Reads.
     * The index of the List represents the Baseposition of the reads.
     * If there are multiple(if any) Reads starting at the same position they are decreasingly sorted by their length.
     * a startIndex is also given, representing the position of the Ring where we start to try and place Reads.
     *  - this can vary from approach but for the first Implementation it is assumed its value is the leftPosition of the most left gapclosing Read
     *
     *  The Procedure can be broken down:
     *  - we traverse the given Array from start to End(i). Since we want to start at the startIndex our acessing indexes are thus calculated like this array[i+startIndex%gLength)
     *  - if we haven´t already placed a firstRead in the current level, we have to do that: (so we know our right&left bounds)
     *      - we didnt find a read at the current Position, we increment the index and move along
     *      - if we aren´t able to place a read in the current level, and haven´t already placed one  we can only conclude that there is nothing left
     *  - if we have placed a first Read in the current Level, we can check at the next possible locations if there are any suitiable entries in the (we can skip incrementing soem and start looking at the end of the last read)
     *      - there are entries: so we first check if the last(smallest) entry in the list would fit.
     *          - If it doesnt we can assume that everything else in that list doesnt fit aswell. So we increment the index and move along
     *          - If it does fit we can now traverse the list in order till we find the biggest still fitting read.
     *      - there are no entries:
     *          - but we still have indexes to traveL: so we increment the index and move along
     *          - we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
     * @param listOfReadLists
     * @param startIndex
     * @return
     */
    private Integer[] createLevelArray(List<List<Read>> listOfReadLists, int startIndex){
        boolean firstReadInLevelSet = false;
        List <Integer> levelArrayList= new ArrayList<>();
        List <Read> readArrayList = new ArrayList<>();
        boolean allDistributed = false;
        int leftBound=0;
        int gLength=listOfReadLists.size();
        int index=0;
        int level=0;


        String before,
                after;
        before = CircularParser.Reads.getReadsSorted().toString();
        long timeBefore,
                timeAfter;
        timeBefore = System.currentTimeMillis();
        listOfReadLists = deleteWeirdReads(listOfReadLists);
        while(!allDistributed){
           while(index <gLength){ // - we traverse the given Array from start to End(i). Since we want to start at the startIndex our acessing indexes are thus calculated like this array[i+startIndex%gLength)
               if (!firstReadInLevelSet){ // - if we haven´t already placed a firstRead in the current level, we have to do that: (so we know our right&left bounds)
                   if(!listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ // we are able to place a first Read in the level.
                       firstReadInLevelSet=true;
                       leftBound=listOfReadLists.get((index+startIndex)%gLength).get(0).getAlignmentStart()-1;
                       readArrayList.add(listOfReadLists.get((index+startIndex)%gLength).get(0));
                       levelArrayList.add(level);
                       int tempLength = listOfReadLists.get((index+startIndex)%gLength).get(0).getAlignmentLength();
                       listOfReadLists.get((index+startIndex)%gLength).remove(0);
                       index+=tempLength;
                       if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                           level++;
                           firstReadInLevelSet=false;
                           index = 0;
                           leftBound=0; //Possibly not needed | Because it gets set at firstRead anyway
                       }
                   }
                   else if(index == gLength-1&&listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ // - if we aren´t able to place a read in the current level, and haven´t already placed one  we can only conclude that there is nothing left.
                       allDistributed=true;
                       readArrayOfSecondLevelCreation= readArrayList.toArray(new Read[0]);
                       break;
                   }
                   else { //  - we didnt find a read at the current Position, we increment the index and move along
                       index++;
                   }
               }
               else { //- if we have placed a first Read in the current Level, we can check at the next possible locations if there are any entries in the Array
                   if (!listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ //- there are entries: so we first check if the last entry in the list would fit.
                       if (listOfReadLists.get((index+startIndex)%gLength).get(listOfReadLists.get((index+startIndex)%gLength).size()-1).getAlignmentLength()>((gLength-index)+leftBound)){ //  - If it doesnt we can assume that everything else in that list doesnt fit aswell. So we increment the index and move along
                           index++;
                       }
                       else { // - If it does fit we can now traverse the sublist in order till we find the longest fitting read.
                           for (int j = 0; j < listOfReadLists.get((index+startIndex)%gLength).size();j++){
                               if (listOfReadLists.get((index+startIndex)%gLength).get(j).getAlignmentLength()<=((gLength-index+leftBound))){
                                   readArrayList.add(listOfReadLists.get((index+startIndex)%gLength).get(j));
                                   levelArrayList.add(level);
                                   int tempLength = listOfReadLists.get((index+startIndex)%gLength).get(j).getAlignmentLength();
                                   listOfReadLists.get((index+startIndex)%gLength).remove(j);
                                   index += tempLength;
                                   if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                                       level++;
                                       firstReadInLevelSet=false;
                                       index = 0;
                                       leftBound=0; //Possibly not needed | Because it gets set at firstRead anyway
                                   }
                               }
                           }
                       }

                   }
                   else { //- there are no entries:
                       if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                           level++;
                           firstReadInLevelSet=false;
                           index = 0;
                           leftBound=0; //Possibly not needed | Because it gets set at firstRead anyway
                       }
                       else { // - but we still have indexes to traveL: so we increment the index and move along
                           index++;
                       }
                   }
               }
            }
        }
        timeAfter = System.currentTimeMillis();
        System.out.println("The creation of the level-array took " + (timeAfter-timeBefore) + "milliseconds");
        after = CircularParser.Reads.getReadsSorted().toString();
        if(!before.equals(after)) System.out.println("CHANGE OF DATA");
        return levelArrayList.toArray(new Integer[0]);
    }

    /**
     * Changes the color of all the ReadViews, depending if they are gapcloser/reversed/normal they get assigned their new color value.
     * @param colorGapCloser
     * @param colorReversed
     * @param colorNormal
     */
    public void changeColor(Color colorGapCloser, Color colorReversed, Color colorNormal){
        for(ReadView rW: readViews){
            if(rW.getRead().isCrossBorder()){
                rW.getArchSegment().setColor(colorGapCloser);
            }
            else if(rW.getRead().getNegativeStrandFlag()){
                rW.getArchSegment().setColor(colorReversed);
            }
            else{
                rW.getArchSegment().setColor(colorNormal);
            }
        }
    }
    //TODO: add method with color + flag to set a specific case only
    void enableCacheOfReadViews(){
        for (ReadView readView : readViews) {
            readView.getArchSegment().getInner().setCache(true);
        }
    }
    public void disableCacheOfReadViews(){
        for (ReadView readView : readViews) {
            readView.getArchSegment().getInner().setCache(false);
        }
    }
    void cacheToQuality(){
        for(ReadView rW: readViews){
            rW.getArchSegment().getInner().setCacheHint(CacheHint.QUALITY);
        }
    }
    void cacheToSpeed(){
        for(ReadView rW:readViews){
            rW.getArchSegment().getInner().setCacheHint(CacheHint.SPEED);
        }
    }

    public ReadView[] getReadViews() {
        return readViews;
    }
    public int[] getLevelArray(){
        return levelArray;
    }

    /**
     * generic print-function for debugging-Purposes
     */
    public void printLevelArrayforDebugging(){
        int currentLevel = 0;
        String[] printText = new String[levelArrayAsInteger[levelArrayAsInteger.length-1]+1];
        for(String s: printText){
            s= " s";
        }
        for(int i = 0; i < levelArrayAsInteger.length;i++){
            if(levelArrayAsInteger[i]!=currentLevel){
              currentLevel++;
              printText[currentLevel] += "s:"+ readArrayOfSecondLevelCreation[i].getAlignmentStart()+ " l:" + readArrayOfSecondLevelCreation[i].getAlignmentLength() + " e:" + readArrayOfSecondLevelCreation[i].getAlignmentEnd() + " ";
            }
            else{
                printText[currentLevel] += "s:"+ readArrayOfSecondLevelCreation[i].getAlignmentStart()+ " l:" + readArrayOfSecondLevelCreation[i].getAlignmentLength() + " e:" + readArrayOfSecondLevelCreation[i].getAlignmentEnd() + " ";
            }
        }
        for (String s:printText) {
            System.out.println(s);
        }
    }
    /**
     * generic print-function for debugging-Purposes
     */
    public void checkIfLevelArrayisCorrect(){
        int currentLevel = 0;
        int currentEnd = 0;
        boolean onecrossborderperlevel = false;
        boolean[] correctPlacement = new boolean[levelArrayAsInteger[levelArrayAsInteger.length-1]+1];
        for(int i = 0; i < correctPlacement.length; i++){
            correctPlacement[i] = true;
        }
        /*for(int i = 0; i < levelArrayAsInteger.length;i++){
            if(levelArrayAsInteger[i]!=currentLevel){
                currentLevel++;
                currentEnd=0;
                if(!readArrayOfSecondLevelCreation[i].isCrossBorder()&&readArrayOfSecondLevelCreation[i].getAlignmentStart()<currentEnd) correctPlacement[currentLevel]= false;

            }
            else{
                if(readArrayOfSecondLevelCreation[i].getAlignmentStart()<currentEnd) correctPlacement[currentLevel]= false;

            }
            currentEnd=readArrayOfSecondLevelCreation[i].getAlignmentEnd();
        }*/
        for(int i = 0; i < levelArrayAsInteger.length;i++){
             if(onecrossborderperlevel){
                if(levelArrayAsInteger[i]!=currentLevel){
                    currentLevel++;
                    onecrossborderperlevel = readArrayOfSecondLevelCreation[i].isCrossBorder();
                }
                else{
                    if(readArrayOfSecondLevelCreation[i].isCrossBorder()) correctPlacement[currentLevel] = false;
                }
            }
            if(!onecrossborderperlevel){
                if(levelArrayAsInteger[i]!=currentLevel){
                    currentLevel++;
                    onecrossborderperlevel = readArrayOfSecondLevelCreation[i].isCrossBorder();
                }
                else{
                    if(readArrayOfSecondLevelCreation[i].isCrossBorder()) onecrossborderperlevel = true;
                }
            }
        }
        for (boolean s:correctPlacement) {
            System.out.println(s);
        }
        System.out.println(correctPlacement.length);
        int wrongLengthcount =0;
        for(Read r :readArrayOfSecondLevelCreation){
            if(r.getAlignmentLength()==0){
                //System.out.println("Read with 0 length detected starting at" + r.getAlignmentStart()+ "ending at: " + r.getAlignmentEnd() + "IsCrossBorder " +r.isCrossBorder() + "Readname: "+ r.getName() + "Sequence: " + r.getSequence());
                wrongLengthcount++;
            }
        }
        //System.out.println("amount of wrong length reads: " + wrongLengthcount);
        for(Read r: readArrayOfSecondLevelCreation){
            int start,end,length,reallength;
            start = r.getAlignmentStart();
            end = r.getAlignmentEnd();
            length= r.getAlignmentLength();
            reallength= info.getReferenceLength() -start+end;
            //if(r.isCrossBorder()) System.out.println("Crossborder starting at:" + start+ " length of: "+length+ " ending at: "+ end + " Math: referenceLength -alignmentStart + alignmentEnd = alignmentLength: ("+ (int)info.getGlobalLength() + "-"+start +")+"+end+ "="+reallength+" "+ " !=" + length);
            //if(!r.isCrossBorder()) System.out.println("Read starting at: "+ start+" length of: "+ length +" end at: "+end);
        }
    }

    /**
     * Basis on this function is, that weird reads exist with a sequence of only "*". their length is inproperly set from SAM-Reader. Possibly unneccesaary - recheck
     * @param listOfReadLists
     * @return
     */
    private List< List< Read > > deleteWeirdReads(List< List< Read > >  listOfReadLists){
        for(List<Read> list:listOfReadLists) {
            for (Read read : list) {
                if(Objects.equals(read.getSequence(), "*")) read.setAlignmentLength(read.getAlignmentEnd()-read.getAlignmentStart());
            }
        }
        return listOfReadLists;
    }
}
