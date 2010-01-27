/**
 * 
 */
package com.cn.jme;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

/**
 * @author Michael
 *
 */
public class HelloJme extends SimpleGame {

	@Override
	protected void simpleInitGame() {
		Box box = new Box("Default NPC", new Vector3f(), 1, 1, 1);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		rootNode.attachChild(box);
	}

	public static void main(String[] args) {
		startGame();
	}
	
	public static HelloJme startGame() {
		HelloJme game = new HelloJme();
		game.setConfigShowMode(ConfigShowMode.AlwaysShow);
		game.start();
		return game;
	}

	/**
	 * Adds a new box to the client.
	 * @param name
	 */
	public void addNewBox(String name) {
		Box box = new Box(name);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		rootNode.attachChild(box);
	}

	/**
	 * Removes a box from the client.
	 * @param name
	 */
	public void removeBox(String name) {
		if(name == null || name.equals("")) {
			return;
		}
		rootNode.detachChildNamed(name);
	}
}
