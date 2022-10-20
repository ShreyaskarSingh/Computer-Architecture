import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class gshare {
//  intializing the instance variable
	int pc_bits, n_mispred, total_pred, counter_value, max_count, global_branch_history_register, global_history_bits;
	double mispred_rate;
	String trace;
	int[] table;

	public gshare(int pc_bits,int global_history_bits, String trace_file) {
		this.trace = trace_file;
		this.pc_bits = pc_bits;
		this.n_mispred = 0;
		this.total_pred = 0;
		this.counter_value = 4;
		this.max_count = 7;
		this.global_branch_history_register = 0;
		this.global_history_bits = global_history_bits;
		this.table = new int[(int) Math.pow(2, pc_bits)];
		Arrays.fill(this.table, this.counter_value);
		input_read();
		print();
	}
	public void prediction(int address, String actual_prediction) {
		int index;
		String local_prediction = "n";
		this.total_pred++;
		address >>= 2;
		index = address & (int)(Math.pow(2, pc_bits)-1);
		index = this.global_branch_history_register ^ index;
		if (this.table[index]>=4)
			local_prediction = "t";
		else
			local_prediction = "n";

		if (!actual_prediction.equals(local_prediction)) {
			this.n_mispred += 1;
		}
//      update the  table according to threshold
		if (actual_prediction.equals("t")) {
			this.table[index] = Math.min(this.table[index] + 1, this.max_count);
		} else {
			this.table[index] = Math.max(this.table[index]-1, 0);
		}
		if (this.global_history_bits > 0) {
			this.global_branch_history_register >>= 1;
			if (actual_prediction.equals("t")) {
				this.global_branch_history_register |= (int)Math.pow(2,(this.global_history_bits-1));
			
			}
			
		} 
			
	}
	
	// to take the input  line by line
	public void input_read() {

		try {
			File file = new File( trace);

			BufferedReader br = new BufferedReader(new FileReader(file));

			String next_line;
			int address;

			while ((next_line = br.readLine()) != null) {
				String hex_address = next_line.split(" ")[0];
				hex_address = hex_address.trim();
				address = Integer.parseInt(hex_address,16);
				String actual_state = next_line.split(" ")[1];
				prediction(address,actual_state);

			}

		} catch (Exception ignored) {
			System.out.println(ignored);
		}

	}
	// to print the result 
	public void print() {
		System.out.println("OUTPUT");
		System.out.println("Number of predictions:     " + this.total_pred);
		System.out.println("Number of Misprediction:   " + this.n_mispred);
		mispred_rate = ((double)(this.n_mispred)/(double)(this.total_pred))*100;
		System.out.println("Misprediction rate:        " + String.format("%.2f", mispred_rate) + "%");
		System.out.println("FINAL BIOMED CONTENT:     " );
		for (int i = 0; i < this.table.length; i++)
	         System.out.println( i +  "        "+ this.table[i]);
	}
}

