package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
/**
 * Marchand chez lequel un ICoopPlayer peut acheter des bombes.
 */
public class Merchant extends MovableAreaEntity implements Interactor {
	private static final int ANIMATION_DURATION = 4;
	private OrientedAnimation animation;
	private boolean hasGreeted = false;
	/**
	 * Constructeur pour un Merchant
	 * @param owner       (Area)                : Aire à laquelle le marchand appartient.
	 * @param orientation (Orientation)         : Orientation du marchand.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le marchand doit occuper.
	 */
	public Merchant(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);
		Orientation[] orders = {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT};
		animation = new OrientedAnimation("icoop/merchant", ANIMATION_DURATION, this, new Vector(0, 0), orders, 4, 1, 2, 16, 32);
	}
	@Override
	public void update(float deltaTime) {
		if(!isDisplacementOccurs())
			animation.reset();
		else
			animation.update(deltaTime);
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
		super.draw(canvas);
	}
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que le Merchant occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * Le Merchant prend-il place dans une cellule ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return true;
	}
	/**
	 * Le Merchant accepte-t-il les interactions de contact ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return false;
	}
	/**
	 * Le Merchant accepte-t-il les interactions à distance ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return true;
	}
	/**
	 * Le Merchant accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	/**
	 * Le champ de vision du Merchant se limite à la seule cellule directement en face de lui.
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de cellule du champ de vision du Merchant.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {		
		return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}
	/**
	 * Le Merchant est-il demandeur d'interactions de contact ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return false;
	}
	/**
	 * Le Merchant est-il demandeur d'interactions à distance ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return true;
	}
	/**
	 * Acheter une bombe chez le Merchant.
	 */
	public void buy() {
		getOwnerArea().registerActor(new Explosive(getOwnerArea(), Orientation.RIGHT, getFieldOfViewCells().get(0)));
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Saluer le joueur si on lui fait face pour la première fois lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			if(!hasGreeted) {
				((ICoopArea)getOwnerArea()).getDialogHandler().publish(new Dialog("merchant_greeting"));
				hasGreeted = true;
			}
		}
	};
	/**
	 * Redirige la gestion des interactions du Merchant à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);		
	}
}
