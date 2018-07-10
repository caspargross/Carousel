import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.model.CircularParser;
import main.view.MainView;

import java.io.File;

public class ProperMainViewTest extends Application{
    @Override
    public void start( Stage primaryStage ) throws Exception {
        MainView mainView = new MainView();
        primaryStage.setTitle("Proper Test of the MainView Class");
        primaryStage.setScene( new Scene( mainView,mainView.getWidth(),mainView.getHeight()));
        primaryStage.show( );
        mainView.setOnMousePressed((me) ->{
            try{
                if(me.isSecondaryButtonDown())parseBam();
            }catch(Exception ex){
                System.out.println("invalid BAM file");
            }
        });
    }
    //Change here if you want to parse with reference + bai + bam or a different file genereally
    public void parseBam()throws  Exception{
        final File onlyBAMFileToTest = new File("./data/03_plB.bam");
        CircularParser.parse(onlyBAMFileToTest);
    }

    public static void main( String[] args ) {
        launch(args);

    }

}
