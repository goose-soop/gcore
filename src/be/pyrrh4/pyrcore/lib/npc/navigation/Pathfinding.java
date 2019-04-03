package be.pyrrh4.pyrcore.lib.npc.navigation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Pair;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class Pathfinding {

	// base data
	private World world;
	private Point start, target;
	private int step, speed, yToleranceUp, yToleranceDown;
	private double targetTolerance = 1d;
	private final List<Pair<Integer, Integer>> relativesOffsets;

	// processing
	private State state = State.WAITING;
	private BukkitTask task = null;
	private Set<Point> toExplore = null;
	private Set<Point> available = null;
	private Map<Point, Double> distances = null;
	private Set<Point> deadEnds = null;
	private Map<List<Point>, Double> paths = null;

	// constructor
	public Pathfinding(World world, Point start, Point target, int step, int speed, int yToleranceUp, int yToleranceDown) {
		this.world = world;
		this.start = start;
		this.target = target;
		this.step = step;
		this.speed = speed;
		this.yToleranceUp = yToleranceUp;
		this.yToleranceDown = yToleranceDown;
		this.relativesOffsets = Utils.asList(// horizontal movements
				new Pair<Integer, Integer>(0, step),
				new Pair<Integer, Integer>(0, -step),
				new Pair<Integer, Integer>(step, 0),
				new Pair<Integer, Integer>(-step, 0)
				);
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

	public int getStep() {
		return step;
	}

	public int getSpeed() {
		return speed;
	}

	public int getYToleranceUp() {
		return yToleranceUp;
	}

	public int getYToleranceDown() {
		return yToleranceDown;
	}

	public State getState() {
		return state;
	}

	// abstract methods
	protected abstract void onFail();
	protected abstract void onSuccess(List<Point> path);

	// methods
	public void start() {
		// invalid state
		if (!state.equals(State.WAITING)) {
			return;
		}
		// initialize exploration
		toExplore = Utils.asSet(start);
		available = new HashSet<Point>();
		distances = Utils.asMap(start, start.distance(target));
		deadEnds = new HashSet<Point>();
		paths = new HashMap<List<Point>, Double>();
		if (!isMatTraversable(Mat.from(world.getBlockAt(target.getX(), target.getY(), target.getZ())))) {
			targetTolerance = 3d;
		}
		// start exploring
		state = State.EXPLORING;
		task = new BukkitRunnable() {
			@Override
			public void run() {
				// explore
				if (state.equals(State.EXPLORING)) {
					int result = explore();
					if (result == 1) {// failed
						cancel();
						onFail();
						return;
					} else if (result == 2) {// success
						// initialize optimization
						state = State.OPTIMIZING;
					}
				}
				// optimize (eventually right after ending exploration above)
				if (state.equals(State.OPTIMIZING)) {
					// find shortest path
					List<List<Point>> sorted = Utils.asSortedList(paths.keySet(), new Comparator<List<Point>>() {
						@Override
						public int compare(List<Point> o1, List<Point> o2) {
							return Double.compare(paths.get(o1), paths.get(o2));
						}
					});
					List<Point> shortest = null;
					for (List<Point> path : sorted) {
						boolean valid = false;
						for (Point point : path) {
							if (point.distance(target) <= targetTolerance + 2d) {
								valid = true;
								break;
							}
						}
						if (valid) {
							shortest = path;
							break;
						}
					}
					// done
					state = State.DONE;
					cancel();
					onSuccess(shortest);
					return;
				}
			}
		}.runTaskTimerAsynchronously(PyrCore.inst(), 0L, 1L);
	}

	public void stop() {
		// invalid state
		if (!(state.equals(State.EXPLORING) || state.equals(State.OPTIMIZING))) {
			return;
		}
		// stop
		state = State.DONE;
		if (task != null) {
			task.cancel();
		}
	}

	/**
	 * @return 0 if still exploring, 1 if failed, 2 if success
	 */
	private int explore() {
		// explore known points (they're sorted by shorted distance, so we always start the loop at the closest known point to the target)
		int limit = 0;
		Point explore = null;
		while ((explore = getNextExplorationPoint()) != null && ++limit <= speed) {
			// remove point to explore and set as available
			toExplore.remove(explore);
			available.add(explore);
			// no path already contain this point
			boolean alreadyHasOnePath = false;
			for (List<Point> path : paths.keySet()) {
				if (path.contains(explore)) {
					alreadyHasOnePath = true;
					break;
				}
			}
			if (!alreadyHasOnePath) {
				// find closest path
				Pair<List<Point>, Double> closestPath = null;
				for (Pair<Integer, Integer> offset : relativesOffsets) {
					// get valid relative
					Point availableRelative = getAvailableForMoving(new Point(explore.getX() + offset.getA(), explore.getY(), explore.getZ() + offset.getB()));
					if (availableRelative == null) continue;
					// find all paths that contains that relative
					Map<List<Point>, Double> containing = new HashMap<List<Point>, Double>();
					for (List<Point> path : paths.keySet()) {
						if (path.contains(availableRelative)) {
							containing.put(path, paths.get(path));
						}
					}
					// none found
					if (containing.isEmpty()) continue;
					// get shortest
					List<Double> sorted = Utils.asSortedList(containing.values());
					double first = sorted.get(0);
					if (closestPath != null && closestPath.getB() < first) continue;// already has shortest with shorter total distance
					for (List<Point> path : containing.keySet()) {
						if (containing.get(path) == first) {
							closestPath = new Pair<List<Point>, Double>(path, first);
							break;
						}
					}
				}
				// add or create path
				if (closestPath != null) {
					List<Point> newPath = Utils.asListMultiple(closestPath.getA(), explore);
					double newDistance = closestPath.getB() + closestPath.getA().get(closestPath.getA().size() - 1).distance(explore);
					paths.put(newPath, newDistance);
				} else {
					paths.put(Utils.asList(start, explore), start.distance(explore));
				}
			}
			// get points around
			for (Pair<Integer, Integer> offset : relativesOffsets) {
				// known dead end
				Point relative = new Point(explore.getX() + offset.getA(), explore.getY(), explore.getZ() + offset.getB());
				if (deadEnds.contains(relative)) {
					continue;
				}
				// not available for moving, so new dead end
				Point availableRelative = getAvailableForMoving(relative);
				if (availableRelative == null) {
					deadEnds.add(relative);
					continue;
				}
				// known available point
				if (available.contains(availableRelative)) {
					continue;
				}
				// must already explore
				if (toExplore.contains(availableRelative)) {
					continue;
				}
				// add distance
				double distance = availableRelative.distance(target);
				distances.put(availableRelative, distance);
				// on target, done
				if (distance <= targetTolerance) {
					return 2;
				}
				// must explore
				toExplore.add(availableRelative);
			}
		}
		// no more blocks
		if (toExplore.isEmpty()) {
			return 1;
		}
		// still exploring
		return 0;
	}

	private Point getNextExplorationPoint() {
		// no more points to explore
		if (toExplore.isEmpty()) {
			return null;
		}
		// build distances list
		List<Double> list = new ArrayList<Double>();
		for (Point explore : toExplore) {
			list.add(distances.get(explore));
		}
		// sort distances
		List<Double> sorted = Utils.asSortedList(list);
		double first = sorted.get(0);
		for (Point explore : toExplore) {
			if (distances.get(explore) == first) {
				return explore;
			}
		}
		// error
		return null;
	}

	private Point getAvailableForMoving(Point point) {
		// not free
		if (!isMatTraversable(Mat.from(world.getBlockAt(point.getX(), point.getY(), point.getZ())))) {
			Point available = getFirstTraversableUp(point);
			// no available block to jump on, or too high, or it's a fence
			if (available == null || available.getY() - point.getY() > yToleranceUp) {
				return null;
			}
			// there's a fence below the target block
			Mat bmat = Mat.from(world.getBlockAt(available.getX(), available.getY() - 1, available.getZ()));
			if (bmat.toString().contains("FENCE") || bmat.equals(Mat.COBBLESTONE_WALL) || bmat.equals(Mat.MOSSY_COBBLESTONE_WALL)) {
				return null;
			}
			// not enough space above the target block to pass or jump up
			if (!isMatTraversable(Mat.from(world.getBlockAt(available.getX(), available.getY() + 1, available.getZ()))) || !isMatTraversable(Mat.from(world.getBlockAt(available.getX(), available.getY() + 2, available.getZ())))) {
				return null;
			}
			// good for jumping up
			return available;
		}
		// free
		else {
			// not enough heigh above the target block to pass or jump down
			if (!isMatTraversable(Mat.from(world.getBlockAt(point.getX(), point.getY() + 1, point.getZ())))) {
				return null;
			}
			Point available = getLastTraversableDown(point);
			// no available block to pass or jump on, or too low
			if (available == null || point.getY() - available.getY() > yToleranceDown) {
				return null;
			}
			// good for passing or jumping down
			return available;
		}
	}

	private Point getLastTraversableDown(Point point) {
		int y = point.getY();
		while (y-- > 0) {// -- so we check the argument on first iteration
			Block block = world.getBlockAt(point.getX(), y, point.getZ());
			if (!isMatTraversable(Mat.from(block))) {
				return new Point(point.getX(), y + 1, point.getZ());
			}
		}
		return null;
	}

	private Point getFirstTraversableUp(Point point) {
		int y = point.getY();
		while (y++ < 255) {// ++ so we check the argument on first iteration
			Block block = world.getBlockAt(point.getX(), y, point.getZ());
			if (isMatTraversable(Mat.from(block))) {
				return new Point(point.getX(), y, point.getZ());
			}
		}
		return null;
	}

	private static final List<String> TRAVERSABLE = Utils.asList("SAPLING", "RAIL", "GRASS", "BUSH", "DANDELION", "ORCHID", "BLUET", "TULIP", "DAISY", "MUSHROOM", "TORCH", "WHEAT", "SIGN", "LEVER", "PLATE", "PATH", "DOOR", "BUTTON", "CANE", "REPEATER", "COMPARATOR", "TRAPDOOR", "STEM", "VINE", "GATE", "LILY_PAD", "WART", "PORTAL", "TRIPWIRE", "CARROTS", "POTATOES", "CARPET", "FLOWER", "LILAC", "FERN", "BUSH", "BANNER", "PEONY", "END_GATEWAY");
	private static final List<Mat> TRAVERSABLE_EXCEPTIONS = Utils.asList(Mat.GRASS_BLOCK, Mat.GRASS_PATH, Mat.REDSTONE_BLOCK, Mat.REDSTONE_LAMP);

	private boolean isMatTraversable(Mat mat) {
		if (mat.isAir()) return true;
		String smat = mat.getModernName();
		for (String trav : TRAVERSABLE) {
			if (smat.contains(trav)) {
				return !TRAVERSABLE_EXCEPTIONS.contains(mat);
			}
		}
		return false;
	}

	// state
	public static enum State {
		WAITING, EXPLORING, OPTIMIZING, DONE;
	}

}
