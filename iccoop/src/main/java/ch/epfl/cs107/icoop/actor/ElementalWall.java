package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Mur élémentaire bloquant le passage aux entités dont l'élément diffère de celui du mur. 
 * Inflige des dégâts correspondants à son élément.
 * Peut-être actif ou non en fonction d'un signal Logic.
 * Peut-être détruit par une bombe(Explosive).
 */
public class ElementalWall extends AreaEntity implements ElementalEntity, Interactor {
	private ElementalType element;
	private Logic signal;
	private Sprite[] sprites;
	/**
	 * Constructeur pour un mur élémentaire(ElementalWall). 
	 * @param owner       (Area)                : Aire à laquelle le mur appartient.
	 * @param orientation (Orientation)         : Orientation du mur. 
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le mur doit occuper.
	 * @param element     (ElementalType)       : Type élémentaire auquel le mur appartient.
	 * @param signal      (Logic)               : Signal logique qui régit le status actif ou inactif du mur.
	 * @param spriteName  (String)              : Chaîne de caractères identifiant la ressource pour la construction du Sprite du mur.
	 */
	public ElementalWall(Area owner, Orientation orientation, DiscreteCoordinates position, ElementalType element, Logic signal, String spriteName) {
		super(owner, orientation, position);
		this.element = element;
		this.signal = signal;
		this.sprites = RPGSprite.extractSprites(spriteName, 4, 1, 1, this, Vector.ZERO, 256, 256); 
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
    @Override
    public void draw(Canvas canvas) {
    	if(signal.isOn())
    		sprites[getOrientation().ordinal()].draw(canvas);
    }
	@Override
	public ElementalType element() {
		return element;
	}
	// L'ElementalWall implémente Interactable.
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que l'ElementWall occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * L'ElementalWall prend-il place dans une cellule ?
	 * @return (boolean) : true s'il est actif, false sinon.
	 */
	@Override
	public boolean takeCellSpace() {
		return signal.isOn();
	}	
	// L'ElementalWall implémente Interactor.
	/**
	 * L'ElementalWall accepte-t-il les interactions de contact ?
	 * @return (boolean) : true s'il est actif, false sinon.
	 */
	@Override
	public boolean isCellInteractable() {
		return signal.isOn();
	}
	/**
	 * L'ElementalWall accepte-t-il les interactions à distance ?
	 * @return (boolean) : true s'il est actif, false sinon.
	 */
	@Override
	public boolean isViewInteractable() {
		return signal.isOn();
	}
	/**
	 * L'ElementalWall accepte les interactions.
	*/
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	// L'ElementalWall implémente Interactor.
	/**
	 * Le champ de vision de l'ElementalWall ne comprend aucune cellule.
	 * @return (List<DiscreteCoordinates) : Liste immuable vide.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.<DiscreteCoordinates> emptyList();
	}
	/**
	 * L'ElementalWall est-il demandeur d'interactions de contact ?
	 * @return (boolean) : true s'il est actif, false sinon.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return signal.isOn();
	}
	/**
	 * L'ElementalWall est-il demandeur d'interactions à distance ?
	 * @return (boolean) : false incoditionnellement.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Tentative d'attaque élémentaire(Vulnerability.FIRE/WATER) lors d'une interaction de contact si l'ElementalWall est actif.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			other.hit(elementToVulnerability(), 1);
		}
	};
	/**
	 * Redirige la gestion des interactions de l'ElementalWall à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
	
}
