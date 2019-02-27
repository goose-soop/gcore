package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPInteger;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPMat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CPBlock extends ContainerParseable {

	// base
	private PPMat type = addComponent(new PPMat("type", this, null, false, 0, EditorGUI.ICON_BLOCK, PCLocale.GUI_GENERIC_EDITOR_BLOCK_TYPELORE.getLines()));
	private PPInteger amount = addComponent(new PPInteger("amount", this, "1", 1, Integer.MAX_VALUE, false, 1, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_BLOCK_AMOUNTLORE.getLines()));

	public CPBlock(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "block", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPMat getType() {
		return type;
	}

	public Mat getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPInteger getAmount() {
		return amount;
	}

	public Integer getAmount(Player parser) {
		return amount.getParsedValue(parser);
	}

	// methods
	public boolean isValid(Player parser) {
		return getType(parser) != null;
	}

	public boolean isValid(Block block, Player parser) {
		// has type
		Mat type = getType(parser);
		if (type != null) {
			return type.isMat(block) && (Utils.isCrops(block) ? Utils.isFullyGrown(block) : true);
		}
		// no type, it's valid
		return true;
	}

	public void setBlock(Block block, Player parser) {
		Mat type = getType(parser);
		if (type != null) {
			type.setBlock(block);
		}
	}

	// clone
	protected CPBlock() {
		super();
	}

	@Override
	public CPBlock clone() {
		// clone
		CPBlock clone = (CPBlock) super.clone();
		// clone properties
		clone.type = type.clone();
		clone.amount = amount.clone();
		// success
		return clone;
	}

}
