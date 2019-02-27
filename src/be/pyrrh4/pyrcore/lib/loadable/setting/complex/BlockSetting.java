package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingInteger;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingMat;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class BlockSetting extends Loadable<BlockSetting> {

	// base
	public BlockSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingMat("type", "AIR", false, PCLocale.GUI_GENERIC_EDITOR_BLOCK_TYPELORE.getLines()));
		registerSetting(new SettingInteger("amount", "1", false, PCLocale.GUI_GENERIC_EDITOR_BLOCK_AMOUNTLORE.getLines()));
	}

	// get
	public Mat getType(Player parsing) {
		return getSettingMat("type").getParsed(parsing);
	}

	public Integer getAmount(Player parsing) {
		return getSettingInteger("amount").getParsed(parsing);
	}

	// methods
	public boolean isValid(Player player, Player parsingPlayer) {
		return getSettingMat("type").getParsed(player) != null;
	}

	public boolean isValid(Block block, Player player) {
		// type
		Mat type = getSettingMat("type").getParsed(player);
		if (type != null && !type.isAir()) {
			return block != null && type.isMat(block) && (Utils.isCrops(block) ? Utils.isFullyGrown(block) : true);
		}
		// no conditions so it's valid
		return true;
	}

	public void set(Block block, Player player) {
		Mat type = getSettingMat("type").getParsed(player);
		if (type != null) {
			type.setBlock(block);
		}
	}

}
