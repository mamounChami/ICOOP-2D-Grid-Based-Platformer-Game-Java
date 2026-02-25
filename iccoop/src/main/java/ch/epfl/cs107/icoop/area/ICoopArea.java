package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Window;

public abstract class ICoopArea extends Area implements Logic {
	public final static float DEFAULT_SCALE_FACTOR = 13.f;
	private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
	private DialogHandler dialogHandler;
	private Logic signal;
    /**
     * Area specific callback to initialise the instance
     */
    protected abstract void createArea();

    /**
     * @return (DiscreteCoordinates) : player1's spawn position in the area
     */
    public abstract DiscreteCoordinates getPlayer1SpawnPosition();
    /**
     * @return (DiscreteCoordinates) : player2's spawn position in the area
     */
    public abstract DiscreteCoordinates getPlayer2SpawnPosition();
    
    /**
     * @return (DialogHandler) : Le gestionnaire de dialogue de l'aire.
     */
	public DialogHandler getDialogHandler() {
		return dialogHandler;
	}
	/**
	 * Constructeur d'une ICoopArea
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues de l'aire.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public ICoopArea(DialogHandler dialogHandler, Logic signal) {
		super();
		this.dialogHandler = dialogHandler;
		this.signal = signal;
	}
    /**
     * Callback to initialise the instance of the area
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the area is instantiated correctly, false otherwise
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICoopBehavior(window, getTitle(), this));
            createArea();
            return true;
        }
        return false;
    }
    /**
	 * Getter for ICoop's scale factor
	 * @return Scale factor in both the x-direction and the y-direction
	 */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }
    /**
	 * Setter for ICoop's scale factor
	 * @return Scale factor in both the x-direction and the y-direction
	 */
    
    public final void setCameraScaleFactor(Vector position_playerA, Vector position_playerB) {
        cameraScaleFactor = Math.max(DEFAULT_SCALE_FACTOR, DEFAULT_SCALE_FACTOR*0.75f + (position_playerA.sub(position_playerB).getLength()) / 2);
    }
    /**
     * La caméra doit-elle être centrée sur l'aire ?
     * @return (boolean) : true inconditionnellement.
     */
    @Override
    public boolean isViewCentered() {
    	return true;
    }
    /**
	 * Assigner un nouveau signal(Logic) à ICoopArea.
	 * @param signal (Logic) : Le signal à assigner.
	 */
    protected void setSignal(Logic signal) {
    	this.signal = signal;
    }
    /** 
     * @return (Logic) : Le signal logique associé à ICoopArea.
     */
    protected Logic getSignal() {
    	return signal;
    }
    /**
     * L'ICoopArea est-elle résolue ?
     * @return (boolean) : true si l'aire est résolue, false sinon.
     */
    @Override
    public boolean isOn() {
    	return signal.isOn();
    }
    /**
     * L'ICoopArea est-elle non résolue ?
     * @return (boolean) : true si l'aire n'est pas résolue, false sinon.
     */
    @Override
	public boolean isOff() {
		return signal.isOff();
	}
}
