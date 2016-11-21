
public class Cache {
	int bytesSize;
	int noOfInstructions;
	int BlockSize;
	int Associativity;
	int miss = 0;
	int hit = 0;
	Object[] dData;
	Object[] iData;
	boolean[] iValidBit;
	boolean[] dValidBit;
	int[] iTag;
	int[] dTag;
	int wpHit; // in case of of 0 -> "write through", in case of 1 ->
				// "write back"
	int wpMiss;
	int accessTime;

	public Cache(int size, int BlockSize, int Associativity, int wpHit,
			int wpMiss, int accessTime) {
		this.noOfInstructions = size / 2;// since each instruction is
													// 2 bytes
		this.BlockSize = BlockSize;// block size is in bytes
		this.Associativity = Associativity;// 1->direct mapped, >1 set
											// associative, == size/2 means full
											// associative
		dData = new Object[noOfInstructions];
		iData = new Object[noOfInstructions];
		dValidBit = new boolean[noOfInstructions];
		iValidBit = new boolean[noOfInstructions];
		dTag = new int[noOfInstructions];
		iTag = new int[noOfInstructions];
		this.wpHit = wpHit;
		this.wpMiss = wpMiss;
		this.accessTime = accessTime;
	}

	public Object searchData(int address, int cType) { /* cType = 0 -> DCache, cType = 1 -> ICache*/
		
		return null;
	}

	public void cacheData(int address, int level,
			int cType) {
	}
}
