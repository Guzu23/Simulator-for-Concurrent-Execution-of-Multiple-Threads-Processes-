This project is a simulator for the concurrent execution of multiple threads (processes). The simulator is capable of handling up to 26 processes simultaneously, each with unique characteristics and execution behaviors. The main goal of this project is to simulate the scheduling and execution of processes, incorporating CPU and IO operations, within a defined priority-based queue system.

Features:



Process Characteristics:
  -Each process has a unique alias (A-Z).
  
  -Start time (TIME_START) defines when the process begins execution.
  
  -Processes consist of multiple phases, each with specific CPU and IO time requirements.
  

Phases and Execution:

  Each phase is defined by:
  
    -Phase number (PHASE_COUNT)
    
    -CPU time units (CPU_TIMES_COUNT)
    
    -IO time units (IO_TIMES_COUNT)
    
    -Number of repetitions (REPEAT_COUNT)
    
  Processes execute CPU and IO operations in cycles, as defined by their phases.
  

Scheduling and Priority:

  Three priority queues (Q1, Q2, Q3) manage process scheduling.
  
  Processes are prioritized and scheduled based on their queue position and defined criteria.
  
  Penalty and award systems adjust process priorities dynamically.
  

IO Queue:

  A single queue manages IO operations for all processes.
  
  Processes are placed in the IO queue based on entry order and execute IO operations sequentially.
  

Simulation Details:

  Time Units: The simulation operates in abstract time units, independent of the system clock.
  
  Concurrent Execution: CPU and IO operations are handled concurrently by the simulator.
  
  Dynamic Scheduling: Processes move between priority queues based on execution outcomes, penalties, and awards.
  
