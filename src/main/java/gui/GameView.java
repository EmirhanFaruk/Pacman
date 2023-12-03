package gui;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import geometry.IntCoordinates;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import model.MazeState;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class GameView {
    // class parameters
	private Rectangle backgroundRect;
	private Text pauseText;
    public final MazeState maze;
    private final Pane gameRoot; // main node of the game
    private final List<GraphicsUpdater> graphicsUpdaters;
    private boolean isPaused = false;
    
    private void addGraphics(GraphicsUpdater updater) {
        gameRoot.getChildren().add(updater.getNode());
        graphicsUpdaters.add(updater);
    }

    /**
     * @param maze  le "modèle" de cette vue (le labyrinthe et tout ce qui s'y trouve)
     * @param root  le nœud racine dans la scène JavaFX dans lequel le jeu sera affiché
     * @param scale le nombre de pixels représentant une unité du labyrinthe
     * @param pauseTextX la position horizontale du texte de pause
     * @param pauseTextY la position verticale du texte de pause
     */
    public GameView(MazeState maze, Pane root, double scale, double pauseTextX, double pauseTextY) {
        this.maze = maze;
        this.gameRoot = root;
        // pixels per cell
        root.setStyle("-fx-background-color: #000000");
        var critterFactory = new CritterGraphicsFactory(scale);
        var cellFactory = new CellGraphicsFactory(scale);
        var BonusFactory = new BonusGraphics(scale) ;
        var affichageScore = new Score();
        var afficheVie = new Vie();
        graphicsUpdaters = new ArrayList<>();
        
        for (var critter : maze.getCritters()) addGraphics(critterFactory.makeGraphics(critter));
        for (int x = 0; x < maze.getWidth(); x++)
        for (int y = 0; y < maze.getHeight(); y++)
        	addGraphics(cellFactory.makeGraphics(maze, new IntCoordinates(x, y)));
            addGraphics(affichageScore.displayScore(root));
            addGraphics(afficheVie.remainingLife());
		    addGraphics(BonusFactory.afficheBonus());
		
		// Créer le texte de pause
		pauseText = new Text("Pause");
		pauseText.setFont(Font.font(200));
	    pauseText.setFill(Color.rgb(255, 165, 0));
		
	    // Créer le background du texte
	     backgroundRect = new Rectangle(maze.getWidth() * 40.0, 200); 
	     backgroundRect.setFill(Color.rgb(15, 5, 107, 0.9)); // Définir la couleur du fond avec une opacité de 50%

		// Rends le texte invisible par défaut
	    pauseText.setVisible(false);
		backgroundRect.setVisible(false);
	    
	    //Centre le texte + background
	    StackPane centerPane = new StackPane();
	    centerPane.getChildren().addAll(backgroundRect, pauseText);
	    StackPane.setAlignment(pauseText, Pos.CENTER);
	    centerPane.setLayoutX(pauseTextX);
        centerPane.setLayoutY(pauseTextY);

	    // Ajoute le StackPane au root
	    root.getChildren().add(centerPane);      
    }

    public void animate() {
        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                    if (last == 0) {
                        last = now;
                        return;
                    }
                    var deltaT = now - last;
                    maze.update(deltaT, isPaused);
                    for (var updater : graphicsUpdaters) {
                        updater.update();
                    }
                    last = now;
            }
        }.start();
    }
    
    public void togglePause() {
        isPaused = !isPaused;
        // Afficher ou masquer le texte de pause en fonction de l'état de pause
        pauseText.setVisible(isPaused);
        backgroundRect.setVisible(isPaused);
    }
    
}
