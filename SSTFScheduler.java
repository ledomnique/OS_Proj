import java.util.ArrayList;
import java.util.Scanner;

public class SSTFScheduler {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of disk requests: ");
        int numRequests = getValidatedInput(scanner, 2, 9);

        ArrayList<Integer> requests = new ArrayList<>();
        System.out.println("Enter the disk requests (e.g., 10, 20, 30):");
        for (int i = 1; i <= numRequests; i++) {
            System.out.print("Request " + i + ": ");
            requests.add(getValidatedInput(scanner, 0, 199)); 
        }

        System.out.print("Enter the initial head position (0-199): ");
        int headPosition = getValidatedInput(scanner, 0, 199);

        performSSTF(requests, headPosition);

        scanner.close();
    }

    private static int getValidatedInput(Scanner scanner, int min, int max) {
        int value;
        while (true) {
            try {
                value = scanner.nextInt();
                if (value < min || value > max) {
                    throw new IllegalArgumentException();
                }
                return value;
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
                scanner.nextLine(); 
            }
        }
    }

    private static void performSSTF(ArrayList<Integer> requests, int headPosition) {
        System.out.println("\nStarting SSTF Disk Scheduling...");

        ArrayList<Integer> sequence = new ArrayList<>();
        int totalMovement = 0;

        ArrayList<Integer> remainingRequests = new ArrayList<>(requests);

        while (!remainingRequests.isEmpty()) {

            int closestRequest = remainingRequests.get(0);
            int minDistance = Math.abs(closestRequest - headPosition);

            for (int request : remainingRequests) {
                int distance = Math.abs(request - headPosition);
                if (distance < minDistance) {
                    closestRequest = request;
                    minDistance = distance;
                }
            }

            sequence.add(closestRequest);
            totalMovement += minDistance;
            headPosition = closestRequest;

            remainingRequests.remove(Integer.valueOf(closestRequest));
        }

        System.out.println("Execution Sequence: " + sequence);
        System.out.println("Total Head Movement: " + totalMovement);
        System.out.println("SSTF Scheduling Complete.\n");
    }
}