
public class Check_size {

	int S, sc;

	public Check_size(int S, int sc) {

		this.S = S;
		this.sc = sc;
	}

	public int main(Fetch instr) {
		int size = 0;

		int actual_size = instr.in_order.size();
		int rem_spc = this.S - this.sc;

		if (actual_size >= rem_spc) {
			size = rem_spc;
		} else if (actual_size < rem_spc) {
			size = actual_size;
		}

		return size;

	}
}
