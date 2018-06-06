package player;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
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

        Path path = Paths.get("/home/racso/Odessey/VideoPlayer/Madura.mp4");
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

        //Se crea el media player
        Media media = new Media("file:///home/racso/Odessey/VideoPlayer/Temporal.mp4");
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);
        root.getChildren().add(view);
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

                stage.setMinWidth(w);
                stage.setMinHeight(h);
            }
        });


    }
}

