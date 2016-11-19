import java.util.ArrayList;
import java.util.Scanner;

public class Processor {
	int cacheLevel;
	int blockSize;
	static ArrayList<Cache> caches = new ArrayList<Cache>();
	static MainMemory mainMemory;
	int[] functionalUnits = new int[5];// 0->add, 1->addI, 2->multiply,
										// 3->load,4->store , are these the only func units ?
	int[] cyclesPerInst = new int[5];
	Object[][] scoreBoard;// same columns as in lecture 11
	int[] registersStatusTable = new int[7];// index 0->R1 and so on...
	//What is the cost of branch miss prediction ?
	int pipelineWidth;
	Object ROB[][];
	int [] instructionBuffer;
	int noOfInstrutions = 0;

	public static void main(String[] args) {
		Processor p = new Processor();
	}

	public Processor() {

		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the number of cache levels prefered");
		this.cacheLevel = sc.nextInt();
		System.out.println("Please enter the Block size in bytes");
		blockSize = sc.nextInt();
		System.out
				.println("Please enter the access time of the main memory in cycles");
		int memAccessTime = sc.nextInt();
		for (int i = 0; i < cacheLevel; i++) {
			System.out.println("Please enter the size of cache level "
					+ (i + 1) + " in bytes");
			int size = sc.nextInt();
			System.out
					.println("Please enter the authorization of cache level"
							+ (i + 1)
							+ ".\nThe options are : 1- For (entry equals 1) Direct mapped will be chosen.\n2- For (1 < entry < "
							+ (size / 2)
							+ ") Set associativity will be chosen.\n3- For entry equals ("
							+ size / 2 + ") Full associativity will be chosen.");
			int auhto = sc.nextInt();
			System.out
					.println("Please enter the writing policy if its a 'write back' or 'write through' in case of HIT");
			String Hit = sc.nextLine();
			System.out
					.println("Please enter the writing policy if its a 'write back' or 'write through' in case of MISS");
			String Miss = sc.nextLine();
			int wpHit = 0;
			int wpMiss = 0;
			if (Hit.equals("write back"))
				wpHit = 1;
			if (Miss.equals("write back"))
				wpMiss = 1;
			System.out.println("Please enter the access time in cycles");
			int accessTime = sc.nextInt();
			caches.add(new Cache(size, blockSize, auhto, wpHit, wpMiss,
					accessTime));
		}
		System.out.println("Please enter how many add funtional units");
		functionalUnits[0] = sc.nextInt();
		System.out.println("Please enter the cost of the add");
		cyclesPerInst[0] = sc.nextInt();
		System.out
				.println("Please enter how many add immediate funtional units");
		functionalUnits[1] = sc.nextInt();
		System.out.println("Please enter the cost of the addi");
		cyclesPerInst[1] = sc.nextInt();
		System.out.println("Please enter how many multiply funtional units");
		functionalUnits[2] = sc.nextInt();
		System.out.println("Please enter the cost of the multiply");
		cyclesPerInst[2] = sc.nextInt();
		System.out.println("Please enter how many load funtional units");
		functionalUnits[3] = sc.nextInt();
		System.out.println("Please enter the cost of the load");
		cyclesPerInst[3] = sc.nextInt();
		System.out.println("Please enter how many store funtional units");
		functionalUnits[4] = sc.nextInt();
		System.out.println("Please enter the cost of the store");
		cyclesPerInst[4] = sc.nextInt();
		int rows = 0;
		for (int i = 0; i < functionalUnits.length; i++) {
			rows += functionalUnits[i];
		}
		scoreBoard = new Object[rows][9];
		System.out.println("Please enter the pipeline width");
		pipelineWidth = sc.nextInt();
		System.out.println("Please enter the number of ROB entries");
		ROB = new Object[sc.nextInt()][4];
		System.out.println("Please enter the size of the instruction buffer");
		instructionBuffer = new int[sc.nextInt()];
		String instructions = ""; // this variable will hold the instructions
									// inserted by the user
		System.out
				.println("Please enter you program in the same form as described in the project except that to eliminate all spaces except after the operand, ex.(inst regA,regB,regC). When you are done please enter the letter 'q'");
		while (true) {
			String check = sc.nextLine();
			noOfInstrutions ++;
			if (check.equals("q"))
				break;
			instructions += check + "\n";
		}
		System.out
				.println("Please enter the address where you want to place the program in the memory. P.S. Your range is from word 8174 to word 32768");
		int pointer = sc.nextInt();
		mainMemory = new MainMemory(blockSize, pointer, memAccessTime);
		String[] instructionsList = instructions.split("\n");
		for (int i = 0; i < instructionsList.length; i++) {
			mainMemory.data[pointer] = instructionsList[i];
			pointer++;
		}
		System.out
				.println("If you would like to insert any data initially, please insert the address followed by the value in the form of (address,value), then press enter for a new entry; and when you are finished please enter the character 'q'. If you dont want to insert data enter the character 'q'. P.S. Your range is from word 8 to word 8173");
		String checkData = sc.nextLine();
		while (!checkData.equals("q")) { // insertion of initial data is done
											// here !!!!!
			String[] addKey = checkData.split(",");
			mainMemory.data[Integer.parseInt(addKey[0])] = addKey[1];
			checkData = sc.nextLine();
		}
	}

	public void issue(Instruction instruction) {

	}

	public void execute(Instruction instruction) {
		
	}

	public void write(Instruction instruction) {

	}

	public void commit(Instruction instruction) {

	}
}
