import java.util.Arrays;
import java.util.Scanner;

public class NonpreemptivePriorityScheduler {
    public static void main(String[] args) {
        System.out.println("*** Priority Scheduling with Arrival Time ***");

        System.out.print("Enter Number of Processes: ");
        Scanner sc = new Scanner(System.in);
        int numberOfProcess = sc.nextInt();

        String process[] = new String[numberOfProcess];
        int burstTime[] = new int[numberOfProcess];
        int priority[] = new int[numberOfProcess];
        int arrivalTime[] = new int[numberOfProcess];

        // Input process names
        int p = 1;
        for (int i = 0; i < numberOfProcess; i++) {
            process[i] = "P" + p;
            p++;
        }        
        
        System.out.println(Arrays.toString(process));

        // Input burst time
        System.out.print("Enter Burst Time for " + numberOfProcess + " processes: \n");
        for (int i = 0; i < numberOfProcess; i++) {
            burstTime[i] = sc.nextInt();
        }

        System.out.println(Arrays.toString(burstTime));

        // Input priority
        System.out.print("Enter Priority for " + numberOfProcess + " processes: \n");
        for (int i = 0; i < numberOfProcess; i++) {
            priority[i] = sc.nextInt();
        }

        System.out.println(Arrays.toString(priority));

        // Input arrival time
        System.out.print("Enter Arrival Time for " + numberOfProcess + " processes: \n");
        for (int i = 0; i < numberOfProcess; i++) {
            arrivalTime[i] = sc.nextInt();
        }

        System.out.println(Arrays.toString(arrivalTime));

        // Sorting processes by priority
        for (int i = 0; i < numberOfProcess - 1; i++) {
            for (int j = 0; j < numberOfProcess - 1; j++) {
                if (priority[j] > priority[j + 1]) {
                    // Swap priority
                    int temp = priority[j];
                    priority[j] = priority[j + 1];
                    priority[j + 1] = temp;

                    // Swap burst time
                    temp = burstTime[j];
                    burstTime[j] = burstTime[j + 1];
                    burstTime[j + 1] = temp;

                    // Swap arrival time
                    temp = arrivalTime[j];
                    arrivalTime[j] = arrivalTime[j + 1];
                    arrivalTime[j + 1] = temp;

                    // Swap process names
                    String temp2 = process[j];
                    process[j] = process[j + 1];
                    process[j + 1] = temp2;
                }
            }
        }

        int TAT[] = new int[numberOfProcess]; // Turnaround Time
        int waitingTime[] = new int[numberOfProcess];
        int completionTime = 0;

        // Calculating Waiting Time and Turnaround Time
        for (int i = 0; i < numberOfProcess; i++) {
            if (completionTime < arrivalTime[i]) {
                completionTime = arrivalTime[i]; // Wait for the process to arrive
            }

            waitingTime[i] = completionTime - arrivalTime[i]; // Waiting Time = Completion Time - Arrival Time
            TAT[i] = waitingTime[i] + burstTime[i];        // Turnaround Time = Waiting Time + Burst Time

            completionTime += burstTime[i]; 
        }

        int totalWT = 0, totalTAT = 0;
        double avgWT, avgTAT;

        // Output results
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%n", "Priority", "Process", "Arrival", "BT", "WT", "TAT");
        for (int i = 0; i < numberOfProcess; i++) {
            System.out.printf(
                "%-10d%-10s%-10d%-10d%-10d%-10d%n",
                priority[i], process[i], arrivalTime[i], burstTime[i], waitingTime[i], TAT[i]
            );
            totalWT += waitingTime[i];
            totalTAT += TAT[i];
        }

        avgWT = totalWT / (double) numberOfProcess;
        avgTAT = totalTAT / (double) numberOfProcess;

        System.out.println("\nAverage Waiting Time: " + avgWT);
        System.out.println("Average Turnaround Time: " + avgTAT);

        sc.close();
    }
}
