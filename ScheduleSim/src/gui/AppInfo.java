package gui;

/**
 * Group details shown on the About screen. The Leader edits ONLY this file to
 * fill in the group number, section, and member names.
 */
final class AppInfo {
    static final String TITLE = "CPU and Disk Scheduling Simulator";
    static final String COURSE = "ICS26012 - Operating Systems";
    static final String TERM = "1st Semester, AY 2026-2027";
    static final String FACILITATOR = "Asst. Prof. Bernard G. Sanidad, DIT, DBA";

    static final String GROUP_NO = "__";
    static final String SECTION = "______";

    /** { member name, assigned algorithm, coordination role } */
    static final String[][] MEMBERS = {
        {"Member 1 (replace)", "Shortest Remaining Time First (SRTF)", "Leader"},
        {"Member 2 (replace)", "Round Robin (RR)", "Documentation Lead"},
        {"Member 3 (replace)", "SCAN", "Video Lead"},
        {"Member 4 (replace)", "C-LOOK", "Testing Lead"},
    };

    private AppInfo() { }
}
