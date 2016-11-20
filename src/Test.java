import java.util.ArrayList;


public class Test {
	
	public static void hi(Instruction i){
		
		i.commit=true;
	}
	public static void main(String[] args) {
		Instruction x = new Instruction("sdasd asdasdasd");
		//ArrayList <Instruction> y = new ArrayList<Instruction>();
		//y.add(x);
		hi(x);
		//x.commit = true;
		System.out.println(x.commit);
	}
}
