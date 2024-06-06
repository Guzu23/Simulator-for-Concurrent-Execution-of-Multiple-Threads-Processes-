public class Process{
    String name;
    int start_time;
    int phases_count = 2;
    char alias;
    int id;
    Phase[] phases;
    int currentPhaseIndex = 0;
    int penalty_points = 0;
    int reward_points = 0;
    boolean process_done = false;
    boolean process_exited = false;
    int q = 1;
    boolean added = false;
    
    Process(String name, int start_time, int phases_count, int i){
        this.name=name;
        this.start_time=start_time;
        this.phases_count=phases_count;
        this.phases = new Phase[phases_count];
        this.alias = (char) ('A' + i);
        this.id = i+1;
    }

    public void reward(){
        this.reward_points++;
        if (this.reward_points >= TS.REWARD_LIMIT){
            if(this.q>1) {
                this.q--;
                this.reward_points -= TS.REWARD_LIMIT;
            }
        }
    }

    public void penalty(){
        this.penalty_points++;
        TS.moving = true;
        if (this.penalty_points >= TS.PENALTY_LIMIT){ 
            if (q < 3){
                this.q++;
                this.penalty_points-=TS.PENALTY_LIMIT;
            }
        }
        TS.customQueue[q-1].Q.offerLast(this);
    }
}