package playground.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.model.Read;
import main.view.CircularView;
import main.view.GlobalInformation;
import main.view.ReadView;
import playground.Coordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class tests the CircularView classes and subclasses aswell as the combination with the CircularParser class
 * NOTE: currently the method for creating a Level-Array is highly primitve, this is simply a proof of concept
 * @author Felix
 */
public class ReadsToCircleTest extends Application    {

    /**
     * The Bamfile to test
     */
    private final File BAMFileToTest = new File("C:/Users/Felix/Desktop/ProgrammierPraktikum2018_Gruppe3/data/01_plB.bam");
    /**
     * The hashmap to be used of the circular parser
     */
    private static HashMap< String, ArrayList< Read > > readMap;
    /**
     * the Arraylist containing Arraylist (hashmap -> arraylistList convertion (contains all duplicates)
     */
    private List<ArrayList<Read>> readArrayListList = new ArrayList<>();
    /**
     * the Arraylist containg only single reads and no duplicates, same as the array @author decided to not-use it out of convenience
     */
    private List<Read> readArrayList = new ArrayList<>();
    /**
     * Array of Reads, contains only single reads and no duplicates, same as the arrayList @author choose it out of convenience
     */
    private Read[] readArray;

    /**
     * Uses the TestFile in the circularParser class, generates a globalInformation out of the referenceeLength, creates a CircularView and later displays the elements of the CircularView on a new Scene
     * @param primaryStage
     * @throws Exception
     */

    @Override
    public void start( Stage primaryStage ) throws Exception {

        CircularParser.parseBAMFile(BAMFileToTest);
        readMap = CircularParser.getReadMap();
        readArrayListList.addAll(readMap.values());
        readArray = new Read[readArrayListList.size()];
        for(int index = 0; index <readArrayListList.size();index++){
            readArrayList.add(readArrayListList.get(index).get(0));
            readArray[index] = readArrayListList.get(index).get(0);
        }

        Pane myPane = new Pane( );

        Coordinate center = new Coordinate(900,500);//TODO: looks funny if height = 1
        GlobalInformation gInfo = new GlobalInformation(center,100,3,CircularParser.getReferenceLength(BAMFileToTest));


        CircularView demo = new CircularView(readArray,gInfo);
        ReadView[] temp = demo.getReadViews();
        for(int i = 0; i < demo.getReadViews().length;i++){
            myPane.getChildren().addAll(temp[i].getArchSegment().getInner(),temp[i].getArchSegment().getOuter(),temp[i].getArchSegment().getStart(),temp[i].getArchSegment().getStop());
        }
        primaryStage.setTitle( "Reads in Circular Display" );
        primaryStage.setScene( new Scene( myPane, 1800, 1000 ) );
        primaryStage.show( );
        /*
        for(int i = 0; i < readArray.length;i++){
            if(readArray[i].isCircular()){
                System.out.println("circular, length; " + readArray[i].getAlignmentLength()+" start; "+readArray[i].getAlignmentStart()+" end: "+ readArray[i].getAlignmentEnd());
            }
            System.out.println(demo.getLevelArray()[i]);
        }*/


    }

    /**
     * The main procedure of the test.
     *
     * @param args unused parameter
     */

    public static void main( String[] args ) {
        launch( args );
    }

}


