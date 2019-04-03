package be.pyrrh4.pyrcore.lib.npc.navigation;

import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import be.pyrrh4.pyrcore.PyrCore;

public abstract class SimpleNavigator implements Navigator {

	// base data
	private World world;
	private Point start, target;
	private long ticksPerStep;

	// running
	private State state = State.WAITING;
	private BukkitTask task = null;
	private Integer index = null;
	private List<Point> path;
	private Point currentStep = null;

	// constructor
	public SimpleNavigator(World world, Point start, Point target, List<Point> path, long ticksPerStep) {
		this.world = world;
		this.path = path;
		this.ticksPerStep = ticksPerStep;
	}

	// get
	public World getWorld() {
		return world;
	}

	public Point getStart() {
		return start;
	}

	public Point getTarget() {
		return target;
	}

	public List<Point> getPath() {
		return path;
	}

	public Point getCurrentStep() {
		return currentStep;
	}

	// abstract methods
	protected abstract void onStep(Point step);
	protected abstract void onSuccess();

	// methods
	public void start() {
		// invalid state
		if (!state.equals(State.WAITING)) {
			return;
		}
		// start travelling
		state = SimpleNavigator.State.TRAVELLING;
		index = -1;
		SimpleNavigator.this.path = path;
		task = new BukkitRunnable() {
			@Override
			public void run() {
				// done
				if (++index >= path.size()) {
					state = SimpleNavigator.State.DONE;
					SimpleNavigator.this.onSuccess();
					cancel();
					return;
				}
				// step
				onStep(currentStep = path.get(index));
			}
		}.runTaskTimer(PyrCore.inst(), 0L, ticksPerStep);
	}

	public void stop() {
		// travelling
		if (state.equals(State.TRAVELLING)) {
			state = State.DONE;
			if (task != null) {
				task.cancel();
			}
		}
	}

	// state
	public static enum State {
		WAITING, TRAVELLING, DONE;
	}

}
