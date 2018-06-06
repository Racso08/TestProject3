package player;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.Time;
import java.util.Base64;


public class MoviePlayer extends Application {
    public static void main (String[] args) {
        launch(args);
    }
    @Override
    public void start (Stage stage) throws Exception{
        stage.setTitle("Odessey Movie Player");
        Group root = new Group();

        /**
         * Simulacion del string que va a recibir del servidor
         */

        Path path = Paths.get("/home/racso/Odessey/VideoPlayer/Dimelo.mp4"); //cambiar el path de la cancion
        byte[] data = Files.readAllBytes(path);
        String encoded = Base64.getEncoder().encodeToString(data);
        //System.out.println(data.toString());

        /**
         * Inicio del metodo de reproducion apartir del string
         */

        //Se descodifica el string
        byte[] decodedBytes = Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8));

        //Se crea un archivo temporal para la reproduccion del video
        FileOutputStream stream = new FileOutputStream("/home/racso/Odessey/VideoPlayer/Temporal.mp4");
        stream.write(decodedBytes);
        stream.close();

        //Se crea el media player con diversos componentes
        Media media = new Media("file:///home/racso/Odessey/VideoPlayer/Temporal.mp4");
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();
        root.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slideOut.play();
            }
        });
        root.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                slideIn.play();
            }
        });
        final VBox vbox = new VBox();
        Slider slider = new Slider();
        vbox.getChildren().add(slider);

        final HBox hbox = new HBox(2);
        final int bands = player.getAudioSpectrumNumBands();
        final Rectangle[] rects = new Rectangle[bands];
        for (int i = 0; i<rects.length;i++){
            rects[i] = new Rectangle();
            rects[i].setFill(Color.LIGHTGRAY);
            hbox.getChildren().add(rects[i]);

        }
        vbox.getChildren().add(hbox);
        root.getChildren().add(view);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root,400,400,Color.BLACK);
        stage.setScene(scene);

        //Se elimina el archivo temporal (Temporal.mp4)
        File file = new File("/home/racso/Odessey/VideoPlayer/Temporal.mp4");
        file.delete();

        stage.show();

        player.play();
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                int w = player.getMedia().getWidth();
                int h = player.getMedia().getHeight();

                hbox.setMinWidth(w);
                int bandWidth = w/rects.length;
                for(Rectangle r:rects){
                    r.setWidth(bandWidth);
                    r.setHeight(2);
                }

                stage.setMinWidth(w);
                stage.setMinHeight(h);

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

            }
        });
        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration duration, Duration current) {
                slider.setValue(current.toSeconds());
            }
        });
        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                player.seek(Duration.seconds(slider.getValue()));

            }
        });
        player.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                for(int i = 0; i<rects.length; i++){
                    double h = magnitudes[i]+60;
                    if(h > 2) {
                        rects[i].setHeight(h);
                    }
                }
            }
        });
    }
}

