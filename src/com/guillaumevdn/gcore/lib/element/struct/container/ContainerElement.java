package com.guillaumevdn.gcore.lib.element.struct.container;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.block.ElementBlockStateList;
import com.guillaumevdn.gcore.lib.collection.LowerCaseArrayList;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.AbstractMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBiome;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBiomeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarFlag;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarFlagList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarStyle;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBucketType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementChancePercentage;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementClickType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementColorList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementComparisonType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementConfigSection;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementCurrency;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementCurrencyList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDamageCauseList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDayOfWeek;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDoubleList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDyeColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDyeColorList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEnchantment;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEnchantmentList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEntityType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEntityTypeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFireworkEffectType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFloat;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFloatList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementHorseColorList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementHorseStyleList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementIntegerList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInventoryTypeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementItemCheck;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementItemFlagList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocationList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLong;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLongList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMat;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMatList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMonth;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPermission;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPhysicalClickType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPhysicalClickTypeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionEffectType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionEffectTypeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionExtra;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementProjectileType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementProjectileTypeList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementRegainReasonList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementSound;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementText;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementTimeUnit;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementTreeType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementUUID;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementUUIDList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementVehicleType;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorld;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorldList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorldguardRegion;
import com.guillaumevdn.gcore.lib.element.type.container.ElementCommandRestriction;
import com.guillaumevdn.gcore.lib.element.type.container.ElementDynmapMarker;
import com.guillaumevdn.gcore.lib.element.type.container.ElementFireworkEffect;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemMode;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemsNeeded;
import com.guillaumevdn.gcore.lib.element.type.container.ElementNotify;
import com.guillaumevdn.gcore.lib.element.type.container.ElementPotionEffect;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.element.type.container.ElementTimeLimit;
import com.guillaumevdn.gcore.lib.element.type.container.ElementWorldRestriction;
import com.guillaumevdn.gcore.lib.element.type.list.ElementFireworkEffectList;
import com.guillaumevdn.gcore.lib.element.type.list.ElementItemList;
import com.guillaumevdn.gcore.lib.element.type.list.ElementItemMatchList;
import com.guillaumevdn.gcore.lib.element.type.list.ElementPositionList;
import com.guillaumevdn.gcore.lib.element.type.list.ElementPotionEffectList;
import com.guillaumevdn.gcore.lib.element.type.map.ElementCurrencyDoubleMap;
import com.guillaumevdn.gcore.lib.element.type.map.ElementEnchantmentLevelMap;
import com.guillaumevdn.gcore.lib.element.type.map.ElementStringMap;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.function.QuintConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.function.TriFunction;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.object.OptionalIfPresentFail;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.TimeUnit;
import com.guillaumevdn.gcore.lib.time.duration.ElementDuration;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeInDay;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeInMonth;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeInWeek;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeInYear;

/**
 * @author GuillaumeVDN
 */
public abstract class ContainerElement extends AbstractMapElement<String, Element> {

	private final LowerCaseArrayList startRowSlots = new LowerCaseArrayList();  // since elements are stored in a linked map, just save which elements trigger a new line
	private final LowerCaseArrayList skipSlots = new LowerCaseArrayList();  // since elements are stored in a linked map, just save which elements trigger a slot skip

	public ContainerElement(String typeName, Element parent, String id, Need need, Text editorDescription) {
		super(String.class, typeName, parent, id, need, editorDescription);
	}

	// ----- get
	public final LowerCaseArrayList getStartRowSlots() {
		return startRowSlots;
	}

	public final LowerCaseArrayList getSkipOneSlots() {
		return skipSlots;
	}

	@Override
	public boolean isCurrentlyDefault() {
		for (Element value : values()) {
			if (!value.isCurrentlyDefault()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final Optional<Element> getElement(String key) {
		Optional<Element> element = super.getElement(key);
		if (element == null) {
			throw new NoSuchElementException("Couldn't find element with id " + key);
		}
		return element;
	}

	public final <T extends Element> T getElementAs(String id) {
		return (T) getElement(id).orNull();
	}

	public final <T extends Element> T getElementAs(String id, Class<T> elementClass) {
		return getElementAs(id);
	}

	public final <T> Optional<T> parseElementAs(String id, Replacer replacer) {
		ParseableElement<T> element = getElementAs(id);
		return element != null ? element.parse(replacer) : Optional.empty();
	}

	public final <T> Optional<T> parseElementAs(String id, Class<T> contentClass, Replacer replacer) {
		ParseableElement<T> element = getElementAs(id);
		return element != null ? element.parse(replacer) : Optional.empty();
	}

	public final <E> Optional<List<E>> parseElementAsList(String id, Class<E> contentClass, Replacer replacer) {
		ParseableElement<List<E>> element = getElementAs(id);
		return element != null ? element.parse(replacer) : Optional.empty();
	}

	public final <K, V> Optional<Map<K, V>> parseElementAsMap(String id, Class<K> keyClass, Class<V> valueClass, Replacer replacer) {
		ParseableElement<Map<K, V>> element = getElementAs(id);
		return element != null ? element.parse(replacer) : Optional.empty();
	}

	// ----- allow some shotcuts, otherwise it's waaaaaayy too boring
	// ----- - direct parse one element
	public final <T> T directParseOrNull(String id, Replacer replacer) {
		Optional<T> parsed = parseElementAs(id, replacer);
		return parsed.orNull();
	}

	public final <T> T directParseNoCatchOrThrowParsingNull(String id, Replacer replacer) throws ParsingError {
		ParseableElement<T> element = getElementAs(id);
		if (element == null) {
			throw new ParsingError(null, "didn't find element '" + id + "'");
		}
		return element.parseNoCatchOrThrowParsingNull(replacer);
	}

	public final <T> T directParseOrElse(String id, Replacer replacer, T def) {
		Optional<T> parsed = parseElementAs(id, replacer);
		return parsed.orElse(def);
	}

	public final <T> T directParseOrElse(String id, Replacer replacer, Supplier<T> def) {
		Optional<T> parsed = parseElementAs(id, replacer);
		return parsed.orElse(def);
	}

	// ----- - direct parse elements and map
	public final <A, R> Optional<R> directParseAndIfPresentMap(String id, Replacer replacer, Function<A, R> mapper) {
		Optional<A> A = parseElementAs(id, replacer);
		return A.ifPresentMap(mapper);
	}

	public final <A, B, R> Optional<R> directParseAndIfPresentMap(String idA, String idB, Replacer replacer, BiFunction<A, B, R> mapper) {
		Optional<A> A = parseElementAs(idA, replacer);
		return A.ifPresentMap(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			return B.ifPresentMap(b -> mapper.apply(a, b)).orNull();
		});
	}

	public final <A, B, C, R> Optional<R> directParseAndIfPresentMap(String idA, String idB, String idC, Replacer replacer, TriFunction<A, B, C, R> mapper) {
		Optional<A> A = parseElementAs(idA, replacer);
		return A.ifPresentMap(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			return B.ifPresentMap(b -> {
				Optional<C> C = parseElementAs(idC, replacer);
				return C.ifPresentMap(c -> mapper.apply(a, b, c)).orNull();
			}).orNull();
		});
	}

	// ----- - direct parse elements and do
	public final <A> OptionalIfPresentFail directParseAndIfPresentDo(String id, Replacer replacer, Consumer<A> consumer) {
		Optional<A> A = parseElementAs(id, replacer);
		return A.ifPresentDo(consumer);
	}

	public final <A, B> void directParseAndIfPresentDo(String idA, String idB, Replacer replacer, BiConsumer<A, B> consumer) {
		Optional<A> A = parseElementAs(idA, replacer);
		A.ifPresentDo(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			B.ifPresentDo(b -> {
				consumer.accept(a, b);
			});
		});
	}

	public final <A, B, C> void directParseAndIfPresentDo(String idA, String idB, String idC, Replacer replacer, TriConsumer<A, B, C> consumer) {
		Optional<A> A = parseElementAs(idA, replacer);
		A.ifPresentDo(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			B.ifPresentDo(b -> {
				Optional<C> C = parseElementAs(idC, replacer);
				C.ifPresentDo(c -> {
					consumer.accept(a, b, c);
				});
			});
		});
	}

	public final <A, B, C, D> void directParseAndIfPresentDo(String idA, String idB, String idC, String idD, Replacer replacer, QuadriConsumer<A, B, C, D> consumer) {
		Optional<A> A = parseElementAs(idA, replacer);
		A.ifPresentDo(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			B.ifPresentDo(b -> {
				Optional<C> C = parseElementAs(idC, replacer);
				C.ifPresentDo(c -> {
					Optional<D> D = parseElementAs(idD, replacer);
					D.ifPresentDo(d -> {
						consumer.accept(a, b, c, d);
					});
				});
			});
		});
	}

	public final <A, B, C, D, E> void directParseAndIfPresentDo(String idA, String idB, String idC, String idD, String idE, Replacer replacer, QuintConsumer<A, B, C, D, E> consumer) {
		Optional<A> A = parseElementAs(idA, replacer);
		A.ifPresentDo(a -> {
			Optional<B> B = parseElementAs(idB, replacer);
			B.ifPresentDo(b -> {
				Optional<C> C = parseElementAs(idC, replacer);
				C.ifPresentDo(c -> {
					Optional<D> D = parseElementAs(idD, replacer);
					D.ifPresentDo(d -> {
						Optional<E> E = parseElementAs(idE, replacer);
						E.ifPresentDo(e -> {
							consumer.accept(a, b, c, d, e);
						});
					});
				});
			});
		});
	}

	// ----- add/remove
	@Override
	protected final Element add(String key, Element value) {
		return super.add(key, value);
	}

	/** @eturn the value parameter */
	public <T extends Element> T add(T element) {
		return (T) add(element.getId(), element);
	}

	/** @eturn the value parameter */
	public <T extends Element> T add(T element, SlotPlacement slot) {
		slot(element.getId(), slot);
		return add(element);
	}

	@Override
	protected final void clear() {
		super.clear();
	}

	// ----- loading and saving
	@Override
	public void resetCache() {
		getElements().forEach(elem -> elem.getB().resetCache());
	}

	@Override
	protected final void clearBeforeRead() {  // obviously don't
	}

	//private static final Set<String> IGNORE_OPTIONS = CollectionUtils.asSet();

	@Override
	protected void doRead() throws Throwable {
		// read elements
		List<String> keys = getSuperElement().getConfiguration().readKeysForSectionCopyIfEmpty(getConfigurationPath());  // this creates a new list
		for (Element element : values()) {
			element.read();
			keys.remove(element.getId());
		}

		// look for unknown options and log them
		//keys.removeAll(IGNORE_OPTIONS);
		for (String key : keys) {
			getSuperElement().addLoadError("found unknown option '" + key + "' at path '" + (getConfigurationPath().isEmpty() ? key : getConfigurationPath() + "." + key) + "'");
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		getSuperElement().getConfiguration().write(getConfigurationPath(), null);
		for (Element element : values()) {
			element.write();
		}
		// if nothing was written (all default values) AND the DIRECT parent is a map element (which is not a container element), then write an empty value
		if (!getSuperElement().getConfiguration().contains(getConfigurationPath())) {
			AbstractMapElement parent = ObjectUtils.castOrNull(getParent(), AbstractMapElement.class);
			if (parent != null && !(parent instanceof ContainerElement)) {
				getSuperElement().getConfiguration().getBackingYML().mkdirs(getConfigurationPath());
			}
		}
	}

	// ----- editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(this, fromCall) {
			@Override
			protected boolean doFill() {
				int page = 0;
				int slot = -1;
				for (Element element : values()) {
					++slot;
					if (startRowSlots.contains(element.getId())) while (slot % 9 != 0) ++slot;
					else if (skipSlots.contains(element.getId()) && slot % 9 != 0) ++slot;

					if (slot > GUIType.CHEST_6_ROW.getRegularItemSlotsEnd()) {
						slot = 0;
						++page;
					} else if (slot >= getType().getSize()) {
						break;
					}

					setRegularItem(element.buildEditorItem(page, slot));
				}
				return super.doFill();
			}
		};
		return editor;
	}

	// ----- ez methods to add a specific element
	private final void slot(String id, SlotPlacement slot) {
		if (slot != null) {
			if (slot.equals(SlotPlacement.START_ROW)) {
				startRowSlots.add(id);
			} else if (slot.equals(SlotPlacement.SKIP_ONE)) {
				skipSlots.add(id);
			}
		}
	}

	public final ElementBlockStateList addBlockStateList(String id, Need need, Text editorDescription) { return add(new ElementBlockStateList(this, id, need, editorDescription)); }
	public final ElementBlockStateList addBlockStateList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBlockStateList(this, id, need, editorDescription)); }
	public final ElementBiome addBiome(String id, Need need, Text editorDescription) { return add(new ElementBiome(this, id, need, editorDescription)); }
	public final ElementBiome addBiome(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBiome(this, id, need, editorDescription)); }
	public final ElementBiomeList addBiomeList(String id, Need need, Text editorDescription) { return add(new ElementBiomeList(this, id, need, editorDescription)); }
	public final ElementBiomeList addBiomeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBiomeList(this, id, need, editorDescription)); }
	public final ElementBoolean addBoolean(String id, Need need, Text editorDescription) { return add(new ElementBoolean(this, id, need, editorDescription)); }
	public final ElementBoolean addBoolean(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBoolean(this, id, need, editorDescription)); }
	public final ElementBossbarColor addBossbarColor(String id, Need need, Text editorDescription) { return add(new ElementBossbarColor(this, id, need, editorDescription)); }
	public final ElementBossbarColor addBossbarColor(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBossbarColor(this, id, need, editorDescription)); }
	public final ElementBossbarStyle addBossbarStyle(String id, Need need, Text editorDescription) { return add(new ElementBossbarStyle(this, id, need, editorDescription)); }
	public final ElementBossbarStyle addBossbarStyle(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBossbarStyle(this, id, need, editorDescription)); }
	public final ElementBossbarFlag addBossbarFlag(String id, Need need, Text editorDescription) { return add(new ElementBossbarFlag(this, id, need, editorDescription)); }
	public final ElementBossbarFlag addBossbarFlag(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBossbarFlag(this, id, need, editorDescription)); }
	public final ElementBossbarFlagList addBossbarFlagList(String id, Need need, Text editorDescription) { return add(new ElementBossbarFlagList(this, id, need, editorDescription)); }
	public final ElementBossbarFlagList addBossbarFlagList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBossbarFlagList(this, id, need, editorDescription)); }
	public final ElementBucketType addBucketType(String id, Need need, Text editorDescription) { return add(new ElementBucketType(this, id, need, editorDescription)); }
	public final ElementBucketType addBucketType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementBucketType(this, id, need, editorDescription)); }
	public final ElementChancePercentage addChancePercentage(String id, Need need, Text editorDescription) { return add(new ElementChancePercentage(this, id, need, editorDescription)); }
	public final ElementChancePercentage addChancePercentage(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementChancePercentage(this, id, need, editorDescription)); }
	public final ElementClickType addClickType(String id, Need need, Text editorDescription) { return add(new ElementClickType(this, id, need, editorDescription)); }
	public final ElementClickType addClickType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementClickType(this, id, need, editorDescription)); }
	public final ElementColor addColor(String id, Need need, Text editorDescription) { return add(new ElementColor(this, id, need, editorDescription)); }
	public final ElementColor addColor(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementColor(this, id, need, editorDescription)); }
	public final ElementColorList addColorList(String id, Need need, Text editorDescription) { return add(new ElementColorList(this, id, need, editorDescription)); }
	public final ElementColorList addColorList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementColorList(this, id, need, editorDescription)); }
	public final ElementCommandRestriction addCommandRestriction(String id, Need need, Text editorDescription) { return add(new ElementCommandRestriction(this, id, need, editorDescription)); }
	public final ElementCommandRestriction addCommandRestriction(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementCommandRestriction(this, id, need, editorDescription)); }
	public final ElementComparisonType addComparisonType(String id, Need need, Text editorDescription) { return add(new ElementComparisonType(this, id, need, editorDescription)); }
	public final ElementComparisonType addComparisonType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementComparisonType(this, id, need, editorDescription)); }
	public final ElementConfigSection addConfigSection(String typeName, String id, Need need, Text editorDescription) { return add(new ElementConfigSection(typeName, this, id, need, editorDescription)); }
	public final ElementConfigSection addConfigSection(String typeName, String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementConfigSection(typeName, this, id, need, editorDescription)); }
	public final ElementCurrency addCurrency(String id, Need need, Text editorDescription) { return add(new ElementCurrency(this, id, need, editorDescription)); }
	public final ElementCurrency addCurrency(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementCurrency(this, id, need, editorDescription)); }
	public final ElementCurrencyList addCurrencyList(String id, Need need, Text editorDescription) { return add(new ElementCurrencyList(this, id, need, editorDescription)); }
	public final ElementCurrencyList addCurrencyList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementCurrencyList(this, id, need, editorDescription)); }
	public final ElementCurrencyDoubleMap addCurrencyDoubleMap(String id, Need need, Text editorDescription) { return add(new ElementCurrencyDoubleMap(this, id, need, editorDescription)); }
	public final ElementCurrencyDoubleMap addCurrencyDoubleMap(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementCurrencyDoubleMap(this, id, need, editorDescription)); }
	public final ElementDayOfWeek addDayOfWeek(String id, Need need, Text editorDescription) { return add(new ElementDayOfWeek(this, id, need, editorDescription)); }
	public final ElementDayOfWeek addDayOfWeek(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDayOfWeek(this, id, need, editorDescription)); }
	public final ElementDamageCauseList addDamageCauseList(String id, Need need, Text editorDescription) { return add(new ElementDamageCauseList(this, id, need, editorDescription)); }
	public final ElementDamageCauseList addDamageCauseList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDamageCauseList(this, id, need, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, Text editorDescription) { return add(new ElementDouble(this, id, need, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDouble(this, id, need, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, double min, Text editorDescription) { return add(new ElementDouble(this, id, need, min, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, double min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDouble(this, id, need, min, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, double min, double max, Text editorDescription) { return add(new ElementDouble(this, id, need, min, max, editorDescription)); }
	public final ElementDouble addDouble(String id, Need need, double min, double max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDouble(this, id, need, min, max, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, Text editorDescription) { return add(new ElementDoubleList(this, id, need, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDoubleList(this, id, need, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, double min, Text editorDescription) { return add(new ElementDoubleList(this, id, need, min, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, double min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDoubleList(this, id, need, min, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, double min, double max, Text editorDescription) { return add(new ElementDoubleList(this, id, need, min, max, editorDescription)); }
	public final ElementDoubleList addDoubleList(String id, Need need, double min, double max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDoubleList(this, id, need, min, max, editorDescription)); }
	public final ElementDyeColor addDyeColor(String id, Need need, Text editorDescription) { return add(new ElementDyeColor(this, id, need, editorDescription)); }
	public final ElementDyeColor addDyeColor(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDyeColor(this, id, need, editorDescription)); }
	public final ElementDyeColorList addDyeColorList(String id, Need need, Text editorDescription) { return add(new ElementDyeColorList(this, id, need, editorDescription)); }
	public final ElementDyeColorList addDyeColorList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDyeColorList(this, id, need, editorDescription)); }
	public final ElementDynmapMarker addDynmapMarker(String id, Need need, Text editorDescription) { return add(new ElementDynmapMarker(this, id, need, editorDescription)); }
	public final ElementDynmapMarker addDynmapMarker(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDynmapMarker(this, id, need, editorDescription)); }
	public final ElementDuration addDuration(String id, Need need, Integer defaultTime, TimeUnit defaultUnit, Text editorDescription) { return add(new ElementDuration(this, id, need, defaultTime, defaultUnit, editorDescription)); }
	public final ElementDuration addDuration(String id, Need need, Integer defaultTime, TimeUnit defaultUnit, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementDuration(this, id, need, defaultTime, defaultUnit, editorDescription)); }
	public final ElementEnchantment addEnchantment(String id, Need need, Text editorDescription) { return add(new ElementEnchantment(this, id, need, editorDescription)); }
	public final ElementEnchantment addEnchantment(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementEnchantment(this, id, need, editorDescription)); }
	public final ElementEnchantmentList addEnchantmentList(String id, Need need, Text editorDescription) { return add(new ElementEnchantmentList(this, id, need, editorDescription)); }
	public final ElementEnchantmentList addEnchantmentList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementEnchantmentList(this, id, need, editorDescription)); }
	public final ElementEnchantmentLevelMap addEnchantmentLevelMap(String id, Need need, Text editorDescription) { return add(new ElementEnchantmentLevelMap(this, id, need, editorDescription)); }
	public final ElementEnchantmentLevelMap addEnchantmentLevelMap(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementEnchantmentLevelMap(this, id, need, editorDescription)); }
	public final ElementEntityType addEntityType(String id, Need need, Text editorDescription) { return add(new ElementEntityType(this, id, need, editorDescription)); }
	public final ElementEntityType addEntityType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementEntityType(this, id, need, editorDescription)); }
	public final ElementEntityTypeList addEntityTypeList(String id, Need need, Text editorDescription) { return add(new ElementEntityTypeList(this, id, need, editorDescription)); }
	public final ElementEntityTypeList addEntityTypeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementEntityTypeList(this, id, need, editorDescription)); }
	public final ElementFireworkEffect addFireworkEffect(String id, Need need, Text editorDescription) { return add(new ElementFireworkEffect(this, id, need, editorDescription)); }
	public final ElementFireworkEffect addFireworkEffect(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFireworkEffect(this, id, need, editorDescription)); }
	public final ElementFireworkEffectList addFireworkEffectList(String id, Need need, Text editorDescription) { return add(new ElementFireworkEffectList(this, id, need, editorDescription)); }
	public final ElementFireworkEffectList addFireworkEffectList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFireworkEffectList(this, id, need, editorDescription)); }
	public final ElementFireworkEffectType addFireworkEffectType(String id, Need need, Text editorDescription) { return add(new ElementFireworkEffectType(this, id, need, editorDescription)); }
	public final ElementFireworkEffectType addFireworkEffectType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFireworkEffectType(this, id, need, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, Text editorDescription) { return add(new ElementFloat(this, id, need, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloat(this, id, need, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, float min, Text editorDescription) { return add(new ElementFloat(this, id, need, min, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, float min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloat(this, id, need, min, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, float min, float max, Text editorDescription) { return add(new ElementFloat(this, id, need, min, max, editorDescription)); }
	public final ElementFloat addFloat(String id, Need need, float min, float max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloat(this, id, need, min, max, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, Text editorDescription) { return add(new ElementFloatList(this, id, need, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloatList(this, id, need, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, float min, Text editorDescription) { return add(new ElementFloatList(this, id, need, min, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, float min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloatList(this, id, need, min, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, float min, float max, Text editorDescription) { return add(new ElementFloatList(this, id, need, min, max, editorDescription)); }
	public final ElementFloatList addFloatList(String id, Need need, float min, float max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementFloatList(this, id, need, min, max, editorDescription)); }
	public final ElementHorseColorList addHorseColorList(String id, Need need, Text editorDescription) { return add(new ElementHorseColorList(this, id, need, editorDescription)); }
	public final ElementHorseColorList addHorseColorList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementHorseColorList(this, id, need, editorDescription)); }
	public final ElementHorseStyleList addHorseStyleList(String id, Need need, Text editorDescription) { return add(new ElementHorseStyleList(this, id, need, editorDescription)); }
	public final ElementHorseStyleList addHorseStyleList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementHorseStyleList(this, id, need, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, Text editorDescription) { return add(new ElementInteger(this, id, need, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementInteger(this, id, need, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, int min, Text editorDescription) { return add(new ElementInteger(this, id, need, min, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, int min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementInteger(this, id, need, min, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, int min, int max, Text editorDescription) { return add(new ElementInteger(this, id, need, min, max, editorDescription)); }
	public final ElementInteger addInteger(String id, Need need, int min, int max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementInteger(this, id, need, min, max, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, Text editorDescription) { return add(new ElementIntegerList(this, id, need, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementIntegerList(this, id, need, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, int min, Text editorDescription) { return add(new ElementIntegerList(this, id, need, min, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, int min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementIntegerList(this, id, need, min, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, int min, int max, Text editorDescription) { return add(new ElementIntegerList(this, id, need, min, max, editorDescription)); }
	public final ElementIntegerList addIntegerList(String id, Need need, int min, int max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementIntegerList(this, id, need, min, max, editorDescription)); }
	public final ElementInventoryTypeList addInventoryTypeList(String id, Need need, Text editorDescription) { return add(new ElementInventoryTypeList(this, id, need, editorDescription)); }
	public final ElementInventoryTypeList addInventoryTypeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementInventoryTypeList(this, id, need, editorDescription)); }
	public final ElementItem addItem(String id, Need need, ElementItemMode mode, Text editorDescription) { return add(new ElementItem(this, id, need, mode, editorDescription)); }
	public final ElementItem addItem(String id, Need need, ElementItemMode mode, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItem(this, id, need, mode, editorDescription)); }
	public final ElementItemCheck addItemCheck(String id, Need need, Text editorDescription) { return add(new ElementItemCheck(this, id, need, editorDescription)); }
	public final ElementItemCheck addItemCheck(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItemCheck(this, id, need, editorDescription)); }
	public final ElementItemMatchList addItemMatchList(String id, Need need, boolean allowCustomCheck, Text editorDescription) { return add(new ElementItemMatchList(this, id, need, allowCustomCheck, editorDescription)); }
	public final ElementItemMatchList addItemMatchList(String id, Need need, boolean allowCustomCheck, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItemMatchList(this, id, need, allowCustomCheck, editorDescription)); }
	public final ElementItemFlagList addItemFlagList(String id, Need need, Text editorDescription) { return add(new ElementItemFlagList(this, id, need, editorDescription)); }
	public final ElementItemFlagList addItemFlagList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItemFlagList(this, id, need, editorDescription)); }
	public final ElementItemList addItemList(String id, Need need, ElementItemMode mode, Text editorDescription) { return add(new ElementItemList(this, id, need, mode, editorDescription)); }
	public final ElementItemList addItemList(String id, Need need, ElementItemMode mode, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItemList(this, id, need, mode, editorDescription)); }
	public final ElementItemsNeeded addItemsNeeded(String id, Need need, Text editorDescription) { return add(new ElementItemsNeeded(this, id, need, editorDescription)); }
	public final ElementItemsNeeded addItemsNeeded(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementItemsNeeded(this, id, need, editorDescription)); }
	public final ElementLocation addLocation(String id, Need need, Text editorDescription) { return add(new ElementLocation(this, id, need, editorDescription)); }
	public final ElementLocation addLocation(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLocation(this, id, need, editorDescription)); }
	public final ElementLocationList addLocationList(String id, Need need, Text editorDescription) { return add(new ElementLocationList(this, id, need, editorDescription)); }
	public final ElementLocationList addLocationList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLocationList(this, id, need, editorDescription)); }
	public final ElementLong addLong(String id, Need need, Text editorDescription) { return add(new ElementLong(this, id, need, editorDescription)); }
	public final ElementLong addLong(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLong(this, id, need, editorDescription)); }
	public final ElementLong addLong(String id, Need need, long min, Text editorDescription) { return add(new ElementLong(this, id, need, min, editorDescription)); }
	public final ElementLong addLong(String id, Need need, long min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLong(this, id, need, min, editorDescription)); }
	public final ElementLong addLong(String id, Need need, long min, long max, Text editorDescription) { return add(new ElementLong(this, id, need, min, max, editorDescription)); }
	public final ElementLong addLong(String id, Need need, long min, long max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLong(this, id, need, min, max, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, Text editorDescription) { return add(new ElementLongList(this, id, need, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLongList(this, id, need, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, long min, Text editorDescription) { return add(new ElementLongList(this, id, need, min, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, long min, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLongList(this, id, need, min, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, long min, long max, Text editorDescription) { return add(new ElementLongList(this, id, need, min, max, editorDescription)); }
	public final ElementLongList addLongList(String id, Need need, long min, long max, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementLongList(this, id, need, min, max, editorDescription)); }
	public final ElementNotify addNotify(String id, Need need, Text editorDescription) { return add(new ElementNotify(this, id, need, editorDescription)); }
	public final ElementNotify addNotify(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementNotify(this, id, need, editorDescription)); }
	public final ElementMat addMat(String id, Need need, Text editorDescription) { return add(new ElementMat(this, id, need, editorDescription)); }
	public final ElementMat addMat(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementMat(this, id, need, editorDescription)); }
	public final ElementMatList addMatList(String id, Need need, Text editorDescription) { return add(new ElementMatList(this, id, need, editorDescription)); }
	public final ElementMatList addMatList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementMatList(this, id, need, editorDescription)); }
	public final ElementMonth addMonth(String id, Need need, Text editorDescription) { return add(new ElementMonth(this, id, need, editorDescription)); }
	public final ElementMonth addMonth(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementMonth(this, id, need, editorDescription)); }
	public final com.guillaumevdn.gcore.lib.element.type.map.ElementPatternTypeColorMap addPatternTypeColorMap(String id, Need need, Text editorDescription) { return add(new com.guillaumevdn.gcore.lib.element.type.map.ElementPatternTypeColorMap(this, id, need, editorDescription)); }
	public final com.guillaumevdn.gcore.lib.element.type.map.ElementPatternTypeColorMap addPatternTypeColorMap(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new com.guillaumevdn.gcore.lib.element.type.map.ElementPatternTypeColorMap(this, id, need, editorDescription)); }
	public final ElementPermission addPermission(String id, Need need, Text editorDescription) { return add(new ElementPermission(this, id, need, editorDescription)); }
	public final ElementPermission addPermission(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPermission(this, id, need, editorDescription)); }
	public final ElementPhysicalClickType addPhysicalClickType(String id, Need need, Text editorDescription) { return add(new ElementPhysicalClickType(this, id, need, editorDescription)); }
	public final ElementPhysicalClickType addPhysicalClickType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPhysicalClickType(this, id, need, editorDescription)); }
	public final ElementPhysicalClickTypeList addPhysicalClickTypeList(String id, Need need, Text editorDescription) { return add(new ElementPhysicalClickTypeList(this, id, need, editorDescription)); }
	public final ElementPhysicalClickTypeList addPhysicalClickTypeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPhysicalClickTypeList(this, id, need, editorDescription)); }
	public final ElementPointTolerance addPointTolerance(String id, Need need, Text editorDescription) { return add(new ElementPointTolerance(this, id, need, editorDescription)); }
	public final ElementPointTolerance addPointTolerance(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPointTolerance(this, id, need, editorDescription)); }
	public final ElementPosition addPosition(String id, Need need, Text editorDescription) { return add(new ElementPosition(this, id, need, editorDescription)); }
	public final ElementPosition addPosition(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPosition(this, id, need, editorDescription)); }
	public final ElementPositionList addPositionList(String id, Need need, Text editorDescription) { return add(new ElementPositionList(this, id, need, editorDescription)); }
	public final ElementPositionList addPositionList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPositionList(this, id, need, editorDescription)); }
	public final ElementPotionEffect addPotionEffect(String id, Need need, Text editorDescription) { return add(new ElementPotionEffect(this, id, need, editorDescription)); }
	public final ElementPotionEffect addPotionEffect(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionEffect(this, id, need, editorDescription)); }
	public final ElementPotionEffectList addPotionEffectList(String id, Need need, Text editorDescription) { return add(new ElementPotionEffectList(this, id, need, editorDescription)); }
	public final ElementPotionEffectList addPotionEffectList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionEffectList(this, id, need, editorDescription)); }
	public final ElementPotionEffectType addPotionEffectType(String id, Need need, Text editorDescription) { return add(new ElementPotionEffectType(this, id, need, editorDescription)); }
	public final ElementPotionEffectType addPotionEffectType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionEffectType(this, id, need, editorDescription)); }
	public final ElementPotionEffectTypeList addPotionEffectTypeList(String id, Need need, Text editorDescription) { return add(new ElementPotionEffectTypeList(this, id, need, editorDescription)); }
	public final ElementPotionEffectTypeList addPotionEffectTypeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionEffectTypeList(this, id, need, editorDescription)); }
	public final ElementPotionExtra addPotionExtra(String id, Need need, Text editorDescription) { return add(new ElementPotionExtra(this, id, need, editorDescription)); }
	public final ElementPotionExtra addPotionExtra(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionExtra(this, id, need, editorDescription)); }
	public final ElementPotionType addPotionType(String id, Need need, Text editorDescription) { return add(new ElementPotionType(this, id, need, editorDescription)); }
	public final ElementPotionType addPotionType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementPotionType(this, id, need, editorDescription)); }
	public final ElementProjectileType addProjectileType(String id, Need need, Text editorDescription) { return add(new ElementProjectileType(this, id, need, editorDescription)); }
	public final ElementProjectileType addProjectileType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementProjectileType(this, id, need, editorDescription)); }
	public final ElementProjectileTypeList addProjectileTypeList(String id, Need need, Text editorDescription) { return add(new ElementProjectileTypeList(this, id, need, editorDescription)); }
	public final ElementProjectileTypeList addProjectileTypeList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementProjectileTypeList(this, id, need, editorDescription)); }
	public final ElementRegainReasonList addRegainReasonList(String id, Need need, Text editorDescription) { return add(new ElementRegainReasonList(this, id, need, editorDescription)); }
	public final ElementRegainReasonList addRegainReasonList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementRegainReasonList(this, id, need, editorDescription)); }
	public final ElementRelativeLocation addRelativeLocation(String id, Need need, Text editorDescription) { return add(new ElementRelativeLocation(this, id, need, editorDescription)); }
	public final ElementRelativeLocation addRelativeLocation(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementRelativeLocation(this, id, need, editorDescription)); }
	public final ElementSound addSound(String id, Need need, Text editorDescription) { return add(new ElementSound(this, id, need, editorDescription)); }
	public final ElementSound addSound(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementSound(this, id, need, editorDescription)); }
	public final ElementString addString(String id, Need need, Text editorDescription) { return add(new ElementString(this, id, need, editorDescription)); }
	public final ElementString addString(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementString(this, id, need, editorDescription)); }
	public final ElementStringList addStringList(String id, Need need, Text editorDescription) { return add(new ElementStringList(this, id, need, editorDescription)); }
	public final ElementStringList addStringList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementStringList(this, id, need, editorDescription)); }
	public final ElementStringMap addStringMap(String id, Need need, Text editorDescription) { return add(new ElementStringMap(this, id, need, editorDescription)); }
	public final ElementStringMap addStringMap(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementStringMap(this, id, need, editorDescription)); }
	public final ElementText addText(String id, Need need, Text editorDescription) { return add(new ElementText(this, id, need, editorDescription)); }
	public final ElementText addText(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementText(this, id, need, editorDescription)); }
	public final ElementTimeFrame addTimeFrame(String id, Need need, Text editorDescription) { return add(new ElementTimeFrame(this, id, need, editorDescription)); }
	public final ElementTimeFrame addTimeFrame(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeFrame(this, id, need, editorDescription)); }
	public final ElementTimeInDay addTimeInDay(String id, Need need, Text editorDescription) { return add(new ElementTimeInDay(this, id, need, editorDescription)); }
	public final ElementTimeInDay addTimeInDay(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeInDay(this, id, need, editorDescription)); }
	public final ElementTimeInWeek addTimeInWeek(String id, Need need, Text editorDescription) { return add(new ElementTimeInWeek(this, id, need, editorDescription)); }
	public final ElementTimeInWeek addTimeInWeek(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeInWeek(this, id, need, editorDescription)); }
	public final ElementTimeInMonth addTimeInMonth(String id, Need need, Text editorDescription) { return add(new ElementTimeInMonth(this, id, need, editorDescription)); }
	public final ElementTimeInMonth addTimeInMonth(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeInMonth(this, id, need, editorDescription)); }
	public final ElementTimeInYear addTimeInYear(String id, Need need, Text editorDescription) { return add(new ElementTimeInYear(this, id, need, editorDescription)); }
	public final ElementTimeInYear addTimeInYear(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeInYear(this, id, need, editorDescription)); }
	public final ElementTimeLimit addTimeLimit(String id, Need need, Text editorDescription) { return add(new ElementTimeLimit(this, id, need, editorDescription)); }
	public final ElementTimeLimit addTimeLimit(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeLimit(this, id, need, editorDescription)); }
	public final ElementTimeUnit addTimeUnit(String id, Need need, Text editorDescription) { return add(new ElementTimeUnit(this, id, need, editorDescription)); }
	public final ElementTimeUnit addTimeUnit(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTimeUnit(this, id, need, editorDescription)); }
	public final ElementTreeType addTreeType(String id, Need need, Text editorDescription) { return add(new ElementTreeType(this, id, need, editorDescription)); }
	public final ElementTreeType addTreeType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementTreeType(this, id, need, editorDescription)); }
	public final ElementUUID addUUID(String id, Need need, Text editorDescription) { return add(new ElementUUID(this, id, need, editorDescription)); }
	public final ElementUUID addUUID(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementUUID(this, id, need, editorDescription)); }
	public final ElementUUIDList addUUIDList(String id, Need need, Text editorDescription) { return add(new ElementUUIDList(this, id, need, editorDescription)); }
	public final ElementUUIDList addUUIDList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementUUIDList(this, id, need, editorDescription)); }
	public final ElementVehicleType addVehicleType(String id, Need need, Text editorDescription) { return add(new ElementVehicleType(this, id, need, editorDescription)); }
	public final ElementVehicleType addVehicleType(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementVehicleType(this, id, need, editorDescription)); }
	public final ElementWorld addWorld(String id, Need need, Text editorDescription) { return add(new ElementWorld(this, id, need, editorDescription)); }
	public final ElementWorld addWorld(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementWorld(this, id, need, editorDescription)); }
	public final ElementWorldList addWorldList(String id, Need need, Text editorDescription) { return add(new ElementWorldList(this, id, need, editorDescription)); }
	public final ElementWorldList addWorldList(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementWorldList(this, id, need, editorDescription)); }
	public final ElementWorldRestriction addWorldRestriction(String id, Need need, Text editorDescription) { return add(new ElementWorldRestriction(this, id, need, editorDescription)); }
	public final ElementWorldRestriction addWorldRestriction(String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementWorldRestriction(this, id, need, editorDescription)); }
	public final ElementWorldguardRegion addWorldguardRegion(ElementWorld world, String id, Need need, Text editorDescription) { return add(new ElementWorldguardRegion(this, world, id, need, editorDescription)); }
	public final ElementWorldguardRegion addWorldguardRegion(ElementWorld world, String id, Need need, SlotPlacement slot, Text editorDescription) { slot(id, slot); return add(new ElementWorldguardRegion(this, world, id, need, editorDescription)); }

}
