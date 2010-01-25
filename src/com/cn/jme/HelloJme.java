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
		Box box = new Box("myBox", new Vector3f(), 1, 1, 1);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		rootNode.attachChild(box);
	}

	public static void main(String[] args) {
		HelloJme game = new HelloJme();
		game.setConfigShowMode(ConfigShowMode.AlwaysShow);
		game.start();
	}

}
