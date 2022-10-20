import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class hybrid {
//  intializing the instance variable
	int m1, n_mispred, total_pred, counter_value, max_count, branch_history_register, n,k,m2;
	double mispred_rate;
	String trace;
	int[] chooser_table, gshare_table, bimodal_table;

	public hybrid(int k, int m1,int n, int m2, String trace_file) {
		this.trace = trace_file;
		this.m1 = m1;
		this.m2 = m2;
		this.n_mispred = 0;
		this.total_pred = 0;
		this.counter_value = 1;
		this.max_count = 3;
		this.branch_history_register = 0;
		this.n = n;
		gshare_table = new int[(int) Math.pow(2, m1)];
		bimodal_table = new int[(int) Math.pow(2, m2)];
		Arrays.fill(gshare_table, 4);
		Arrays.fill(bimodal_table, 4);
		this.chooser_table = new int[(int) Math.pow(2, k)];
		Arrays.fill(this.chooser_table, this.counter_value);
		this.k = k;
		input_read();
		print();
	}
//	to check the gshare prediction
	public String gshare_prediction(int address, String actual_prediction) {
		int ghare_index;
		String local_prediction = "n";
		
		address >>= 2;
		ghare_index = address & (int)(Math.pow(2, m1)-1);
		ghare_index = this.branch_history_register ^ ghare_index;
		if (gshare_table[ghare_index]>=4)
			local_prediction = "t";
		else
			local_prediction = "n";
		
			return local_prediction;
	}
// to update the register
	public void gshare_register_update(int address, String actual_prediction) {
		int ghare_index;
		
		address >>= 2;
		ghare_index = address & (int)(Math.pow(2, m1)-1);
		ghare_index = this.branch_history_register ^ ghare_index;


		if (actual_prediction.equals("t")) {
			gshare_table[ghare_index] = Math.min(gshare_table[ghare_index] + 1, 7);
		} else {
			gshare_table[ghare_index] = Math.max(gshare_table[ghare_index]-1, 0);
		}
	}
//	to check the bimodal prediction
	public String bimodal_prediction(int address, String actual_prediction) {
		int index;
		String local_prediction = "n";
	
		address >>= 2;
		index = address & (int)(Math.pow(2, this.m2)-1);
		if (bimodal_table[index]>=4)
			local_prediction = "t";
		else
			local_prediction = "n";


				return local_prediction;
	}
//	to update the bimodal register
	public void bimodal_register_update(int address, String actual_prediction) {
		int index;
		address >>= 2;
		index = address & (int)(Math.pow(2, this.m2)-1);
		if (actual_prediction.equals("t")) {
			bimodal_table[index] = Math.min(bimodal_table[index] + 1, 7);
		} else {
			bimodal_table[index] = Math.max(bimodal_table[index]-1, 0);
		}

	}
//	updating the branch history register 
	public void global_branch_history_register_update(String actual_prediction) {
		if (this.n > 0) {
		this.branch_history_register >>= 1;
		if (actual_prediction.equals("t")) {
			this.branch_history_register |= (int)Math.pow(2,(this.n-1));
		}
		} 
	}
//	to calculate the actual prediction according to hybrid
	public void prediction(int address, String actual_prediction) {
		int index;
		int flag_gshare = 0;
		int flag_bimodalshare = 0;
		String local_prediction = "n";
		String g_share_prediction, bimodal_prediction;
		g_share_prediction = gshare_prediction(address,actual_prediction);
		bimodal_prediction = bimodal_prediction(address,actual_prediction);
		this.total_pred++;
		int original_address = address;
		original_address >>= 2;
		index = original_address & (int)(Math.pow(2, this.k)-1);
		
		if (this.chooser_table[index]>=2) {
			local_prediction = g_share_prediction;
			flag_gshare = 1;}
		else {
			local_prediction = bimodal_prediction ;
			flag_bimodalshare = 1;}

		if (!actual_prediction.equals(local_prediction)) {
			this.n_mispred += 1;
		}
		if (flag_bimodalshare == 1) {
			bimodal_register_update(address, actual_prediction);
		}
		else if (flag_gshare == 1) {
			gshare_register_update(address, actual_prediction);
		}
		global_branch_history_register_update(actual_prediction);
		
		if (g_share_prediction.equals(actual_prediction) && !bimodal_prediction.equals(actual_prediction)) {
			this.chooser_table[index] = Math.min(this.chooser_table[index] + 1, this.max_count);
		} else if (!g_share_prediction.equals(actual_prediction) && bimodal_prediction.equals(actual_prediction)){
			this.chooser_table[index] = Math.max(this.chooser_table[index]-1, 0);
		}
		
			
	}
	
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
	public void print() {
		System.out.println("OUTPUT");
		System.out.println("Number of predictions:     " + this.total_pred);
		System.out.println("Number of Misprediction:   " + this.n_mispred);
		mispred_rate = ((double)(this.n_mispred)/(double)(this.total_pred))*100;
		System.out.println("Misprediction rate:        " + String.format("%.2f", mispred_rate) + "%");
		System.out.println("FINAL CHOOSER CONTENT:     " );
		for (int i = 0; i < this.chooser_table.length; i++)
	         System.out.println( i +  "        "+ this.chooser_table[i]);
		System.out.println("FINAL GSHARE CONTENT:     " );
		for (int i = 0; i < gshare_table.length; i++)
	         System.out.println( i +  "        "+ this.gshare_table[i]);
		System.out.println("FINAL BIMODAL CONTENT:     " );
		for (int i = 0; i < bimodal_table.length; i++)
	         System.out.println( i +  "        "+ bimodal_table[i]);
	}
}
