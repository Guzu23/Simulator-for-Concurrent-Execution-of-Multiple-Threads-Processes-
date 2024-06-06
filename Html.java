import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Html {
    StringBuilder sb = new StringBuilder();
    String cpuProcess, ioProcess, q1Queue, q2Queue, q3Queue, ioQueue;
    Html(){
        startHTMLelements();
    }

    public void startHTMLelements(){
        sb.append("<HTML>\n");
        sb.append("<HEAD><TITLE>THREADS SIMULATION</TITLE>\n");
        sb.append("\n");
        sb.append("<SCRIPT language=\"javascript\">\n");
        sb.append("    var timerONE\n");
        sb.append("    var timerTWO\n");
        sb.append("    var defStatus = ' '\n");
        sb.append("    function ShowStatus(newstat){\n");
        sb.append("        clearTimeout(timerONE)\n");
        sb.append("        clearTimeout(timerTWO)\n");
        sb.append("        if (newstat != defStatus){\n");
        sb.append("            var cmd1 = 'ShowStatus(\"' + defStatus + '\")'\n");
        sb.append("            timerONE = window.setTimeout(cmd1,6000)\n");
        sb.append("        }\n");
        sb.append("        scrllStatus(newstat,newstat.length)\n");
        sb.append("    }\n");
        sb.append("    function scrllStatus(scrllStat,i){\n");
        sb.append("        if (i >= 0){\n");
        sb.append("            if (i >= 2) i = i - 2\n");
        sb.append("            else  --i\n");
        sb.append("            window.status = scrllStat.substring(i,scrllStat.length)\n");
        sb.append("            cmd2='scrllStatus(\"'+ scrllStat +'\",'+ i +')'\n");
        sb.append("            timerTWO = window.setTimeout(cmd2,10)\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("</SCRIPT>\n");
        sb.append("\n");
        sb.append("</HEAD>\n");
        sb.append("<BODY BACKGROUND=\"bkg.jpg\" BGPROPERTIES=\"fixed\">\n");
        sb.append("<A NAME=\"top\"></A><BR><BR><CENTER>\n");
        sb.append("<H1><B>THREADS SIMULATION</B></H1>\n");
        sb.append("<BR><BR>\n");
        sb.append("<TABLE WIDTH=30%>\n");
        sb.append("    <TR>\n");
        sb.append("        <TD><A HREF=\"#sid\" onMouseOver=\"ShowStatus('Simulation Input Data')\">Simulation Input Data</A></TD>\n");
        sb.append("    </TR>\n");
        sb.append("    <TR>\n");
        sb.append("        <TD VALIGN=top><A HREF=\"#pd\" onMouseOver=\"ShowStatus('Processes Data')\">Processes Data</A></TD>\n");
        sb.append("        <TD>\n");
        for (int i=1; i<=TS.processes_count; i++){
            sb.append("<A HREF=\"#p" + i + "\" onMouseOver=\"ShowStatus('Process #" + i + "')\">Process #" + i + "</A><BR>\n");
        }
        sb.append("        </TD>\n");
        sb.append("    </TR>\n");
        sb.append("    <TR>\n");
        sb.append("        <TD><A HREF=\"#sod\" onMouseOver=\"ShowStatus('Simulation Output Data')\">Simulation Output Data</A></TD>\n");
        sb.append("    </TR>\n");
        sb.append("</TABLE>\n");
        sb.append("<BR><BR><BR><BR><BR>\n");
        sb.append("<TABLE WIDTH=100% BORDER=1>\n");
        sb.append("    <CAPTION ALIGN=left><A NAME=\"sid\"></A><B>SIMULATION INPUT DATA</B></CAPTION>\n");
        sb.append("    <THEAD ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TH>MAX PRIORITY</TH>\n");
        sb.append("            <TH>NORMAL PRIORITY</TH>\n");
        sb.append("            <TH>MIN PRIORITY</TH>\n");
        sb.append("            <TH>PENALTY LIMIT</TH>\n");
        sb.append("            <TH>AWARD LIMIT</TH>\n");
        sb.append("        </TR>\n");
        sb.append("    </THEAD>\n");
        sb.append("    <TBODY ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TD>q1 = " + TS.customQueue[0].q + "</TD>\n");
        sb.append("            <TD>q2 = " + TS.customQueue[1].q + "</TD>\n");
        sb.append("            <TD>q3 = " + TS.customQueue[2].q + "</TD>\n");
        sb.append("            <TD>k = " + TS.PENALTY_LIMIT + "</TD>\n");
        sb.append("            <TD>r = " + TS.REWARD_LIMIT + "</TD>\n");
        sb.append("        </TR>\n");
        sb.append("    </THEAD>\n");
        sb.append("</TABLE>\n");
        sb.append("</CENTER><A HREF=#top onMouseOver=\"ShowStatus('top')\">top</A><CENTER>\n");
        sb.append("<BR><BR><BR><BR><BR>\n");
        sb.append("<A NAME=\"pd\"></A><P ALIGN=left><B>PROCESSES DATA</B></P>\n");
        sb.append("<P ALIGN=left>Processes_Count = " + TS.processes_count + "</P>\n");
        for (int i=0; i<TS.processes_count; i++) bindProcessDetails(i);
        sb.append("<BR><BR><BR><BR><BR>\n");
        sb.append("<TABLE WIDTH=100% BORDER=1>\n");
        sb.append("    <CAPTION ALIGN=left><A NAME=\"sod\"></A><B>SIMULATION OUTPUT DATA</B></CAPTION>\n");
        sb.append("    <THEAD ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TH>TIME</TH>\n");
        sb.append("            <TH>CPU</TH>\n");
        sb.append("            <TH>I/O</TH>\n");
        sb.append("            <TH>Q1 Queue</TH>\n");
        sb.append("            <TH>Q2 Queue</TH>\n");
        sb.append("            <TH>Q3 Queue</TH>\n");
        sb.append("            <TH>I/O Queue</TH>\n");
        sb.append("        </TR>\n");
        sb.append("    </THEAD>\n");
    }

    public void bindProcessDetails(int i){
        sb.append("<TABLE WIDTH=100% BORDER=1>\n");
        sb.append("    <CAPTION ALIGN=left><A NAME=\"p" + (i+1) + "\"></A><B>PROCESS #" + (i+1) + "</B></CAPTION>\n");
        sb.append("    <THEAD ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TH>NAME</TH>\n");
        sb.append("            <TH>ALIAS</TH>\n");
        sb.append("            <TH>START TIME</TH>\n");
        sb.append("            <TH>PHASES COUNT</TH>\n");
        sb.append("        </TR>\n");
        sb.append("    </THEAD>\n");
        sb.append("    <TBODY ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TD>" + TS.processes[i].name +"</TD>\n");
        sb.append("            <TD>" + TS.processes[i].alias +"</TD>\n");
        sb.append("            <TD>" + TS.processes[i].start_time +"</TD>\n");
        sb.append("            <TD>" + TS.processes[i].phases_count +"</TD>\n");
        sb.append("        </TR>\n");
        sb.append("    </TBODY>\n");
        sb.append("    <THEAD ALIGN=center>\n");
        sb.append("        <TR>\n");
        sb.append("            <TH>PHASE COUNT</TH>\n");
        sb.append("            <TH>CPU TIMES COUNT</TH>\n");
        sb.append("            <TH>I/O TIMES COUNT</TH>\n");
        sb.append("            <TH>REPEAT COUNT</TH>\n");
        sb.append("        </TR>\n");
        sb.append("    </THEAD>\n");
        sb.append("    <TBODY ALIGN=center>\n");
        for (int j=0; j<TS.processes[i].phases_count; j++) bindPhase(i, j);
        sb.append("    </TBODY>\n");
        sb.append("</TABLE>\n");
        sb.append("</CENTER><A HREF=#top onMouseOver=\"ShowStatus('top')\">top</A><CENTER>\n");
        sb.append("<BR>\n");
    }

    public void bindPhase(int i, int j){
        sb.append("        <TR>\n");
        sb.append("            <TD>" + (j + 1) +"</TD>\n");
        sb.append("            <TD>" + (TS.processes[i].phases[j].CPU_TIMES_COUNT + 1) +"</TD>\n");
        sb.append("            <TD>" + (TS.processes[i].phases[j].IO_TIMES_COUNT + 1) +"</TD>\n");
        sb.append("            <TD>" + (TS.processes[i].phases[j].REPEAT_COUNT + 1) +"</TD>\n");
        sb.append("        </TR>\n");
    }

    public void resetStrings(){
        cpuProcess = "            <TD align=\"center\">CPU: -</TD>\n";
        ioProcess = "            <TD align=\"center\">I/O: -</TD>\n";
        q1Queue = "            <TD align=\"center\">-</TD>\n";
        q2Queue = "            <TD align=\"center\">-</TD>\n";
        q3Queue = "            <TD align=\"center\">-</TD>\n";
        ioQueue = "            <TD align=\"center\">-</TD>\n";
    }

    public void addLineHTML(){
        sb.append("        <TR>\n");
        sb.append("            <TD align=\"center\"><FONT COLOR=blue>" + String.format("%06d", TS.current_time) + "</FONT></TD>\n");
        sb.append(cpuProcess);
        sb.append(ioProcess);
        sb.append(q1Queue);
        sb.append(q2Queue);
        sb.append(q3Queue);
        sb.append(ioQueue);
        sb.append("        </TR>\n");
    }

    public void finishHTMLelements(){
        sb.append("        <TR>");
        sb.append("            <TD align=\"center\" COLSPAN=7 BGCOLOR=red>Simulation is finished.</TD>");
        sb.append("        </TR>");
        sb.append("    </TBODY>\n");
        sb.append("</TABLE>\n");
        sb.append("</CENTER><A HREF=#top onMouseOver=\"ShowStatus('top')\">top</A><CENTER>\n");
        sb.append("</BODY>\n");
        sb.append("</HTML>\n");
        writeHTML();
    }

    public void writeHTML(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.htm"))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.out.println("A aparut o eroare la generarea fisierului HTML: " + e.getMessage());
        }
    }
}
