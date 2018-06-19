package player;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

public class MusicPlayer extends Application {
    public static void main (String[] args) {
        launch(args);
    }
    @Override
    public void start (Stage stage) throws Exception{
        stage.setTitle("Odessey Music Player");
        Group root = new Group();

        /**
         * Simulacion del string que va a recibir del servidor
         */

        Path path = Paths.get("/home/racso/Odessey/VideoPlayer/PorSiempre.mp3"); //cambiar el path de la cancion
        byte[] data = Files.readAllBytes(path);
        String encoded = Base64.getEncoder().encodeToString(data);
        //System.out.println(data.toString());

        /**
         * Inicio del metodo de reproducion apartir del string
         */

        //Se descodifica el string
        byte[] decodedBytes = Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8));

        //Se crea un archivo temporal para la reproduccion del video
        FileOutputStream stream = new FileOutputStream("/home/racso/Odessey/VideoPlayer/Temporal.mp3");
        stream.write(decodedBytes);
        stream.close();

        //Se crea el media player con diversos componentes
        Media media = new Media("file:///home/racso/Odessey/VideoPlayer/Temporal.mp3");
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();
        root.setOnMouseExited(event -> slideOut.play());
        root.setOnMouseEntered(event -> slideIn.play());
        final VBox vbox = new VBox();
        Slider slider = new Slider();


        Button btn_pause = new Button();
        btn_pause.setText("Pause");
        btn_pause.setTranslateX(300);
        btn_pause.setTranslateY(-20);
        btn_pause.setOnAction(event -> player.pause());

        Button btn_previous = new Button();
        btn_previous.setText("Previous");
        btn_previous.setTranslateX(0);
        btn_previous.setTranslateY(-20);
        btn_previous.setOnAction(event -> player.seek(player.getCurrentTime().add(new Duration(-20000))));

        Button btn_next = new Button();
        btn_next.setText("Next");
        btn_next.setTranslateX(310);
        btn_next.setTranslateY(-20);
        btn_next.setOnAction(event -> player.seek(player.getCurrentTime().add(new Duration(20000))));

        Button btn_stop = new Button();
        btn_stop.setText("Stop");
        btn_stop.setTranslateX(290);
        btn_stop.setTranslateY(-20);
        btn_stop.setOnAction(event -> player.stop());

        Button btn_play = new Button();
        btn_play.setText("Play");
        btn_play.setTranslateX(285);
        btn_play.setTranslateY(-20);
        btn_play.setOnAction(event -> player.play());

        final HBox hbox = new HBox(2);
        final HBox hbox2 = new HBox(2);
        final int bands = player.getAudioSpectrumNumBands();
        final Rectangle[] rects = new Rectangle[bands];
        Random rand = new Random();
        for (int i = 0; i<rects.length;i++){
            rects[i] = new Rectangle();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            rects[i].setFill(Color.color(r,g,b));
            hbox.getChildren().add(rects[i]);

        }
        hbox2.getChildren().add(btn_play);
        hbox2.getChildren().add(btn_stop);
        hbox2.getChildren().add(btn_pause);
        hbox2.getChildren().add(btn_next);
        hbox2.getChildren().add(btn_previous);
        vbox.getChildren().add(hbox2);
        vbox.getChildren().add(hbox);
        root.getChildren().add(view);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root,400,400,Color.BLACK);
        stage.setScene(scene);

        //Se elimina el archivo temporal (Temporal.mp4)
        File file = new File("/home/racso/Odessey/VideoPlayer/Temporal.mp3");
        file.delete();

        stage.show();

        player.play();
        player.setOnReady(() -> {

            int w =800;
            int h = 400;

            hbox.setMinWidth(w);
            hbox2.setMinWidth(w);
            int bandWidth = w/rects.length;
            for(Rectangle r:rects){
                r.setWidth(bandWidth);
                r.setHeight(2);
            }

            stage.setMinWidth(w);
            stage.setMinHeight(h+100);

            vbox.setMinSize(w,150);
            vbox.setTranslateY(h-100);

            slider.setMin(0.0);
            slider.setValue(0.0);
            slider.setMax(player.getTotalDuration().toSeconds());

            slideOut.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),
                            new KeyValue(vbox.translateYProperty(),h-100),
                            new KeyValue(vbox.opacityProperty(),0.9)
                    ),
                    new KeyFrame(new Duration(300),
                            new KeyValue(vbox.translateYProperty(),h),
                            new KeyValue(vbox.opacityProperty(),0.0)
                    )
            );


            slideIn.getKeyFrames().addAll(
                    new KeyFrame(new Duration(0),
                            new KeyValue(vbox.translateYProperty(),h),
                            new KeyValue(vbox.opacityProperty(),0.0)
                    ),
                    new KeyFrame(new Duration(300),
                            new KeyValue(vbox.translateYProperty(),h-100),
                            new KeyValue(vbox.opacityProperty(),0.9)
                    )
            );

        });
        player.currentTimeProperty().addListener((observable, duration, current) -> {
            //slider.setValue(current.toSeconds());
        });
        slider.setOnMouseClicked(event -> {
            //player.seek(Duration.seconds(slider.getValue()));

        });
        player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            for(int i = 0; i<rects.length; i++){
                double h = magnitudes[i]+60;
                if(h > 2) {
                    rects[i].setHeight(h);
                }
            }
        });
    }
}


