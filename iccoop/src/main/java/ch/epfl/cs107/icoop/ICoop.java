package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.area.Spawn;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.List;

import ch.epfl.cs107.icoop.actor.CenterOfMass;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalEntity.ElementalType;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.Arena;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.Maze;
import ch.epfl.cs107.icoop.area.OrbWay;
import ch.epfl.cs107.icoop.area.Sanctum;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public final class ICoop extends AreaGame{
	private final String[] areas = {"Spawn", "OrbWay", "Maze", "Arena", "Sanctum"};
	private ICoopPlayer[] players = new ICoopPlayer[2];
	private int areaIndex;
	private CenterOfMass cameraTarget;
	private Dialog currentDialog = null;
	private DialogHandler dialogHandler = new DialogHandler() {
		@Override
		/**
		 * Publier une boîte de dialogue.
		 * @param dialog (Dialog) : Nouvelle boîte de dialogue à assigner.
		 */
		public void publish(Dialog dialog) {
			setDialog(dialog);
		}
	};
	private void createAreas() {
		Maze maze = new Maze(dialogHandler, Logic.FALSE);
		Arena arena = new Arena(dialogHandler, Logic.FALSE);
		// Spawn n'est résolue que si Maze et Arena le sont.
		addArea(new Spawn(dialogHandler, new And(maze, arena)));
		addArea(new OrbWay(dialogHandler, Logic.TRUE));
		addArea(maze);
		addArea(arena);
		addArea(new Sanctum(dialogHandler, Logic.FALSE));
	}
	/**
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0;
            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }
    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
    	Keyboard keyboard = getWindow().getKeyboard();
    	if(currentDialog == null) { // S'il n'y a pas de boîte de dialogue en cours.
    		if(keyboard.get(KeyBindings.RESET_GAME).isPressed()) { // La touche de réinitialisation du jeu a été pressée.
        		begin(getWindow(), getFileSystem()); // On recrée le jeu.
        	}
        	else if(keyboard.get(KeyBindings.RESET_AREA).isPressed() || players[0].isOff() || players[1].isOff()) { // La touche de réinitialisation de l'aire a été pressée ou un joueur est mort.
        		ICoopArea area = (ICoopArea)getCurrentArea();
                players[0].leaveArea();
                players[1].leaveArea();
                players[0].resetHealth();
                players[1].resetHealth();
                players[0].resetImmunity();
                players[1].resetImmunity();
        		area.begin(getWindow(), getFileSystem());
                DiscreteCoordinates coords1 = area.getPlayer1SpawnPosition();
                DiscreteCoordinates coords2 = area.getPlayer2SpawnPosition();
                players[0].enterArea(area, coords1);
                players[1].enterArea(area, coords2);
                getCurrentArea().setViewCandidate(cameraTarget);
        	}
        	else {
    			for(ICoopPlayer p: players) { // On vérifie si un joueur souhaite transiter par une porte.
    	    		if(p.wantsToLeave()) {
    	    			switchArea(p.getDoor());
    	    			break;
    	    		}
    	    	}
        	}
    		super.update(deltaTime);
    		
    	}
    	else if(keyboard.get(KeyBindings.NEXT_DIALOG).isPressed()) {
    		currentDialog.update(deltaTime);
    		if(currentDialog.isCompleted()) // Une fois le dialogue complété, on l'oublie.
    			currentDialog = null;
		} 
    }
    @Override
    public void draw() {
    	super.draw();
    	if(currentDialog != null)
    		currentDialog.draw(getWindow());
    }
    @Override
    public void end() {

    }
    /**
     * sets the area named `areaKey` as current area in the game ICoop
     * @param areaKey (String) title of an area
     */
    private void initArea(String areaKey) {
        ICoopArea area = (ICoopArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords1 = area.getPlayer1SpawnPosition();
        DiscreteCoordinates coords2 = area.getPlayer2SpawnPosition();
        players[0] = new ICoopPlayer(area, Orientation.DOWN, coords1, "icoop/player", KeyBindings.RED_PLAYER_KEY_BINDINGS, ElementalType.FIRE);
        players[1] = new ICoopPlayer(area, Orientation.DOWN, coords2, "icoop/player2", KeyBindings.BLUE_PLAYER_KEY_BINDINGS, ElementalType.WATER);
        players[0].enterArea(area, coords1);
        players[1].enterArea(area, coords2);
        cameraTarget = new CenterOfMass(players[0], players[1]);
        getCurrentArea().setViewCandidate(cameraTarget);
    }
    /**
     * switches from one area to the other
     * @param door (Door) : Porte par laquelle on veut transiter.
     */
    private void switchArea(Door door) {
        players[0].leaveArea();
        players[1].leaveArea();
        ICoopArea currentArea = (ICoopArea) setCurrentArea(door.getDestination(), false);
        DiscreteCoordinates[] arrival = door.getArrival();
        players[0].enterArea(currentArea, arrival[0]);
        players[1].enterArea(currentArea, arrival[1]);
        currentArea.setViewCandidate(cameraTarget);
        currentArea.setCameraScaleFactor(players[0].getPosition(), players[1].getPosition());
    }
    /**
     * Assignateur privé pour la boîte de dialogue
     * @param dialog (Dialog) : Nouvelle boîte de dialogue à assigner.
     */
    public void setDialog(Dialog dialog) {
    	currentDialog = dialog;
    }
    
    @Override
    public String getTitle() {
        return "ICoop";
    }
}
