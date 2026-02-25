package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * A GUI that shows information about the player on the screen.
 */
public class ICoopPlayerStatusGUI implements Graphics {

    private final static int DEPTH = 2000;
    private final ICoopPlayer player;
    private final boolean flipped;

    public ICoopPlayerStatusGUI(ICoopPlayer player, boolean flipped) {
        this.player = player;
        this.flipped = flipped;
    }

    @Override
    public void draw(Canvas canvas) {
        // Compute width, height and anchor
        float width = canvas.getTransform().getX().getX();
        float height = canvas.getTransform().getY().getY();

        float ratio = canvas.getWidth() / (float) canvas.getHeight();
        if (ratio > 1)
            height = width / ratio;
        else
            width = height * ratio;

        Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(flipped ? (-width / 2 + 2) : width / 2, height / 2));

        //Draw selected gear
        ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/gearDisplay"), 1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(new Vector(0, height - 1.75f)), 1, DEPTH);
        gearDisplay.draw(canvas);
        // Dessiner l'objet en séléction.
        ImageGraphics itemDisplay = new ImageGraphics(ResourcePath.getSprite(player.getCurrentItem().sprite), 0.5f, 0.5f, new RegionOfInterest(0, 0, 16, 16), anchor.add(new Vector(0.5f, height - 1.25f)), 1, DEPTH);
        itemDisplay.draw(canvas);
        // Dessiner les pièces.
        ImageGraphics coinsDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/coinsDisplay"), 2.f, 1.f, new RegionOfInterest(0, 0, 64, 32), anchor.add(new Vector(flipped ? -2.f : 1.5f, height - 1.5f)), 1, DEPTH);
        coinsDisplay.draw(canvas);
        // Dessiner le nombre.
        RegionOfInterest[] rois = new RegionOfInterest[] {
        		new RegionOfInterest(16, 32, 16, 16), new RegionOfInterest( 0,  0, 16, 16),
        		new RegionOfInterest(16,  0, 16, 16), new RegionOfInterest(32,  0, 16, 16),
        		new RegionOfInterest(48,  0, 16, 16), new RegionOfInterest( 0, 16, 16, 16),
        		new RegionOfInterest(16, 16, 16, 16), new RegionOfInterest(32, 16, 16, 16),
        		new RegionOfInterest(48, 16, 16, 16), new RegionOfInterest( 0, 32, 16, 16)
        		};
        ImageGraphics digitsDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/digits"), 1.f, 0.75f, rois[player.getCoinCount()], anchor.add(new Vector(flipped ? -1.f : 2.5f, height - 1.35f)), 1, DEPTH);
        digitsDisplay.draw(canvas);
    }
}
