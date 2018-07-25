import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.view.MiddlePane;

import java.io.File;

public class ProperMainViewTest extends Application{
    boolean parsed = false;
    @Override
    public void start( Stage primaryStage ) throws Exception {
        MiddlePane mainView = new MiddlePane();
        primaryStage.setTitle("Proper Test of the MainView Class");
        Scene cacheTest = new Scene( mainView,mainView.getWidth(),mainView.getHeight());
        primaryStage.setScene(cacheTest);
        primaryStage.show( );
        mainView.setOnMousePressed((me) ->{
            try{
                if(me.isSecondaryButtonDown())parseBam();
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        cacheTest.setOnKeyPressed(ke->{

            KeyCode keyCode = ke.getCode();
            System.out.println("key was pressed: "+ keyCode);
            if(keyCode.equals(KeyCode.S)){
                System.out.println("setting cache to speed");
                mainView.CacheTempSpeed();
            }
            if(keyCode.equals(KeyCode.Q)){
                System.out.println("setting cache to quality");
                mainView.CacheTempQuality();
            }
            if(keyCode.equals(KeyCode.E)){
                mainView.EnableCache();
            }
        });


    }
    //Change here if you want to parse with reference + bai + bam or a different file genereally
    public void parseBam()throws  Exception{
        final File referenceSequencesFileToTest = new File( "./data/p7_ref.fasta" ),
                   BAMFileToTest = new File( "./data/p7_mapped.bam" ),
                   BAIFileToTest = new File( "./data/p7_mapped.bai" );
        if(!parsed)CircularParser.parse( referenceSequencesFileToTest, BAMFileToTest, BAIFileToTest );parsed = true;
    }

    public static void main( String[] args ) {
        launch(args);

    }

}
