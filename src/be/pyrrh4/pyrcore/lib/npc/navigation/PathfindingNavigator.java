package be.pyrrh4.pyrcore.lib.npc.navigation;

import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import be.pyrrh4.pyrcore.PyrCore;

public abstract class PathfindingNavigator implements Navigator {

	// base data
	private World world;
	private Point start, target;
	private int pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown;
	private long ticksPerStep;

	// running
	private State state = State.WAITING;
	private Pathfinding pathfinding = null;
	private BukkitTask task = null;
	private Integer index = null;
	private List<Point> path = null;
	private Point currentStep = null;

	// constructor
	public PathfindingNavigator(World world, Point start, Point target, int pathfindingStep, int pathfindingSpeed, int yToleranceUp, int yToleranceDown, long ticksPerStep) {
		this.world = world;
		this.start = start;
		this.target = target;
		this.pathfindingStep = pathfindingStep;
		this.pathfindingSpeed = pathfindingSpeed;
		this.yToleranceUp = yToleranceUp;
		this.yToleranceDown = yToleranceDown;
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
	protected abstract void onFail();
	protected abstract void onSuccess();

	// methods
	public void start() {
		// invalid state
		if (!state.equals(State.WAITING)) {
			return;
		}
		// find path
		state = State.FINDING_PATH;
		new Pathfinding(world, start, target, pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown) {
			@Override
			protected void onFail() {
				state = PathfindingNavigator.State.DONE;
				PathfindingNavigator.this.onFail();
			}
			@Override
			protected void onSuccess(final List<Point> path) {
				state = PathfindingNavigator.State.TRAVELLING;
				index = -1;
				PathfindingNavigator.this.path = path;
				task = new BukkitRunnable() {
					@Override
					public void run() {
						// done
						if (++index >= path.size()) {
							state = PathfindingNavigator.State.DONE;
							PathfindingNavigator.this.onSuccess();
							cancel();
							return;
						}
						// step
						onStep(currentStep = path.get(index));
					}
				}.runTaskTimer(PyrCore.inst(), 0L, ticksPerStep);
			}
		}.start();
	}

	public void stop() {
		// pathfinding
		if (state.equals(State.FINDING_PATH)) {
			state = State.DONE;
			pathfinding.stop();
		}
		// travelling
		else if (state.equals(State.TRAVELLING)) {
			state = State.DONE;
			if (task != null) {
				task.cancel();
			}
		}
	}

	// state
	public static enum State {
		WAITING, FINDING_PATH, TRAVELLING, DONE;
	}

}
