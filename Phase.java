public class Phase{
    int CPU_TIMES_COUNT;
    int IO_TIMES_COUNT;
    int REPEAT_COUNT;
    boolean phase_done = false;
    int CPU_CURRENT_TIME=0;
    int IO_CURRENT_TIME=0;
    int CYCLES_DONE=0;

    Phase(int CPU_TIMES_COUNT, int IO_TIMES_COUNT, int REPEAT_COUNT){
        this.CPU_TIMES_COUNT=CPU_TIMES_COUNT;
        this.IO_TIMES_COUNT=IO_TIMES_COUNT;
        this.REPEAT_COUNT=REPEAT_COUNT;
    }
}