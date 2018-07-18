

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.view.MainView;

import java.io.File;

public class ProperMainViewTest extends Application{
    boolean parsed = false;
    @Override
    public void start( Stage primaryStage ) throws Exception {
        MainView mainView = new MainView();
        primaryStage.setTitle("Proper Test of the MainView Class");
        primaryStage.setScene( new Scene( mainView,mainView.getWidth(),mainView.getHeight(),true,SceneAntialiasing.DISABLED));
        primaryStage.show( );
        mainView.setOnMousePressed((me) ->{
            try{
                if(me.isSecondaryButtonDown())parseBam();
            }catch(Exception ex){
                System.out.println(ex.getMessage());
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
