import java.util.ArrayList;

public class Cache {
	int bytesSize;
	int noOfInstructions;
	int BlockSize;
	int Associativity;
	int miss;
	int hit;
	Object[] dData;
	Object[] iData;
	boolean[] iValidBit;
	boolean[] dValidBit;
	int[] iTag;
	int[] dTag;

	public Cache(int size, int BlockSize, int Associativity) {
		this.bytesSize = size; //size is given in bytes
		this.noOfInstructions = this.bytesSize/2;//since each instruction is 2 bytes
		this.BlockSize = BlockSize;//block size is in bytes
		this.Associativity = Associativity;// 1->direct mapped, >1 set associative, == size/2 means full associative
		dData = new Object[noOfInstructions];
		iData = new Object[noOfInstructions];
		dValidBit = new boolean[noOfInstructions];
		iValidBit = new boolean[noOfInstructions];
		dTag = new int[noOfInstructions];
		iTag = new int[noOfInstructions];
	}

	public Object searchData(int index, int tag, int offSet, int cType) {//cType = 0 when I'm working on Icache and = 1 when I'm working on DCache
		return null;
	}

	public void cacheData(int index, int[] tag, int[] offset, Object[] data,
			int cType) {

	}
}
