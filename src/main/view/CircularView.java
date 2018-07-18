package main.view;

import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import main.model.Read;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class containing readViews, and function to create a levelArray. needs a Array of Reads + globalInformation to be constructed
 * @author Felix
 */
public class CircularView {
    /**
     * the levelArray contains information on which level each Read is to be drawn
     */
    public Integer[] levelArrayAsInteger; // for the second LevelMethod
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
     * Creates a new CircularView, additionally creates a levelArray so the readViews can be properly drawn
     * @param readArray Array of Reads, selfexplanatory
     * @param info globalInformation, containing radius,center Coordinate, circumference etc
     */
    public CircularView (Read[] readArray, GlobalInformation info){
        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.height.getValue(),info.getGlobalLength());
        // Fill the Array of readViews
        readViews = new ReadView[readArray.length];
        levelArray = createLevelArray(readArray);
        for (int i = 0; i < readArray.length;i++) {
            readViews[i] = new ReadView(readArray[i],this.info, levelArray[i]);
        }
        this.info.height.addListener((observable, oldValue, newValue) -> {
            for(int i =0; i < readViews.length;i++){
                readViews[i].updateHeight((double)newValue);
                readViews[i].updateStrokeWidth((double)newValue);

            }
        });

    }

    /**
     * Creates a CircularView, creates a levelArray with a different method since this method is given a different datatype.
     * No testing available, will propably throw tons of errors..
     * @param listOfReadLists
     * @param info
     */

    public CircularView(ObservableList< List< Read > >  listOfReadLists, GlobalInformation info){
        printGivenReadList(listOfReadLists);
        this.info = new GlobalInformation(info.getCenter(),info.getRadius(),info.height.getValue(),info.getGlobalLength());
        readViews = new ReadView[calcReadCount(listOfReadLists)];
        levelArrayAsInteger = createLevelArray(listOfReadLists,0);
        for(int i = 0; i < readArrayOfSecondLevelCreation.length;i++){
            readViews[i] = new ReadView(readArrayOfSecondLevelCreation[i],this.info,levelArrayAsInteger[i]);
            }
        this.info.height.addListener((observable, oldValue, newValue) -> {
            for(int i =0; i < readViews.length;i++){
                readViews[i].updateHeight((double)newValue);
                readViews[i].updateStrokeWidth((double)newValue);

            }
        });
        printLevelArrayforDebugging();
        //checkIfLevelArrayisCorrect();
    }
    private int calcReadCount(ObservableList< List< Read > >  listOfReadLists){
        int readcount = 0;
        for(int i = 0; i < listOfReadLists.size(); i++){
            for(int j = 0; j <listOfReadLists.get(i).size();j++)
                readcount++;
        }
        return readcount;
    }


    /**
     * Super primitive form of creating a level-array. This method prioritizes to firstly put all circulars on top of each other, then fill in the rest.
     * PRO: all circulars bundles together and close to the center
     * CON: skews data since this isnt exactly representative of the circular density of all the reads
     * @param readArray The array of reads given
     * @return  returns a levelArray
     */
    private int[] createLevelArray(Read[] readArray){
        //DO magic- so that we have a level-array


        ArrayList<Integer> occupancyOfLevels = new ArrayList<>();
        ArrayList<Integer> endOfCircularReads= new ArrayList<>(); //this is the true End of the circulars != getAlignmentEnd
        int[] tempArray;
        tempArray = new int[(int)info.getGlobalLength()]; //TODO: clean up global info: ints where ints are needed, double where double are needed
        //This firstly adds in all the circular reads
        for(int index = 0; index < readArray.length;index++){
            if(readArray[index].isCrossBorder()){
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
            if(!readArray[index].isCrossBorder()){
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

    public Integer[] createLevelArray(ObservableList< List< Read > >  listOfReadLists, int startIndex){
        /**
         * To keep track if we even placed any Read in the current Level, this enables us that if we went through all the possible Indexes of the given Array and have´t placed a Read in, we can deduce that we no longer have ANY remaining reads.
         * Also it enables us to divide the cases:
         *  - that we still need to determine the bounds (which are already filled with reads) because we haven´t placed anything yet.
         *  - that we already placed something in this level (,have determined the bounds which are filled) and only need to check if next candidates fit in the empty parts of the levelRing
         */
        boolean firstReadInLevelSet = false;
        /**
         * ArrayList to store the Levels of the Reads. levelArraylist(i) is the level of the read at readArrayList(i)
         */
        List <Integer> levelArrayList= new ArrayList<Integer>();
        /**
         * Arraylist to store our reads that are already processed and assigned a level: levelArraylist(i) is the level of the read at readArrayList(i)
         */
        List <Read> readArrayList = new ArrayList<Read>();

        /**
         * if this gets true, we visited an entire (empty) levelRing without placing anything. Thus we logically have nothing left to place - and have distributed everything.
         */
        boolean allDistributed = false;
        /**
         * left Bound of our filled Segment of the levelRing (typically read.getAlignmentEnd of the firstly placed read. (if the first read is circular start&End are swapped)
         */
        int leftBound=0;
        /**
         * right Bound of our filled Segment of the levelRing (typically read.getAlignmentStart of the firstly placed read. (if the first read is circular start&End are swapped)
         * Since we add on the right side, this continously increases till we no longer find any fitting Reads between rightBound and leftBound
         */
        int rightBound=0;

        /**
         * is simply the amount of Indexes thelistOfReadLists has, thus the total referenceLength, not exactly necessarily but used to keep the expressions shorter.
         */
        int gLength=listOfReadLists.size();
        /**
         * simple counter for our index in the method
         */
        int index=0;
        /**
         * simple counter for our levele in the method
         */
        int level=0;
        /**
         * Creates a levelArray: the principle of this Method is bound to the given datatype:
         * We are given an Array of readArrayLists.
         * The index of the Array represents the Baseposition of the reads.
         * If there are multiple(if any) Reads starting at the same position they are decreasingly sorted by their length.
         * a startIndex is also given, representing the position of the Ring where we start to try and place Reads.
         *  - this can vary from approach but for the first Implementation it is assumed its value is the leftPosition of the most left gapclosing Read
         *
         *  The Procedure can be broken down:
         *  - we traverse the given Array from start to End(i). Since we want to start at the startIndex our acessing indexes are thus calculated like this array[i+startIndex%gLength)
         *  - if we haven´t already placed a firstRead in the current level, we have to do that: (so we know our right&left bounds)
         *      - we didnt find a read at the current Position, we increment the index and move along
         *      - if we aren´t able to place a read in the current level, and haven´t already placed one  we can only conclude that there is nothing left
         *  - if we have placed a first Read in the current Level, we can check at the next possible locations if there are any entries in the Array
         *      - there are entries: so we first check if the last entry in the list would fit.
         *          - If it doesnt we can assume that everything else in that list doesnt fit aswell. So we increment the index and move along
         *          - If it does fit we can now traverse the list in order till we find the fitting read.
         *      - there are no entries:
         *          - but we still have indexes to traveL: so we increment the index and move along
         *          - we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
         */

        long timeBefore,
                timeAfter;
        timeBefore = System.currentTimeMillis();
        int readcount = 0;
        for(int i = 0; i < listOfReadLists.size(); i++){
            for(int j = 0; j <listOfReadLists.get(i).size();j++)
                readcount++;
        }
        int firstplaced = 0;
        int placed = 0;
        deleteWeirdReads(listOfReadLists);
        System.out.println(listOfReadLists.toString());
        while(!allDistributed){
           while(index <gLength){ // - we traverse the given Array from start to End(i). Since we want to start at the startIndex our acessing indexes are thus calculated like this array[i+startIndex%gLength)
               if (!firstReadInLevelSet){ // - if we haven´t already placed a firstRead in the current level, we have to do that: (so we know our right&left bounds)
                   if(!listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ // we are able to place a first Read in the level.
                       // Insert add-procedure here
                       //System.out.println("we are able to place a first Read in the level: Index is (" + (index+startIndex)%gLength +") and the readname is " + listOfReadLists.get((index+startIndex)%gLength).get(0));
                       firstReadInLevelSet=true;
                       firstplaced++;
                       placed++;
                       leftBound=listOfReadLists.get((index+startIndex)%gLength).get(0).getAlignmentStart()-1;
                       //System.out.println("our Rightbound is now: "+rightBound +" and our Leftbound is " + leftBound);
                       readArrayList.add(listOfReadLists.get((index+startIndex)%gLength).get(0));
                       levelArrayList.add(level);
                       //System.out.println("added something: " + readArrayList.get(readArrayList.size()-1).getName() + "at level" + levelArrayList.get(levelArrayList.size()-1));
                       int tempLength = listOfReadLists.get((index+startIndex)%gLength).get(0).getAlignmentLength();

                       int tempIndex =listOfReadLists.get((index+startIndex)%gLength).get(0).getAlignmentEnd()-1;
                       listOfReadLists.get((index+startIndex)%gLength).remove(0);
                       //index+=tempIndex;
                       index+=tempLength;
                       //System.out.println("first read in current level placed");
                       //System.out.println("our Index now changed to: " + index);
                       if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                           //System.out.println("we are going to increase the level: current level before incrementation: " + level + "index before level increase: " + index);
                           level++;
                           firstReadInLevelSet=false;
                           index = 0;

                           leftBound=0; //Possibly not needed | Because it gets set at firstRead anyway


                       }



                   }
                   else if(index == gLength-1&&listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ // - if we aren´t able to place a read in the current level, and haven´t already placed one  we can only conclude that there is nothing left.
                       System.out.println("we are done, all Distributed is set");
                       System.out.println(firstplaced);
                       allDistributed=true;
                       readArrayOfSecondLevelCreation= readArrayList.toArray(new Read[readArrayList.size()]);
                       break;

                   }
                   else { //  - we didnt find a read at the current Position, we increment the index and move along
                       //System.out.println("We didnt find a read at the current position we increment the index and move along, index before incrementation: "+index);
                       index++;

                   }
               }
               else { //- if we have placed a first Read in the current Level, we can check at the next possible locations if there are any entries in the Array
                   if (!listOfReadLists.get((index+startIndex)%gLength).isEmpty()){ //- there are entries: so we first check if the last entry in the list would fit.

                       if (listOfReadLists.get((index+startIndex)%gLength).get(listOfReadLists.get((index+startIndex)%gLength).size()-1).getAlignmentLength()>((gLength-index)+leftBound)){ //  - If it doesnt we can assume that everything else in that list doesnt fit aswell. So we increment the index and move along
                           //System.out.println("already placed something, but didnt find another fitting read at the current position, we increment the index and keep looking");
                           index++;
                       }
                       else { // - If it does fit we can now traverse the sublist in order till we find the longest fitting read.
                           //System.out.println("we found a read that fits so:");
                           //System.out.println(listOfReadLists.get(index));
                           //System.out.println(listOfReadLists.get((index+startIndex)%gLength).size());
                           for (int j = 0; j < listOfReadLists.get((index+startIndex)%gLength).size();j++){
                               if (listOfReadLists.get((index+startIndex)%gLength).get(j).getAlignmentLength()<=((gLength-index+leftBound))){
                                   //insert add-procedure here
                                   placed++;
                                   readArrayList.add(listOfReadLists.get((index+startIndex)%gLength).get(j));
                                   levelArrayList.add(level);
                                   int tempLength = listOfReadLists.get((index+startIndex)%gLength).get(j).getAlignmentLength();
                                   int tempIndex = listOfReadLists.get((index+startIndex)%gLength).get(j).getAlignmentEnd()-1;

                                   listOfReadLists.get((index+startIndex)%gLength).remove(j);
                                   index += tempLength;
                                   //index = tempIndex;
                                   //System.out.println("we are gonna add something, this isnt the first in this level, index now "+ index + " current percentage of distributed reads is: " + ((double)readArrayList.size()/(double)readcount));
                                   if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                                       //System.out.println("we are going to increase the level: currentl level before incrementation: " + level);
                                       level++;
                                       
                                       firstReadInLevelSet=false;
                                       index = 0;
                                       leftBound=0;

                                       //Possibly not needed | Because it gets set at firstRead anyway

                                   }


                               }
                           }
                       }

                   }
                   else { //- there are no entries:
                       if(index>=gLength-1){  //- we no longer can increase the index: SO we simply add another ring(level), reset the variables so that we are in a new level and contiue working till every Read is distributed
                           //System.out.println("we are going to increase the level: currentl level before incrementation: " + level);
                           level++;
                           firstReadInLevelSet=false;

                           index = 0;
                           leftBound=0; //Possibly not needed | Because it gets set at firstRead anyway
                       }
                       else { // - but we still have indexes to traveL: so we increment the index and move along
                           //System.out.println("there are no entries, but we still have indexes to travel: current index (" + index+ ")");
                           index++;
                       }

                   }


               }


            }

        }
        System.out.println(levelArrayList.toString());
        System.out.println(readcount);
        timeAfter = System.currentTimeMillis();
        System.out.println("The creation of the level-array took " + (timeAfter-timeBefore) + "milliseconds");
        return levelArrayList.toArray(new Integer[levelArrayList.size()]);

    }
    public void enableCacheOfReadViews(){
        for(int i = 0; i < readViews.length; i++){
            readViews[i].getArchSegment().getInner().setCache(true);
        }
    }
    public void disableCacheOfReadViews(){
        for(int i = 0; i < readViews.length; i++){
            readViews[i].getArchSegment().getInner().setCache(false);
        }
    }
    public void cacheToQuality(){
        for(ReadView rW: readViews){
            rW.getArchSegment().getInner().setCacheHint(CacheHint.QUALITY);
        }
    }
    public void cacheToSpeed(){
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
        System.out.println(printText.length);


    }
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
                    if(readArrayOfSecondLevelCreation[i].isCrossBorder()) {
                        onecrossborderperlevel= true;
                    }
                    else{
                        onecrossborderperlevel=false;
                    }
                }
                else{
                    if(readArrayOfSecondLevelCreation[i].isCrossBorder()) correctPlacement[currentLevel] = false;
                }
            }
            if(!onecrossborderperlevel){
                if(levelArrayAsInteger[i]!=currentLevel){
                    currentLevel++;
                    if(readArrayOfSecondLevelCreation[i].isCrossBorder()){
                        onecrossborderperlevel = true;

                    }
                    else{
                        onecrossborderperlevel=false;
                    }
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
            reallength=(int)info.getGlobalLength()-start+end;
            //if(r.isCrossBorder()) System.out.println("Crossborder starting at:" + start+ " length of: "+length+ " ending at: "+ end + " Math: referenceLength -alignmentStart + alignmentEnd = alignmentLength: ("+ (int)info.getGlobalLength() + "-"+start +")+"+end+ "="+reallength+" "+ " !=" + length);
            //if(!r.isCrossBorder()) System.out.println("Read starting at: "+ start+" length of: "+ length +" end at: "+end);
        }
    }
    private void printGivenReadList(ObservableList< List< Read > >  listOfReadLists){
        for(List<Read> readList:listOfReadLists){
            String temp= "";
            if(!readList.isEmpty()){
                for(Read read:readList){
                    temp+=" s: " +read.getAlignmentStart() + " l: "+read.getAlignmentLength() + " e: " + read.getAlignmentEnd();
                }
                System.out.println(temp);
            }

        }
    }
    private ObservableList< List< Read > > deleteWeirdReads(ObservableList< List< Read > >  listOfReadLists){
        for(List<Read> list:listOfReadLists) {
            for (Read read : list) {
                if(Objects.equals(read.getSequence(), "*")) read.setAlignmentLength(read.getAlignmentEnd()-read.getAlignmentStart());
            }
        }
        return listOfReadLists;
    }



}
