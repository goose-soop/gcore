package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementItemCheck;
import com.guillaumevdn.gcore.lib.element.type.list.ItemMatch;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementItemMatch extends ParseableContainerElement<ItemMatch> {

	private ElementItem item = addItem("item", Need.required(), ElementItemMode.MATCH, TextEditorGeneric.descriptionItemMatchItem);
	private ElementInteger goal = addInteger("goal", Need.optional(1), 1, TextEditorGeneric.descriptionItemMatchGoal);
	private ElementItemCheck check;

	public ElementItemMatch(Element parent, String id, Need need, boolean allowCustomCheck, Text editorDescription) {
		super("item condition", parent, id, need, editorDescription);
		check = !allowCustomCheck ? null : addItemCheck("check", Need.required(), TextEditorGeneric.descriptionItemMatchCheck);
	}

	// ----- get
	public ElementItem getItem() {
		return item;
	}

	public ElementInteger getGoal() {
		return goal;
	}

	public ElementItemCheck getCheck() {
		return check;
	}

	// ----- parse
	@Override
	public ItemMatch doParse(Replacer replacer) throws ParsingError {
		ItemReference reference = ItemReference.of(this.item, replacer);
		int goal = this.goal.parseNoCatch(replacer).orElse(1);
		ItemCheck check = this.check == null ? ItemCheck.ExactSame : this.check.parseNoCatch(replacer).orElse(ItemCheck.ExactSame);
		return new ItemMatch(getId(), reference, goal, check);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<String> desc = CollectionUtils.asList("check : " + this.check.parseGeneric().orElse(ItemCheck.ExactSame));
		item.parseGeneric().ifPresentDo(item -> desc.addAll(ItemUtils.describe(item)));
		return desc;
	}

}
