import cpu.*;
import disk.*;
import java.util.*;

public class AlgoTest {
    static int fails = 0;
    static void check(String name, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        if (!ok) fails++;
        System.out.println((ok ? "PASS " : "FAIL ") + name + " -> " + actual + (ok ? "" : "  (expected " + expected + ")"));
    }
    static String gantt(CPUResult r) {
        StringBuilder sb = new StringBuilder();
        for (GanttSlice s : r.getGantt()) sb.append(s.getPid()).append(s.getStart()).append("-").append(s.getEnd()).append(" ");
        return sb.toString().trim();
    }
    static String times(CPUResult r) {
        StringBuilder sb = new StringBuilder();
        for (SimProcess p : r.getProcesses())
            sb.append(p.getPid()).append(":").append(p.getCompletion()).append("/").append(p.getTurnaround()).append("/").append(p.getWaiting()).append("/").append(p.getResponse()).append(" ");
        return sb.toString().trim();
    }
    public static void main(String[] a) {
        List<SimProcess> in = Arrays.asList(new SimProcess("P1",0,5), new SimProcess("P2",1,3), new SimProcess("P3",2,1));
        CPUResult s = new SRTFScheduler().schedule(in, 0);
        check("SRTF gantt", gantt(s), "P10-1 P21-2 P32-3 P23-5 P15-9");
        check("SRTF CT/TAT/WT/RT", times(s), "P1:9/9/4/0 P2:5/4/1/0 P3:3/1/0/0");
        check("SRTF avg TAT", String.format("%.2f", s.getAverageTurnaround()), "4.67");
        check("SRTF avg WT", String.format("%.2f", s.getAverageWaiting()), "1.67");
        check("SRTF util", s.getCpuUtilization(), 100.0);

        CPUResult r = new RoundRobinScheduler().schedule(in, 2);
        check("RR gantt", gantt(r), "P10-2 P22-4 P34-5 P15-7 P27-8 P18-9");
        check("RR CT/TAT/WT/RT", times(r), "P1:9/9/4/0 P2:8/7/4/1 P3:5/3/2/2");
        check("RR avg TAT", String.format("%.2f", r.getAverageTurnaround()), "6.33");
        check("RR avg WT", String.format("%.2f", r.getAverageWaiting()), "3.33");
        check("RR avg RT", String.format("%.2f", r.getAverageResponse()), "1.00");

        // idle time
        List<SimProcess> idle = Arrays.asList(new SimProcess("P1",2,3), new SimProcess("P2",10,2));
        CPUResult i1 = new SRTFScheduler().schedule(idle, 0);
        check("SRTF idle gantt", gantt(i1), "IDLE0-2 P12-5 IDLE5-10 P210-12");
        check("SRTF idle util", String.format("%.2f", i1.getCpuUtilization()), "41.67");
        CPUResult i2 = new RoundRobinScheduler().schedule(idle, 4);
        check("RR idle gantt", gantt(i2), "IDLE0-2 P12-5 IDLE5-10 P210-12");

        // strict preemption: equal remaining must NOT preempt
        List<SimProcess> tie = Arrays.asList(new SimProcess("P1",0,4), new SimProcess("P2",2,2));
        check("SRTF no preempt on tie", gantt(new SRTFScheduler().schedule(tie, 0)), "P10-4 P24-6");

        // RR: new arrival queued before preempted process at same instant (P2 arrives at t=2 when P1's slice ends)
        List<SimProcess> rrTie = Arrays.asList(new SimProcess("P1",0,4), new SimProcess("P2",2,2), new SimProcess("P3",4,1));
        check("RR tie order", gantt(new RoundRobinScheduler().schedule(rrTie, 2)), "P10-2 P22-4 P14-6 P36-7");

        // disk
        List<Integer> req = Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67);
        DiskResult sc = new ScanScheduler().schedule(req, 53, 200, Direction.UP);
        check("SCAN order", sc.getServiceOrder(), "[65, 67, 98, 122, 124, 183, 37, 14]");
        check("SCAN total", sc.getTotalMovement(), 331);
        check("SCAN avg", String.format("%.3f", sc.getAverageSeek()), "41.375");
        DiskResult cl = new CLookScheduler().schedule(req, 53, 200, Direction.UP);
        check("CLOOK order", cl.getServiceOrder(), "[65, 67, 98, 122, 124, 183, 14, 37]");
        check("CLOOK total", cl.getTotalMovement(), 322);
        check("CLOOK avg", String.format("%.2f", cl.getAverageSeek()), "40.25");
        DiskResult scd = new ScanScheduler().schedule(req, 53, 200, Direction.DOWN);
        check("SCAN down order", scd.getServiceOrder(), "[37, 14, 65, 67, 98, 122, 124, 183]");
        check("SCAN down total", scd.getTotalMovement(), 53 + 183);   // 53->0 then 0->183
        DiskResult cld = new CLookScheduler().schedule(req, 53, 200, Direction.DOWN);
        check("CLOOK down order", cld.getServiceOrder(), "[37, 14, 183, 124, 122, 98, 67, 65]");
        check("CLOOK down total", cld.getTotalMovement(), (53-14) + (183-14) + (183-65));
        // edge: head at boundary, duplicates
        DiskResult dup = new CLookScheduler().schedule(Arrays.asList(10, 10, 50), 10, 200, Direction.UP);
        check("CLOOK dup order", dup.getServiceOrder(), "[10, 10, 50]");
        check("CLOOK dup total", dup.getTotalMovement(), 40);
        DiskResult edge = new ScanScheduler().schedule(Arrays.asList(199, 5), 199, 200, Direction.UP);
        check("SCAN head at end order", edge.getServiceOrder(), "[199, 5]");
        check("SCAN head at end total", edge.getTotalMovement(), 194);
        System.out.println(fails == 0 ? "ALL PASSED" : fails + " FAILED");
    }
}
