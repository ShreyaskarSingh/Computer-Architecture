import java.util.*;

public class Pointer {
	String instr, is, updt1, updt2;
	int decision, dest_reg, s1, s2, l, t1, t2, update;
	String[] str;

	public Pointer(String is5) {

		str = is5.split(" ");

		this.instr = str[0];
		this.decision = Integer.parseInt(str[1]);
		this.dest_reg = Integer.parseInt(str[2]);
		this.s1 = Integer.parseInt(str[3]);
		this.s2 = Integer.parseInt(str[4]);

		this.t1 = -1;
		this.t2 = -1;

	}

	ArrayList<Integer> it_n = new ArrayList<Integer>();

	public void iteration(int counter) {
		this.it_n.add(counter);
	}

	public void rat() {

		switch (this.decision) {
		case 0:
			this.l = 1;
			this.update = this.it_n.get(3) + l;
			break;
		case 1:
			this.l = 2;
			this.update = this.it_n.get(3) + l;
			break;
		case 2:
			this.l = 5;
			this.update = this.it_n.get(3) + l;
			break;
		}
	}

}
