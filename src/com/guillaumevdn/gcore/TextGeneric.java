package com.guillaumevdn.gcore;

import com.guillaumevdn.gcore.lib.string.TextElement;
import com.guillaumevdn.gcore.lib.string.TextEnumElement;

/**
 * @author GuillaumeVDN
 */
public enum TextGeneric implements TextEnumElement {

	// ----- miscellaneous
	messageNoPermission,
	messageInvalidPlayer,
	messageInvalidOfflinePlayer,
	messageCC,
	messageSilentCC,
	textCancel,

	// ----- command
	commandParameterUsageTarget,
	commandParameterUsageReset,
	commandParameterUsageOperation,
	commandParameterUsageAmount,
	commandParameterUsageSpecificEditor,
	commandDescriptionGenericReload,
	commandDescriptionGenericPluginState,
	commandDescriptionGenericMigrate,
	messageCommandNotPlayer,
	messageCommandMissingArgument,
	messageCommandMissingArgumentFound,
	messageCommandUnnecessaryArguments,
	messageCommandIncompatibleArguments,
	messageCommandDependentArguments,
	messageCommandUnknown,
	messageCommandHelpElementPrefix,
	messageCommandHelpElement,
	messageCommandHelpColorArgumentOptional,
	messageCommandHelpColorArgumentRequired,
	messageCommandHelpPageOutsideBounds,
	messageCommandHelpHeader,

	// ----- numbers
	numberMillion,
	numberBillion,
	numberTrillion,
	durationFormatS,
	durationFormatMS,
	durationFormatHMS,
	durationFormatDHMS,

	// ----- cost
	messageMustHaveCurrency,
	messageMustHaveMoreItem,
	messageMustHaveItem,
	messageMustHaveItemNoDetail,

	// ----- item
	textDescribeItem,
	textDescribeItemNameIfHas,
	textDescribeItemLoreIfHas,
	textDescribeItemEnchantmentsIfHas,
	textDescribeItemEnchantmentsLine,

	textDescribeItemSingleLine,
	textDescribeItemSingleLineNameIfHas,
	textDescribeItemSingleLineLoreIfHas,
	textDescribeItemSingleLineEnchantmentsIfHas,
	textDescribeItemSingleLineEnchantmentsLine,
	textDescribeItemSingleLineSeparator,

	// ----- format
	dateTimeFormat

	;

	private TextElement text = new TextElement();

	TextGeneric() {
	}

	// ----- get
	@Override
	public String getId() {
		return name();
	}

	@Override
	public TextElement getText() {
		return text;
	}

}
