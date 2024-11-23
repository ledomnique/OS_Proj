import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class RoundRobinScheduler {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();

        int[] arrivalTime = new int[n];
        int[] burstTime = new int[n];
        int[] remainingTime = new int[n];
        int[] completionTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        System.out.println("Enter arrival times:");
        for (int i = 0; i < n; i++) {
            arrivalTime[i] = sc.nextInt();
        }

        System.out.println("Enter burst times:");
        for (int i = 0; i < n; i++) {
            burstTime[i] = sc.nextInt();
            remainingTime[i] = burstTime[i];  // Initialize remaining time
        }

        System.out.print("Enter time quantum: ");
        int timeQuantum = sc.nextInt();

        // Round Robin Scheduling Algorithm
        int currentTime = 0;
        Queue<Integer> queue = new LinkedList<>();
        boolean[] isInQueue = new boolean[n];
        int completed = 0;

        // Enqueue processes that arrive at time 0
        for (int i = 0; i < n; i++) {
            if (arrivalTime[i] <= currentTime) {
                queue.add(i);
                isInQueue[i] = true;
            }
        }

        while (completed < n) {
            if (queue.isEmpty()) {
                currentTime++;  // If the queue is empty, increment the time
                for (int i = 0; i < n; i++) {
                    if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                        queue.add(i);
                        isInQueue[i] = true;
                    }
                }
                continue;
            }

            int process = queue.poll();  // Dequeue the process

            if (remainingTime[process] > timeQuantum) {
                currentTime += timeQuantum;
                remainingTime[process] -= timeQuantum;
            } else {
                currentTime += remainingTime[process];
                remainingTime[process] = 0;
                completionTime[process] = currentTime;
                completed++;
            }

            // Check if new processes have arrived
            for (int i = 0; i < n; i++) {
                if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                    queue.add(i);
                    isInQueue[i] = true;
                }
            }

            // Re-add the process to the queue if it is not finished
            if (remainingTime[process] > 0) {
                queue.add(process);
            }
        }

        // Calculate waiting and turnaround times
        double totalWaitingTime = 0, totalTurnaroundTime = 0;
        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = completionTime[i] - arrivalTime[i];
            waitingTime[i] = turnaroundTime[i] - burstTime[i];
            totalWaitingTime += waitingTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        // Print results
        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        for (int i = 0; i < n; i++) {
            System.out.println((i+1) + "\t" + arrivalTime[i] + "\t" + burstTime[i] + "\t" + completionTime[i]
                    + "\t\t" + turnaroundTime[i] + "\t\t" + waitingTime[i]);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", (totalWaitingTime / n));
        System.out.printf("Average Turnaround Time: %.2f\n", (totalTurnaroundTime / n));

        sc.close();
    }
}
