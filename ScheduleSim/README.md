# CPU and Disk Scheduling Simulator

ICS26012 - Operating Systems (UST). One Java program with a Swing GUI.

| Group | Algorithm |
|---|---|
| CPU scheduling | Shortest Remaining Time First (SRTF, preemptive), Round Robin (RR) |
| Disk scheduling | C-LOOK, SCAN |

Group No.: __   Section: ______   Repo: <paste repo link here>

## Requirements
- Java 8 or newer (JDK or JRE). Standard library only; no internet or database.

## Run
    java -jar ScheduleSim.jar

On most systems you can also double-click `ScheduleSim.jar`.

## Build from source (needs a JDK)
    sh build.sh        (Windows: build.bat)

## Run the algorithm tests
    sh run-tests.sh

## How to use
1. The main menu shows the four algorithms, grouped as CPU Scheduling and Disk Scheduling, plus About and Exit.
2. On any algorithm screen: **Load sample data** fills a practice problem, **Run** computes and shows the results, **Reset** clears the inputs and results, **Back to menu** returns.
3. CPU screens: enter PID, arrival time and burst time per process (10 rows to start; use Add process for more). RR also takes a time quantum. Output: Gantt chart with idle time, CT / TAT / WT / RT per process, the three averages, CPU utilization and throughput.
4. Disk screens: enter the request queue (comma separated), initial head position, disk size (default 200) and initial direction. Output: seek sequence, head movement per step, total head movement, average seek distance and a head path graph.

Invalid input (empty, negative, non-numeric, out of range) shows a clear message and never crashes the program.

## Conventions
- Time starts at 0 in whole units; idle CPU time is shown; context switch time is 0.
- CPU ties: earlier arrival, then lower PID (order of entry).
- SRTF preempts only if the newcomer's remaining time is strictly less.
- RR: a new arrival is queued before a preempted process at the same instant.
- TAT = CT - AT, WT = TAT - BT, RT = first CPU time - AT, utilization = busy / total x 100, throughput = processes / total time.
- Cylinders run 0 to N-1. SCAN goes to the physical disk end. C-LOOK turns at the last request and jumps to the farthest pending request on the other side; the jump counts in total head movement.
- Duplicate disk requests are served consecutively with 0 movement.

## Project layout
    src/Main.java                  entry point
    src/cpu/                       CPUScheduler (interface), SRTFScheduler, RoundRobinScheduler,
                                   SimProcess, GanttSlice, GanttBuilder, CPUResult
    src/disk/                      DiskScheduler (interface), ScanScheduler, CLookScheduler,
                                   Direction, DiskStep, DiskPathBuilder, DiskResult
    src/gui/                       MainFrame (CardLayout), MenuPanel, CpuPanel, DiskPanel,
                                   AboutPanel, GanttPanel, HeadPathPanel, InputParser,
                                   SampleData, AppInfo, UiKit, Navigator, InputException
    test/AlgoTest.java             expected-vs-actual checks for all four algorithms
    build.sh / build.bat           build ScheduleSim.jar
    run-tests.sh                   run the tests

## Before submitting
- Edit `src/gui/AppInfo.java`: group number, section, member names, roles.
- Put each owner's name in the header comment of their algorithm class.
- Replace `SampleData.java` with the facilitator's practice problem sets.
- Rebuild the JAR (`sh build.sh`) and re-test it.
