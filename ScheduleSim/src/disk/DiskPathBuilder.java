package disk;

import java.util.ArrayList;
import java.util.List;

/**
 * Records the head's path step by step. Schedulers call service(), sweepTo()
 * and jumpAndService(); the builder tracks the head position and the seek
 * sequence so the algorithms stay short.
 */
final class DiskPathBuilder {
    private final List<DiskStep> steps = new ArrayList<DiskStep>();
    private final List<Integer> serviceOrder = new ArrayList<Integer>();
    private int position;

    DiskPathBuilder(int startHead) {
        this.position = startHead;
    }

    /** Move to a requested cylinder and serve it. A duplicate costs 0 movement. */
    void service(int cylinder) {
        steps.add(new DiskStep(position, cylinder, DiskStep.Type.SERVICE));
        serviceOrder.add(cylinder);
        position = cylinder;
    }

    /** Move to the physical end of the disk (no request there). Skipped if already there. */
    void sweepTo(int endCylinder) {
        if (position == endCylinder) {
            return;
        }
        steps.add(new DiskStep(position, endCylinder, DiskStep.Type.SWEEP_TO_END));
        position = endCylinder;
    }

    /** Jump back across the disk to a pending request and serve it. Counted in the total. */
    void jumpAndService(int cylinder) {
        steps.add(new DiskStep(position, cylinder, DiskStep.Type.JUMP));
        serviceOrder.add(cylinder);
        position = cylinder;
    }

    DiskResult build(String algorithm, int startHead, int diskSize, Direction direction,
                     int requestCount) {
        return new DiskResult(algorithm, startHead, diskSize, direction,
                serviceOrder, steps, requestCount);
    }
}
