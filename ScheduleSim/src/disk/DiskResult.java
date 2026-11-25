package disk;

import java.util.Collections;
import java.util.List;

/** Everything a disk scheduling run produces. */
public final class DiskResult {
    private final String algorithm;
    private final int startHead;
    private final int diskSize;
    private final Direction direction;
    private final List<Integer> serviceOrder;
    private final List<DiskStep> steps;
    private final int requestCount;
    private final int totalMovement;

    DiskResult(String algorithm, int startHead, int diskSize, Direction direction,
               List<Integer> serviceOrder, List<DiskStep> steps, int requestCount) {
        this.algorithm = algorithm;
        this.startHead = startHead;
        this.diskSize = diskSize;
        this.direction = direction;
        this.serviceOrder = Collections.unmodifiableList(serviceOrder);
        this.steps = Collections.unmodifiableList(steps);
        this.requestCount = requestCount;

        int total = 0;
        for (DiskStep s : steps) {
            total += s.getDistance();
        }
        this.totalMovement = total;
    }

    public String getAlgorithm() { return algorithm; }
    public int getStartHead() { return startHead; }
    public int getDiskSize() { return diskSize; }
    public Direction getDirection() { return direction; }

    /** The order in which requests are served (the seek sequence). */
    public List<Integer> getServiceOrder() { return serviceOrder; }

    /** Every head move, including trips to the disk end and return jumps. */
    public List<DiskStep> getSteps() { return steps; }

    /** Sum of |difference| between successive head positions. */
    public int getTotalMovement() { return totalMovement; }

    /** Average seek distance = total head movement / number of requests. */
    public double getAverageSeek() {
        return requestCount == 0 ? 0 : (double) totalMovement / requestCount;
    }
}
