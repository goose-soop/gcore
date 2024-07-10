package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.compatibility.particle.Particle;
import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public class OperationDisplay implements Operation {

	// ----- base
	private Particle effect;
	private String x, y, z, count;
	private String red, green, blue;
	private String noteColor;

	public OperationDisplay(Particle effect, String x, String y, String z, String count, String red, String green, String blue, String noteColor) {
		this.effect = effect;
		this.x = x;
		this.y = y;
		this.z = z;
		this.count = count;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.noteColor = noteColor;
	}

	// ----- get
	public Particle getEffect() {
		return effect;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public String getZ() {
		return z;
	}

	public String getCount() {
		return count;
	}

	public String getRed() {
		return red;
	}

	public String getGreen() {
		return green;
	}

	public String getBlue() {
		return blue;
	}

	public String getNoteColor() {
		return noteColor;
	}

	// ----- methods
	@Override
	public int perform(final ParticleScriptExecution execution, final Collection<Player> players, boolean isAsync) {
		final Double x = execution.parseAndCalculate(this.x);
		final Double y = execution.parseAndCalculate(this.y);
		final Double z = execution.parseAndCalculate(this.z);
		final Integer red = execution.parseAndCalculateInteger(this.red);
		final Integer green = execution.parseAndCalculateInteger(this.green);
		final Integer blue = execution.parseAndCalculateInteger(this.blue);
		final Integer noteColor = execution.parseAndCalculateInteger(this.noteColor);
		final int count = execution.parseAndCalculateInteger(this.count);
		if (x != null && y != null && z != null) {
			Color color = NumberUtils.isInRange(red, 0, 255) && NumberUtils.isInRange(red, 0, 255) && NumberUtils.isInRange(red, 0, 255)
					? Color.fromRGB(red, green, blue)
					: null;
			BukkitThread.regular(isAsync).operate(execution.getPlugin(), () -> {
				Location loc = new Location(execution.getBaseLocation().getWorld(), x, y, z);
				effect.send(players != null ? players : loc.getWorld().getPlayers(), loc, color, noteColor, count, 0f);
			});
		}
		return 0;
	}

}
