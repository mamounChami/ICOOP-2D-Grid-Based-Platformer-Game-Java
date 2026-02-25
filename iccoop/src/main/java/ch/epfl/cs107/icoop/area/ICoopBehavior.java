package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.ElementalEntity;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.actor.Unstoppable;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public final class ICoopBehavior extends AreaBehavior {
    /**
     * Default ICoopBehavior Constructor
     *
     * @param window (Window), not null
     * @param name   (String): Name of the Behavior, not null
     */
    public ICoopBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICoopCellType color = ICoopCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICoopCell(x, y, color));
            }
        }
    }
    /**
     * Constructeur optionnel auquel on passe une aire sur laquelle on veut poser des entités en fonction de la behavior_map.
     * @param area (Area) : Aire dans laquelle rajouter des entités.
     */
    public ICoopBehavior(Window window, String name, Area area) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICoopCellType color = ICoopCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICoopCell(x, y, color));
                if(color == ICoopCellType.ROCK) // Selon le type de cellule, on rajoute des entités Rock ou Obstacle à l'aire.
                	area.registerActor(new Rock(area, Orientation.LEFT, new DiscreteCoordinates(x, y)));
                else if(color == ICoopCellType.OBSTACLE)
                	area.registerActor(new Obstacle(area, Orientation.LEFT, new DiscreteCoordinates(x, y)));
            }
        }
    }
    public enum ICoopCellType {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NULL(0, false, false),
        WALL(-16777216, false, false),
        IMPASSABLE(-14112955/*-8750470*/, false, true),
        INTERACT(-256, true, true),
        DOOR(-195580, true, true),
        WALKABLE(-1, true, true),
        ROCK(-16777204, true, true),
        OBSTACLE(-16723187, true, true)
        ;

        final int type;
        final boolean isWalkable;
		final boolean canFly;

        ICoopCellType(int type, boolean isWalkable, boolean canFly) {
            this.type = type;
            this.isWalkable = isWalkable;
            this.canFly = canFly;
        }

        public static ICoopCellType toType(int type) {
            for (ICoopCellType ict : ICoopCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NULL;
        }
    }

    /**
     * Cell adapted to the ICoop game
     */
    public class ICoopCell extends Cell {
        /// Type of the cell following the enum
        private final ICoopCellType type;

        /**
         * Default ICoopCell Constructor
         *
         * @param x    (int): x coordinate of the cell
         * @param y    (int): y coordinate of the cell
         * @param type (EnigmeCellType), not null
         */
        public ICoopCell(int x, int y, ICoopCellType type) {
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
        	if(!type.isWalkable && !type.canFly) return false; // On ne peut ni marcher ni voler sur cette cellule, elle ne peut donc être occupée. 
        	if(entity instanceof Unstoppable) return true; // Un projectile peut passer sur n'importe quelle cellule du moment qu'on peut marcher ou voler dessus.
        	if(type.isWalkable) { // Toutes les autres entités ne peuvent que marcher sur une cellule.
        		if(entity.takeCellSpace()) {
        			if(entity instanceof ElementalEntity) // Si on est une entité élémentaire, alors on doit vérifier si les entités déjà installées le sont aussi.
        				for(Interactable e : entities) {
        					if(e.takeCellSpace()) { // Cette entité n'accepte pas qu'on lui marche dessus, mais est-elle élémentaire ?
        						if(e instanceof ElementalEntity) { // Si elle l'est,
        							if(((ElementalEntity)e).element() != ((ElementalEntity)entity).element()) // et qu'elle n'a pas le même élément que nous,
        								return false; // alors on ne peut pas entrer.
        						}
        						else return false; // Ce n'est pas une entité élémentaire alors on honore son refus de se faire marcher dessus.
        					}
			        	}
        			else // Cas le plus simple : si une seule entité n'accepte pas qu'on lui marche dessus, alors on ne peut pas entrer.
			        	for(Interactable e : entities) { 
			        		if(e.takeCellSpace())
			        			return false;
			        	}
        		}
        		return true; // Les entités qui acceptent qu'on leur marche dessus peuvent entrer sans problème.
        	}
            return false; // L'entité n'est pas un projectile et on ne peut que voler sur cette cellule, donc l'entité ne peut pas entrer.
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return true;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        	((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
        }

    }
}
