import javafx.stage.Stage;
import player.MoviePlayer;
import player.MusicPlayer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");


        /**
         * Simulacion de entrada al metodo
         */

        Path path = Paths.get("/home/racso/Odessey/VideoPlayer/PorSiempre.mp3"); //cambiar el path de la cancion
        byte[] data = Files.readAllBytes(path);
        String encoded = Base64.getEncoder().encodeToString(data);
        //System.out.println(data.toString());

        //MoviePlayer player = new MoviePlayer();
        //player.reproducir_mp4(encoded);

        MusicPlayer player = new MusicPlayer();
        player.reproducir_mp3(encoded);
    }
}
