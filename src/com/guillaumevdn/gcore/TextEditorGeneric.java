package com.guillaumevdn.gcore;

import com.guillaumevdn.gcore.lib.string.TextElement;
import com.guillaumevdn.gcore.lib.string.TextEnumElement;

/**
 * @author GuillaumeVDN
 */
public enum TextEditorGeneric implements TextEnumElement {

	// ----- element
	guiSelectTitle,
	guiSearchName,

	elementTypeMandatory,
	elementTypeOptional,

	elementCurrentValueNone,
	elementCurrentValueNoneDefault,
	elementCurrentValueSingle,
	elementCurrentValueSingleDefault,
	elementCurrentValueList,
	elementCurrentValueListDefault,
	elementCurrentValueListLine,

	elementDescription,
	elementElementCountSingle,
	elementElementCountPlural,
	elementListElement,

	// ----- controls
	controlEdit,
	controlClear,
	controlToggle,
	controlSelect,
	controlImport,
	controlDelete,
	controlLinearSelectType,
	controlLinearEditParameters,

	controlListInsert,
	controlListSwap,

	controlAddElementName,
	controlAddGlobalElementName,
	controlAddElementWithQuick,
	controlAddElementWithDefault,
	controlTextSetRandom,

	// ----- item
	descriptionItemType,
	descriptionItemTypeHDB,
	descriptionItemDurability,
	descriptionItemAmount,
	descriptionItemUnbreakable,
	descriptionItemCustomModelData,
	descriptionItemFlags,
	descriptionItemEnchantments,
	descriptionItemName,
	descriptionItemLore,
	descriptionItemNbt,

	descriptionItemBannerBaseColor,
	descriptionItemBannerPatterns,

	descriptionItemBookAuthor,
	descriptionItemBookTitle,
	descriptionItemBookGeneration,
	descriptionItemBookPages,

	descriptionItemCrossbowChargedProjectiles,

	descriptionItemStoredEnchantments,

	descriptionItemFireworkEffect,
	descriptionItemFireworkEffects,

	descriptionItemKnowledgeBookRecipes,

	descriptionItemLeatherArmorColor,

	descriptionItemPotionType,
	descriptionItemPotionExtra,
	descriptionItemPotionColor,
	descriptionItemPotionCustomEffects,

	descriptionItemSkullId,
	descriptionItemSkullName,
	descriptionItemSkullData,
	descriptionItemSkullSignature,

	descriptionItemSpawnEggType,

	descriptionItemSuspiciousStewCustomEffects,

	descriptionItemTropicalFishBodyColor,
	descriptionItemTropicalFishPatternColor,
	descriptionItemTropicalFishPattern,

	// ----- item condition
	descriptionItemMatchItem,
	descriptionItemMatchGoal,
	descriptionItemMatchCheck,

	descriptionItemsNeededItems,
	descriptionItemsNeededInHand,
	descriptionItemsNeededInHandSlot,
	descriptionItemsNeededCount,
	descriptionItemsNeededTake,
	descriptionItemsNeededErrorMessage,

	// ----- gui
	descriptionGuiName,
	descriptionGuiType,
	descriptionGuiContents,

	descriptionGuiItemType,
	descriptionGuiItemIcon,
	descriptionGuiItemLocations,
	descriptionGuiItemPersistent,
	descriptionGuiItemClickSound,
	descriptionGuiItemOverrideClicks,

	descriptionGuiItemDynamicBorderLinearOnCount,
	descriptionGuiItemDynamicBorderLinearIconOn,
	descriptionGuiItemDynamicBorderLinearIconOff,
	descriptionGuiItemDynamicBorderLinearRefreshTicks,

	// ----- position
	descriptionPositionType,

	descriptionPositionTypeClosestEntityTypes,
	descriptionPositionTypeClosestEntityNames,
	descriptionPositionTypeClosestEntityColors,
	descriptionPositionTypeCitizensNPCRelative,
	descriptionPositionTypeCitizensNPCRelativeNames,
	descriptionPositionTypeClosestMythicMobMobs,

	descriptionPositionTypeWorld,
	descriptionPositionTypeBiomes,
	descriptionPositionTypeSingleLocation,
	descriptionPositionTypeSingleRandomLocations,
	descriptionPositionTypeRelativeSingleLocation,
	descriptionPositionTypePointTolerance,

	descriptionPositionTypeBound1,
	descriptionPositionTypeBound2,
	descriptionPositionTypeRelativeBound1,
	descriptionPositionTypeRelativeBound2,
	descriptionPositionTypeWorldguardRegionWorld,
	descriptionPositionTypeWorldguardRegionRegion,

	descriptionPositionTypeSphereCenter,
	descriptionPositionTypeSphereRadius,
	descriptionPositionTypeCylinderCenter,
	descriptionPositionTypeCylinderRadius,

	// ----- relative location
	descriptionRelativeLocationHorizontalAngle,
	descriptionRelativeLocationVerticalOffset,
	descriptionRelativeLocationDistance,

	// ----- world restriction
	descriptionWorldRestrictionWhitelist,
	descriptionWorldRestrictionBlacklist,

	// ----- command restriction
	descriptionCommandRestrictionWhitelist,
	descriptionCommandRestrictionBlacklist,

	// ----- dynmap marker
	descriptionDynmapMarkerLocation,
	descriptionDynmapMarkerText,

	// ----- potion effect
	descriptionPotionEffectType,
	descriptionPotionEffectDuration,
	descriptionPotionEffectAmplifier,
	descriptionPotionEffectAmbient,
	descriptionPotionEffectParticles,
	descriptionPotionEffectIcon,

	// ----- firework effect
	descriptionFireworkEffectType,
	descriptionFireworkEffectColors,
	descriptionFireworkEffectFadeColors,
	descriptionFireworkEffectFlicker,
	descriptionFireworkEffectTrail,

	// ----- time
	descriptionElementTimeMonth,
	descriptionElementTimeDayOfMonth,
	descriptionElementTimeDayOfWeek,
	descriptionElementTimeHour,
	descriptionElementTimeMinute,

	// ----- time limit
	descriptionTimeLimitDuration,
	descriptionTimeLimitReminder,

	// ----- duration
	descriptionDurationTime,
	descriptionDurationUnit,

	// ----- comparison
	descriptionComparisonType,

	// ----- notify
	descriptionNotifyMessage,
	descriptionNotifyActionbar,
	descriptionNotifyActionbarDuration,
	descriptionNotifyBossbar,
	descriptionNotifyBossbarColor,
	descriptionNotifyBossbarStyle,
	descriptionNotifyBossbarFlags,
	descriptionNotifyBossbarDuration,
	descriptionNotifyTitle,
	descriptionNotifyTitleSubtitle,
	descriptionNotifyTitleFadeIn,
	descriptionNotifyTitleDuration,
	descriptionNotifyTitleFadeOut,
	descriptionNotifySound,
	descriptionNotifySoundVolume,
	descriptionNotifySoundPitch,

	// ----- time frame
	descriptionTimeFrameType,

	descriptionTimeFrameInDayStart,
	descriptionTimeFrameInDayEnd,

	descriptionTimeFrameInWeekStart,
	descriptionTimeFrameInWeekEnd,

	descriptionTimeFrameInMonthStart,
	descriptionTimeFrameInMonthEnd,

	descriptionTimeFrameInYearStart,
	descriptionTimeFrameInYearEnd,

	descriptionTimeFrameLimitedStart,
	descriptionTimeFrameLimitedEnd,

	descriptionTimeFrameRepeatPeriodDuration,

	// ----- message
	messageElementBasicEdit,
	messageElementBasicEditSuggestCurrent,
	messageElementBasicEditSearch,
	messageElementBasicEditSearchNoMatch,
	messageElementBasicEditSearchTooManyMatches,
	messageElementBasicEditSearchMatch,
	messageElementBasicListLineEdit,
	messageElementBasicListSwap,
	messageElementBasicListSwapped,
	messageElementBasicImportLocation,
	messageElementBasicImportNPC,
	messageElementContainerImportItem,
	messageElementCreateEnterId,
	messageElementCreateInvalidId,
	messageElementCreateAlreadyExists,
	messageCantImportValue

	;

	private TextElement text = new TextElement();

	TextEditorGeneric() {
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
