package ch.epfl.cs107.icoop;

import static ch.epfl.cs107.play.window.Keyboard.*;

/**
 * Interface KeyboardConfig
 * Definition of movement keys for both players, as well as other global actions in the game.
 */
public final class KeyBindings {

    /**
     * Keys used for the red player.
     */
    public static final PlayerKeyBindings RED_PLAYER_KEY_BINDINGS = new PlayerKeyBindings(Z, Q, S, D, A, E, Y);

    /**
     * Touches utilisées pour le joueur bleu.
     */
    public static final PlayerKeyBindings BLUE_PLAYER_KEY_BINDINGS = new PlayerKeyBindings(I, J, K, L, U, O, H);
    /**
     * Key to move on to the next dialogue.
     */
    public static final int NEXT_DIALOG = SPACE;
    /**
     * Key to reset the game.j
     */
    public static final int RESET_GAME = B;
    /**
     * Key to reset the area
     */
    public static final int RESET_AREA = V;
    
    private KeyBindings() {

    }

    /**
     * Keys used by the player
     *
     * @param up         Pour le déplacement vers le haut
     * @param left       Pour le déplacement vers la gauche
     * @param down       Pour le déplacement vers le bas
     * @param right      Pour le déplacement vers la droite
     * @param switchItem Pour changer d'objet en main
     * @param useItem    Pour utiliser l'objet en main
     * @param sayYes     Pour dire oui à un personnage
     */
    public record PlayerKeyBindings(int up, int left, int down, int right, int switchItem, int useItem, int sayYes) {
    }
}
