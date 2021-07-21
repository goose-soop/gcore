package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementConfigSection;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementItemFlagList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMat;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.element.type.map.ElementEnchantmentLevelMap;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.item.meta.MetaBook;
import com.guillaumevdn.gcore.lib.item.meta.MetaEnchantmentStorage;
import com.guillaumevdn.gcore.lib.item.meta.MetaFirework;
import com.guillaumevdn.gcore.lib.item.meta.MetaFireworkEffect;
import com.guillaumevdn.gcore.lib.item.meta.MetaLeatherArmor;
import com.guillaumevdn.gcore.lib.item.meta.MetaPotion;
import com.guillaumevdn.gcore.lib.item.meta.MetaSkull;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterNBTCompound;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class ElementItem extends ParseableContainerElement<ItemStack> {

	private final ElementItemMode mode;

	private final ElementMat type;
	private final ElementInteger durability;
	private final ElementInteger amount;

	private final ElementBoolean unbreakable;
	private final ElementInteger customModelData;
	private final ElementEnchantmentLevelMap enchantments;
	private final ElementItemFlagList flags;
	private final ElementString name;
	private final ElementStringList lore;

	private final ElementConfigSection nbt;

	public ElementItem(Element parent, String id, Need need, ElementItemMode mode, Text editorDescription) {
		super("item", parent, id, need, editorDescription);
		this.mode = mode;

		type = addMat("type", mode.requireType() ? Need.required() : Need.optional(), TextEditorGeneric.descriptionItemType);
		durability = addInteger("durability", Need.optional(0), TextEditorGeneric.descriptionItemDurability);
		amount = !mode.allowAmount() ? null : addInteger("amount", Need.optional(1), TextEditorGeneric.descriptionItemAmount);

		unbreakable = addBoolean("unbreakable", Need.optional(false), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemUnbreakable);
		customModelData = !Version.ATLEAST_1_14 ? null : addInteger("custom_model_data", Need.optional(), TextEditorGeneric.descriptionItemCustomModelData);
		enchantments = addEnchantmentLevelMap("enchantments", Need.optional(), TextEditorGeneric.descriptionItemEnchantments);
		flags = addItemFlagList("flags", Need.optional(), TextEditorGeneric.descriptionItemFlags);
		name = addString("name", Need.optional(), TextEditorGeneric.descriptionItemName);
		lore = addStringList("lore", Need.optional(), TextEditorGeneric.descriptionItemLore);

		nbt = addConfigSection("nbt tags", "nbt", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemNbt);

		// add watcher of type for specific metas
		type.addWatcher((previous, next) -> {
			updateSpecificMetaElements(this, previous, next);
		});
	}

	private static final void updateSpecificMetaElements(ElementItem item, Mat previous, Mat next) {
		// same type, don't change
		if (Objects.deepEquals(previous, next)) {
			return;
		}
		// clear previous
		ItemStack prev = previous == null ? null : previous.newStack();
		ItemMeta prevMeta = prev == null ? null : prev.getItemMeta();
		if (prevMeta != null) {
			MetaBook.clearElements(item);
			MetaEnchantmentStorage.clearElements(item);
			MetaFireworkEffect.clearElements(item);
			MetaFirework.clearElements(item);
			MetaLeatherArmor.clearElements(item);
			MetaPotion.clearElements(item);
			MetaSkull.clearElements(item);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.clearElements(item);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.clearElements(item);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.clearElements(item);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.clearElements(item);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.clearElements(item);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.clearElements(item);
		}
		// fill new
		ItemStack nxt = next == null ? null : next.newStack();
		ItemMeta nextMeta = nxt == null ? null : nxt.getItemMeta();
		if (nextMeta != null) {
			MetaBook.fillElements(nextMeta, item);
			MetaEnchantmentStorage.fillElements(nextMeta, item);
			MetaFireworkEffect.fillElements(nextMeta, item);
			MetaFirework.fillElements(nextMeta, item);
			MetaLeatherArmor.fillElements(nextMeta, item);
			MetaPotion.fillElements(nextMeta, item);
			MetaSkull.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.fillElements(nextMeta, item);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.fillElements(nextMeta, item);
		}
	}

	public ElementItemMode getMode() {
		return mode;
	}

	public ElementMat getType() {
		return type;
	}

	public ElementInteger getDurability() {
		return durability;
	}

	public ElementInteger getAmount() {
		return amount;
	}

	public ElementBoolean getUnbreakable() {
		return unbreakable;
	}

	public ElementInteger getCustomModelData() {
		return customModelData;
	}

	public ElementEnchantmentLevelMap getEnchantments() {
		return enchantments;
	}

	public ElementItemFlagList getFlags() {
		return flags;
	}

	public ElementString getName() {
		return name;
	}

	public ElementStringList getLore() {
		return lore;
	}

	public ElementConfigSection getNbt() {
		return nbt;
	}

	// ----- read/import
	@Override
	protected void doRead() throws Throwable {
		type.read();  // read type first so it'll trigger watcher that will add elements
		for (Element element : values()) {
			if (!element.equals(type)) {
				element.read();
			}
		}
	}

	public void importValue(ItemStack value, @Nullable Player clicker) {
		// item
		Mat mat = Mat.fromItem(value).get();
		int dura = Compat.getDurability(value);
		type.setValue(CollectionUtils.asList(mat.getId()));
		durability.setValue(dura == 0 ? null : CollectionUtils.asList("" + dura));
		if (amount != null) {
			amount.setValue(value.getAmount() == 1 ? null : CollectionUtils.asList("" + value.getAmount()));
		}
		// meta
		if (value.hasItemMeta()) {
			ItemMeta meta = value.getItemMeta();
			unbreakable.setValue(Compat.isUnbreakable(meta) ? null : CollectionUtils.asList("true"));
			if (Version.ATLEAST_1_14) {
				customModelData.setValue(meta.hasCustomModelData() ? CollectionUtils.asList("" + meta.getCustomModelData()) : null);
			}
			enchantments.clear();
			meta.getEnchants().forEach((enchantment, level) -> {
				enchantments.createAndAddElement(enchantment).setValue(CollectionUtils.asList("" + level));
			});
			List<ItemFlag> fl = Compat.getItemFlags(meta);
			flags.setValue(fl.isEmpty() ? null : Serializer.ITEM_FLAG.serialize(fl));
			name.setValue(meta.hasDisplayName() ? CollectionUtils.asList(meta.getDisplayName()) : null);
			lore.setValue(meta.hasLore() ? CollectionUtils.asList(meta.getLore()) : null);
			// specific meta
			MetaBook.importElements(this, meta);
			MetaEnchantmentStorage.importElements(this, meta);
			MetaFireworkEffect.importElements(this, meta);
			MetaFirework.importElements(this, meta);
			MetaLeatherArmor.importElements(this, meta);
			MetaPotion.importElements(this, meta);
			MetaSkull.importElements(this, meta);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.importElements(this, meta);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.importElements(this, meta);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.importElements(this, meta);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.importElements(this, meta);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.importElements(this, meta, clicker);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.importElements(this, meta);
		}
		// nbt
		try {
			DataIO nbtWriter = new DataIO();
			AdapterNBTCompound.INSTANCE.write(new NBTItem(value), nbtWriter);
			nbt.setValue(nbtWriter.isEmpty() ? null : nbtWriter.toYML(null, "nbt", false));
		} catch (Throwable exception) {
			if (clicker != null) {
				clicker.sendMessage("§cCouldn't save NBT tags of this item, see console for a detailed error.");
			}
			exception.printStackTrace();
		}
	}

	// ----- parsing/match
	@Override
	public ItemStack doParse(Replacer replacer) throws ParsingError {
		// don't contain and optional, means it's null : don't throw parsing errors
		if (!readContains() && getNeed().equals(NeedType.OPTIONAL)) {
			return null;
		}
		// wrap in DataIO and then use the adapter, so it's consistent with json/config
		DataIO data = new DataIO();
		data.write("type", type.parseNoCatchOrThrowParsingNull(replacer));
		data.write("durability", durability.parseNoCatch(replacer).orNull());
		if (amount != null) {
			data.write("amount", amount.parseNoCatch(replacer).orNull());
		} else {
			data.write("amount", 1);
		}
		data.write("unbreakable", unbreakable.parseNoCatch(replacer).orNull());
		if (Version.ATLEAST_1_14) {
			data.write("customModelData", customModelData.parseNoCatch(replacer).orNull());
		}
		enchantments.parseNoCatch(replacer).ifPresentDo(enchants -> {
			data.writeObject("enchantments", w -> {
				enchants.forEach((enchantment, level) -> w.write(enchantment.getName(), level));
			});
		});
		data.writeSerializedList("flags", flags.parseNoCatch(replacer).orNull());
		data.write("name", name.parseNoCatch(replacer).orNull());
		data.writeSerializedList("lore", lore.parseNoCatch(replacer).orNull());
		try {
			MetaBook.writeElements(this, data, replacer);
			MetaEnchantmentStorage.writeElements(this, data, replacer);
			MetaFireworkEffect.writeElements(this, data, replacer);
			MetaFirework.writeElements(this, data, replacer);
			MetaLeatherArmor.writeElements(this, data, replacer);
			MetaPotion.writeElements(this, data, replacer);
			MetaSkull.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.writeElements(this, data, replacer);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.writeElements(this, data, replacer);
			if (nbt.getValue() != null) {
				data.writeObject("nbt", nbt.getValue().toIO(false, replacer));
			}
			return AdapterItemStack.INSTANCE.readCurrent(data);
		} catch (Throwable exception) {
			throw new ParsingError(this, exception);
		}
	}

	public boolean match(ItemStack item, ItemCheck check, Replacer replacer) throws ParsingError {
		return ItemUtils.match(item, ItemReference.of(this, replacer), check);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlImport.parseLines());
		return lore;
	}

	@Override
	public List<String> editorCurrentValue() {
		return parseGeneric().ifPresentMap(item -> ItemUtils.describe(item)).orNull();
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// shift + right-click : import
		if (call.getType().equals(ClickType.SHIFT_RIGHT)) {
			call.getClicker().closeInventory();
			WorkerGCore.inst().awaitItem(call.getClicker(), TextEditorGeneric.messageElementContainerImportItem, value -> {
				importValue(value, call.getClicker());
				call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
				call.reopenGUI();
				getSuperElement().onEditorChange(this);
			}, () -> call.reopenGUI());
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
