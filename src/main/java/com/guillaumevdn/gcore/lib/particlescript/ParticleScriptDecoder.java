package com.guillaumevdn.gcore.lib.particlescript;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.particle.Particle;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ParticleScriptDecoder {

	// ----- base
	private final GPlugin plugin;
	private final File file;
	private final ParticleScript script;

	public ParticleScriptDecoder(GPlugin plugin, File file, String id) {
		this.plugin = plugin;
		this.file = file;
		this.script = new ParticleScript(plugin, id);
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final File getFile() {
		return file;
	}

	public final ParticleScript getScript() {
		return script;
	}

	// ----- decode
	public void decode() throws Throwable {
		// read lines without comments
		List<String> lines = new ArrayList<>();
		List<String> fileLines = FileUtils.readLines(file);
		for (String line : fileLines) {
			int commentIndex = line.indexOf('#');
			if (commentIndex != -1) {
				line = line.substring(0, commentIndex);
			}
			line = line.replace("\t", "  ");
			if (!line.trim().isEmpty()) {
				lines.add(line);
			}
		}
		// find indent
		String firstIndented = lines.stream().filter(elem -> elem.startsWith(" ")).findFirst().orElse(null);
		int indentLevel = firstIndented != null ? StringUtils.countLeadingChar(firstIndented, ' ') : 2;
		// consume lines
		consume(Collections.unmodifiableList(lines), 0, 0, indentLevel, new HashMap<>());
	}

	protected void consume(List<String> lines, int index, int indent, int indentLevel, Map<String, Double> localVars) {
		// no matching indent
		if (StringUtils.countLeadingChar(lines.get(index), ' ') != indent) {
			plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured : invalid indent level at line '" + lines.get(index)
					+ "', expected " + StringUtils.pluralizeAmountDesc("space", indent));
			return;
		}
		// build line (with local variables)
		String trimmed = lines.get(index).trim();
		for (String var : localVars.keySet()) {
			int i = trimmed.toLowerCase().indexOf(var);
			if (i != -1) {
				trimmed = trimmed.substring(0, i) + localVars.get(var) + trimmed.substring(i + var.length());
			}
		}
		// read function and parameters
		String trimmedLower = trimmed.toLowerCase();
		int paramsIndex = trimmedLower.indexOf('(');
		if (paramsIndex == -1 || trimmedLower.charAt(trimmedLower.length() - 1) != ')') {
			plugin.getMainLogger()
					.error("On decode of script " + file.getName() + ", an error occured : invalid function/parenthesis at line '" + trimmed + "'");
			return;
		}
		String function = trimmedLower.substring(0, paramsIndex).trim();
		String[] params = trimmedLower.substring(paramsIndex + 1, trimmedLower.length() - 1).trim().split(",");
		// loop
		if (function.equals("loop")) {
			// decode params
			String var;
			double begin, end, step;
			try {
				var = params[0].trim();
				begin = Double.parseDouble(params[1].trim());
				end = Double.parseDouble(params[2].trim());
				step = Double.parseDouble(params[3].trim());
			} catch (Throwable exception) {
				exception.printStackTrace();
				plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured while parsing params of line '" + trimmed + "'");
				return;
			}
			// when does it end ?
			List<String> loopLines = new ArrayList<>();
			++index;
			for (; index < lines.size(); ++index) {
				if (StringUtils.countLeadingChar(lines.get(index), ' ') < indent + indentLevel) {
					break;
				}
				loopLines.add(lines.get(index));
			}
			// loop
			for (double i = begin; i <= end; i += step) {
				Map<String, Double> newLocalVars = CollectionUtils.asMap(localVars);
				newLocalVars.put(var, i);
				consume(loopLines, 0, indent + indentLevel, indentLevel, newLocalVars);
			}
			// keep consuming
			if (index < lines.size()) {
				consume(lines, index, indent, indentLevel, localVars);
			}
			return;
		}
		// display
		else if (function.equals("display")) {
			// decode params
			Particle effect;
			String x, y, z, count;
			String red = null, green = null, blue = null, noteColor = null;
			try {
				effect = Particle.firstFromIdOrDataName(params[0].trim()).orNull();
				if (effect == null) {
					plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured while parsing params of line '" + trimmed
							+ "' : unknown particle effect '" + params[0] + "'");
				}
				x = params[1].trim();
				y = params[2].trim();
				z = params[3].trim();
				count = params[4].trim();
				if (params.length == 6) {
					noteColor = params[5].trim();
				} else if (params.length == 8) {
					red = params[5].trim();
					green = params[6].trim();
					blue = params[7].trim();
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured while parsing params of line '" + trimmed + "'");
				return;
			}
			// add operation
			script.getOperations().add(new OperationDisplay(effect, x, y, z, count, red, green, blue, noteColor));
		}
		// set
		else if (function.equals("set")) {
			// decode params
			String variable, value;
			try {
				variable = params[0].trim();
				value = params[1].trim();
			} catch (Throwable exception) {
				exception.printStackTrace();
				plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured while parsing params of line '" + trimmed + "'");
				return;
			}
			// add operation
			script.getOperations().add(new OperationSet(variable, value));
		}
		// wait_ticks
		else if (function.equals("wait_ticks")) {
			// decode params
			String ticks;
			try {
				ticks = params[0].trim();
			} catch (Throwable exception) {
				exception.printStackTrace();
				plugin.getMainLogger().error("On decode of script " + file.getName() + ", an error occured while parsing params of line '" + trimmed + "'");
				return;
			}
			// add operation
			script.getOperations().add(new OperationWaitTicks(ticks));
		}
		// other operation
		else {
			plugin.getMainLogger().error("On decode of script " + file.getName() + ", unknown operation in line '" + trimmed + "'");
			return;
		}
		// keep consuming
		if (++index < lines.size()) {
			consume(lines, index, indent, indentLevel, localVars);
		}
	}

}
