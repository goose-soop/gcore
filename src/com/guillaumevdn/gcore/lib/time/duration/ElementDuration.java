package com.guillaumevdn.gcore.lib.time.duration;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementTimeUnit;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.TimeUnit;

/**
 * @author GuillaumeVDN
 */
public class ElementDuration extends ParseableContainerElement<Long> {

	protected ElementInteger time;
	protected ElementTimeUnit unit;

	public ElementDuration(Element parent, String id, Need need, Integer defaultTime, TimeUnit defaultUnit, Text editorDescription) {
		super(parent, id, need, editorDescription);

		// we need to overwrite the doWrite methods here, because when editing from the editor, calling superElement.onChange() triggers the write method of this thing and it'll write as section

		time = add(new ElementInteger(this, "time", Need.optional(defaultTime), 0, TextEditorGeneric.descriptionDurationTime) {
			@Override
			protected void doWrite() throws Throwable {
				ElementDuration.this.doWrite();
			};
		});
		unit = add(new ElementTimeUnit(this, "unit", Need.optional(defaultUnit), TextEditorGeneric.descriptionDurationUnit) {
			@Override
			protected void doWrite() throws Throwable {
				ElementDuration.this.doWrite();
			};
		});
	}

	// ----- get
	public ElementInteger getTime() {
		return time;
	}

	public ElementTimeUnit getUnit() {
		return unit;
	}

	// ----- set
	public void setValue(long milliseconds) {
		if (milliseconds < 1000L || milliseconds % 1000L != 0L) {
			time.setValue(CollectionUtils.asList("" + milliseconds));
			unit.setValue(CollectionUtils.asList(TimeUnit.MILLISECOND.name()));
		} else if (milliseconds / 1000L % 60L != 0L) {
			time.setValue(CollectionUtils.asList("" + (milliseconds / 1000L)));
			unit.setValue(CollectionUtils.asList(TimeUnit.SECOND.name()));
		} else if (milliseconds / 1000L / 60L % 60L != 0L) {
			time.setValue(CollectionUtils.asList("" + (milliseconds / 1000L / 60L)));
			unit.setValue(CollectionUtils.asList(TimeUnit.MINUTE.name()));
		} else if (milliseconds / 1000L / 60L / 60L % 24L != 0L) {
			time.setValue(CollectionUtils.asList("" + (milliseconds / 1000L / 60L / 60L)));
			unit.setValue(CollectionUtils.asList(TimeUnit.HOUR.name()));
		} else {
			time.setValue(CollectionUtils.asList("" + (milliseconds / 1000L / 60L / 60L / 24L)));
			unit.setValue(CollectionUtils.asList(TimeUnit.DAY.name()));
		}
	}

	// ----- load
	@Override
	protected void doRead() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		try {
			String[] value = config.readString(path, "").split(" ");
			if (value.length != 2) {
				getSuperElement().addLoadError("Invalid " + getTypeName() + " at path " + path + ", is should be <time amount> <time unit>");
			} else {
				this.time.setValue(CollectionUtils.asList(value[0]));
				this.unit.setValue(CollectionUtils.asList(value[1].toLowerCase().endsWith("s") ? value[1].substring(0, value[1].length() - 1) : value[1]));
			}
		} catch (Throwable exception) {
			ConfigError configError = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (configError != null) {
				getSuperElement().addLoadError(configError.getMessage().replace(", in file " + config.getLogFilePath(), ""));
			}
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		String time = this.time.getRawValueLine(0);
		String defaultTime = this.time.getDefaultValueLine(0);
		String unit = this.unit.getRawValueLine(0);
		String defaultUnit = this.unit.getDefaultValueLine(0);

		if ((time == null && unit == null) || (!getNeed().equals(NeedType.REQUIRED) && StringUtils.equalsIgnoreCaseNullable(time, defaultTime) && StringUtils.equalsIgnoreCaseNullable(unit, defaultUnit))) {
			config.write(path, null);
		} else {
			config.write(path, (time != null ? time : (defaultTime != null ? defaultTime : 1)) + " " + (unit != null ? unit : (defaultUnit != null ? defaultUnit : TimeUnit.SECOND)));
		}
	}

	// ----- parse
	@Override
	public Long doParse(Replacer replacer) throws ParsingError {
		// don't contain and optional, means it's null : don't throw parsing errors
		if (!readContains() && !isRequiredInContext() && time.getDefaultValue() == null && unit.getDefaultValue() == null) {
			return null;
		}
		int time = this.time.parseNoCatchOrThrowParsingNull(replacer);
		TimeUnit unit = this.unit.parseNoCatchOrThrowParsingNull(replacer);
		return unit.toMillis(time);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

	@Override
	public List<String> editorCurrentValue() {
		Integer time = this.time.parseGeneric().orNull();
		TimeUnit unit = this.unit.parseGeneric().orNull();
		return time == null || unit == null ? null : CollectionUtils.asList(time + " " + unit.name());
	}

}
