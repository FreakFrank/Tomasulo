import java.util.ArrayList;
import java.util.Scanner;

public class Processor {
	int cacheLevel;
	int blockSize;
	ArrayList<Cache> caches = new ArrayList<Cache>();
	MainMemory mainMemory;

	public static void main(String[] args) {
		Processor p = new Processor();
		p.create();
		p.instructionsFetch();
	}

	public void create() {

		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the number of cache levels prefered");
		this.cacheLevel = sc.nextInt();
		System.out.println("Please enter the Block size in bytes");
		blockSize = sc.nextInt() * 8;
		for (int i = 0; i < cacheLevel; i++) {
			System.out.println("Please enter the size of cache level " + (i + 1)
					+ " in bytes");
			int size = sc.nextInt();
			System.out.println("Please enter the authorization of cache level"
					+ (i + 1) +".\nThe options are : 1- For (entry equals 1) Direct mapped will be chosen\n2- For (1 < entry < "+(size/2)+") Set associativity will be chosen.\n3- For entry equals ("+size/2+") Full associativity will be chosen.");
			int auhto = sc.nextInt();
			caches.add(new Cache(size, blockSize, auhto));
		}

	}
	public void instructionsFetch(){
		
	}
}
