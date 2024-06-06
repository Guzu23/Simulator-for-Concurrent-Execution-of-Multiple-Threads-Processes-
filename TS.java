import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TS extends Frame {     
    String path, dir;
    URL classFile;            
    String[] data;   
    static Process[] processes;
    static int processes_count;
    static CustomCPUQueue[] customQueue = new CustomCPUQueue[3]; //It contains a Deque, a priority q, and the time left(quantum) for each Queue.
    static int PENALTY_LIMIT, REWARD_LIMIT;
    static Deque<Process> IO;
    static long current_time = 0;
    static int current_queue = 1;
    Process currentCPUProcess, currentIOProcess, holdForIO;
    boolean held = false;
    int moveFromCPUToQueue = 0;
    int moveFromIOToQueue = 0;
    boolean completedCPUCycle = false;
    static boolean moving = false;
    Html html;

    //*proiect* Outputul este out.htm ca sa nu il inlocuiasca pe cel original si pentru a se putea face comparari
    public void simulate(){
        for (int i=0; i<=2; i++) customQueue[i] = new CustomCPUQueue();
        IO = new LinkedList<>();

        readProcesses();
        html = new Html();

        while (!simulationDone()) processQueues();
        processQueues();

        html.finishHTMLelements();
    }

    public boolean simulationDone() {
        for (int i=0; i<processes_count; i++) if (!processes[i].process_done) return false;
        System.out.println("Simulation done!");
        return true;
    }

    //The logic====================================================================================
    public void processQueues() {
        addProcesses();
        current_queue=0;
        for (int i=0; i<=2; i++){
            if(!customQueue[i].Q.isEmpty()){
                current_queue = i+1;
                if(customQueue[i].quantum == 0) customQueue[i].quantum = customQueue[i].q;
                break;
            }
        }
        if(current_queue==0) {
            html.resetStrings();
            processIO();
            moveFromIOToQueue=0;
            current_time++;
            return;
        }

        while(customQueue[current_queue-1].quantum>0){
            if (customQueue[0].Q.isEmpty() && customQueue[1].Q.isEmpty() && customQueue[2].Q.isEmpty() && IO.isEmpty()) break; //Simulation is done
            if (current_queue > 1 && !customQueue[0].Q.isEmpty()) break; //We are working on Q2/Q3 and Q1 receives a process
            if (current_queue == 3 && !customQueue[1].Q.isEmpty()) break; //We are working on Q3 and Q2 receives a process
            if (completedCPUCycle){
                completedCPUCycle = false;
                //*proiect*
                //Daca un proces isi termina ciclul si intra in IO la timpul 14, timpul cozii Q1 se reseteaza, 
                //astfel procesul B este penalizat la dupa timpul 20, nu dupa timpul 19, dar la 168 timpul cozii Q2 nu se reseteaza
                //dupa ce procesul C termina un ciclu, iar procesul E nu mai e penalizat dupa timpul 168. De ce?
                //La timpul 154 la fel. Timpul cozii se reseteza dupa ce A isi termina ciclul si intra in IO
                //Fiindca uneori se reseteaza timpul cozii, iar alteori nu, tabelele din output.htm si out.htm o iau pe cai diferite
                //Care e conditia exacta de a se reseta timpul cozii?
                
                //Cumva fiecare coada are timpul ei care scade separat si acest timp se reseteaza cand in coada respectiva se termina un ciclu CPU al unui proces?
                //Daca e adevarat si corectam avem iar probleme la timpul 344 unde nu se sincronizeaza penalizarile si timpurile cozilor, 
                // probleme cauzate cel mai probabil de logica programului scris de mine.
                break;
            }
            customQueue[current_queue-1].quantum--;
            html.resetStrings();
            
            processCPU();
            processIO();
            if(held){     
                held=false;
                IO.offer(holdForIO);
            }

            //If a process had a penalty and was moved to an inferior queue and on the same time a process from IO moves to the same queue:
            if (moving) orderProcesses();   
            moving = false;
            moveFromCPUToQueue = 0;
            moveFromIOToQueue = 0;  
            current_time++;
        }  
    }

    public void processCPU() {
        addProcesses();

        if (current_queue == 1 && customQueue[0].Q.isEmpty()) current_queue = 2;
        if (current_queue == 2 && customQueue[1].Q.isEmpty()) current_queue = 3;
        if (current_queue == 3 && customQueue[2].Q.isEmpty()) return;

        currentCPUProcess = customQueue[current_queue-1].Q.poll();
        
        html.cpuProcess = "            <TD align=\"center\">CPU: <A HREF=\"#p" + currentCPUProcess.id + "\">" + currentCPUProcess.alias + "</A></TD>\n";
        html.q1Queue = "            <TD align=\"center\">" + showElementsFromDeque(customQueue[0].Q) +"</TD>\n";
        html.q2Queue = "            <TD align=\"center\">" + showElementsFromDeque(customQueue[1].Q) +"</TD>\n";
        html.q3Queue = "            <TD align=\"center\">" + showElementsFromDeque(customQueue[2].Q) +"</TD>\n";

        if(currentCPUProcess.process_done) {
            currentCPUProcess.process_exited = true;
            return;
        }

        Phase currentPhase = currentCPUProcess.phases[currentCPUProcess.currentPhaseIndex];
        currentPhase.CPU_CURRENT_TIME++;

        if (currentPhase.CPU_CURRENT_TIME == currentPhase.CPU_TIMES_COUNT){
                currentPhase.CPU_CURRENT_TIME = 0;
                currentCPUProcess.phases[currentCPUProcess.currentPhaseIndex] = currentPhase;
                currentCPUProcess.reward();
                completedCPUCycle=true;
                customQueue[current_queue-1].quantum = customQueue[current_queue-1].q;
                held = true;
                holdForIO = currentCPUProcess;
                return;
        }
        else if (customQueue[current_queue - 1].quantum == 0) currentCPUProcess.penalty();
        else customQueue[currentCPUProcess.q-1].Q.offerFirst(currentCPUProcess);
        moveFromCPUToQueue = currentCPUProcess.q;
    }

    public void processIO() {
        if (!IO.isEmpty()) {
            currentIOProcess = IO.poll();

            html.ioProcess = "            <TD align=\"center\">I/O: <A HREF=\"#p" + currentIOProcess.id + "\">" + currentIOProcess.alias + "</A></TD>\n";
            html.ioQueue = "            <TD align=\"center\">" + showElementsFromDeque(IO) +"</TD>\n";

            Phase currentPhase = currentIOProcess.phases[currentIOProcess.currentPhaseIndex];
            currentPhase.IO_CURRENT_TIME ++;

            if (currentPhase.IO_CURRENT_TIME  == currentPhase.IO_TIMES_COUNT) {
                currentPhase.IO_CURRENT_TIME  = 0; 
                currentPhase.CYCLES_DONE++;
                if (currentPhase.CYCLES_DONE == currentPhase.REPEAT_COUNT){
                    currentPhase.phase_done = true;
                    currentIOProcess.currentPhaseIndex++;
                    if (currentIOProcess.currentPhaseIndex == currentIOProcess.phases_count) currentIOProcess.process_done = true;
                }
                else currentIOProcess.phases[currentIOProcess.currentPhaseIndex] = currentPhase;
                customQueue[currentIOProcess.q-1].Q.offerLast(currentIOProcess);
                moveFromIOToQueue = currentIOProcess.q;
            } else {
                currentIOProcess.phases[currentIOProcess.currentPhaseIndex] = currentPhase;
                IO.offerFirst(currentIOProcess);   
            }
        }
        html.addLineHTML();
        try{
            if(currentIOProcess.phases[currentIOProcess.currentPhaseIndex-1].phase_done){
                html.sb.append("        <TR>");
                html.sb.append("            <TD align=\"center\" COLSPAN=7 BGCOLOR=yellow>Phase #" + currentIOProcess.currentPhaseIndex + " of the Process #" + currentIOProcess.id + " is finished.</TD>");
                html.sb.append("        </TR>");
                currentIOProcess.phases[currentIOProcess.currentPhaseIndex-1].phase_done = false;
            }
        }
        catch(Exception e){}
        if (currentCPUProcess.process_exited){
                html.sb.append("        <TR>");
                html.sb.append("            <TD align=\"center\" COLSPAN=7 BGCOLOR=red>Process #" + currentCPUProcess.id + " is finished.</TD>");
                html.sb.append("        </TR>");
                currentCPUProcess.process_exited = false;
        }
    }
    //The logic====================================================================================
    
    //When 2 processes enter the queue in the same time
    public void orderProcesses(){
        if (moveFromCPUToQueue==moveFromIOToQueue && moveFromCPUToQueue!=0){
            for (int i=0; i<=2; i++){
                if (current_queue == i+1){
                    Process swap1, swap2;
                    swap1 = customQueue[i].Q.pollLast();
                    swap2 = customQueue[i].Q.pollLast();
                    if (swap1.id < swap2.id){
                        customQueue[i].Q.offerLast(swap1);
                        customQueue[i].Q.offerLast(swap2);
                    }
                    else{
                        customQueue[i].Q.offerLast(swap2);
                        customQueue[i].Q.offerLast(swap1);
                    }
                    break;
                }
            }
        }
    }

    public void readProcesses(){ 
        Pattern pattern = Pattern.compile("(\\w+)=(\\d+)");
        Matcher matcher = pattern.matcher(data[12]);
        while (matcher.find()) {
            String variable = matcher.group(1);
            int value = Integer.parseInt(matcher.group(2));
            switch (variable) {
                case "q1":
                    customQueue[0].q = value;
                    break;
                case "q2":
                    customQueue[1].q = value;
                    break;
                case "q3":
                    customQueue[2].q = value;
                    break;
                case "k":
                    PENALTY_LIMIT = value;
                    break;
                case "r":
                    REWARD_LIMIT = value;
                    break;
            }
        }

        pattern = Pattern.compile("(\\w+)=(\\d+)");
        matcher = pattern.matcher(data[16]);
        if (matcher.find()) {
            processes_count = Integer.parseInt(matcher.group(2));
            processes = new Process[processes_count];
        }
        int line=18;
        pattern = Pattern.compile("Process\\s*#\\d+");
        for (int i=0; i<processes_count; i++){  
            while(true){
                matcher = pattern.matcher(data[line]);
                if (matcher.find()) {
                    line++;
                    pattern = Pattern.compile("(\\w+)=(.*)");
                    matcher = pattern.matcher(data[line]);
                    String name = "";
                    if (matcher.find()){
                        name = matcher.group(2);
                    }else {System.out.println("boom");}
                    line++;
                    matcher = pattern.matcher(data[line]);
                    int start_time = -1;
                    if (matcher.find()){
                        start_time = Integer.parseInt(matcher.group(2).trim());
                    }else {System.out.println("boom");}
                    line++;
                    matcher = pattern.matcher(data[line]);
                    int phases_count = -1;
                    if (matcher.find()){
                        phases_count = Integer.parseInt(matcher.group(2).trim());
                    }else {System.out.println("boom");}

                    processes[i] = new Process(name, start_time, phases_count, i);

                    pattern = Pattern.compile("CPU=(\\d+)\\s+I/O=(\\d+)\\s+REPEAT=(\\d+)");
                    for (int j=0; j<phases_count; j++){
                        line++;
                        matcher = pattern.matcher(data[line]);
                        if (matcher.find()){
                            int cpu = Integer.parseInt(matcher.group(1));
                            int io = Integer.parseInt(matcher.group(2));
                            int repeat = Integer.parseInt(matcher.group(3));
                            processes[i].phases[j] = new Phase(cpu, io, repeat);
                        }
                        else System.out.println("boom x3");
                    }
                    break;
                }
                line++;
            }
            pattern = Pattern.compile("Process\\s*#\\d+");
        }
    }

    public void addProcesses() {
        for (int i=0; i<processes_count; i++) {
            if (processes[i].start_time == current_time && processes[i].added == false) {
                customQueue[0].Q.add(processes[i]);
                processes[i].added = true;
            }
        }
    }

    public String showElementsFromDeque(Deque<Process> coada) {
        if(coada.isEmpty()) return "-";
        ArrayList<Process> temp = new ArrayList<>(coada);
        StringBuilder sb = new StringBuilder();
        for (Process proces : temp) {
            sb.append(proces.alias);
        }
        return sb.toString().trim();
    }

    //*proiect*
    //IOExemplu model=============================================================================
    public static void main (String args[]){
        new TS();
    }

    @SuppressWarnings("all")
    TS(){
        Dimension res=getToolkit().getScreenSize();
        setBackground(new Color(38, 104, 165));
        setForeground(new Color(255,255,0));    
        setResizable(false);       
        adaugaMenuBar();
        setTitle("Threads Simulation");    
        resize(400,400);
        move((int)((res.width-400)/2),(int)((res.height-400)/2));
        show();
    }

    private void adaugaMenuBar(){
        MenuBar men=new MenuBar();    
        Menu f = new Menu("File");
        f.add("Open");
        f.add("-");
        f.add("Exit");    
        men.add(f);
        setMenuBar(men);    
    }

    @SuppressWarnings("all")
    public boolean handleEvent(Event e){
        if(e.id==Event.WINDOW_DESTROY){
            System.exit(0);
        }else if(e.id==Event.ACTION_EVENT && e.target instanceof MenuItem){
            if("Exit".equals(e.arg)){
                System.exit(0);
            }else if("Open".equals(e.arg)){
                loadFile();
                return true; 
            }             	
        }else return false;	
        return super.handleEvent(e);
    }
        
    @SuppressWarnings("all")
    private void loadFile(){ 
        try{ 	
            FileDialog fd=new FileDialog(this, "Open File", 0);
            if(dir!=null) fd.setDirectory(dir);
            fd.setVisible(true);
            if(fd.getFile() != null) {
                dir = fd.getDirectory();
                String fisier = fd.getFile();
                path = dir + fisier;	                            
                try { classFile=new URL("file:/"+path); }
                catch (MalformedURLException e) {}
                LoadStream(classFile); 
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }

    @SuppressWarnings("all")
    private void LoadStream(URL classFile){	
        try {
            InputStream is = classFile.openStream();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
            data = new String[100000];
            int i = 0;
            while ((data[i] = dis.readLine()) != null) i++;
            String[] data1 = new String[i];
            System.arraycopy(data, 0, data1, 0, i);
            data = data1;

            simulate();

            is.close();		
        } 
        catch (IOException e) {}
    }
    //IOExemplu model=============================================================================
}