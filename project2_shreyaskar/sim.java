import java.io.*;
import java.util.*;

public class sim {
    static String trace, type;
    static int  n_bits, pc_bits, global_history_bits, k,m1,n,m2;
    
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
//		Checking the parameters to see which class to call
		if (args.length == 3) {
            type = args[0];
            if (type.equals("smith")) {
            	n_bits = Integer.parseInt(args[1]);
            	trace = args[2];
            	new smith(n_bits,trace);
            }
            else if (type.equals("bimodal")) {
            	pc_bits = Integer.parseInt(args[1]);
            	trace = args[2];
            	new bimodal(pc_bits,trace);
            }
            
        }
		else if (args.length == 4) {
			type = args[0];
			 if (type.equals("gshare")) {
	            	pc_bits = Integer.parseInt(args[1]);
	            	global_history_bits = Integer.parseInt(args[2]);
	            	trace = args[3];
	            	new gshare(pc_bits,global_history_bits,trace);
	            }
			
		}
		else if (args.length == 6) {
			
			type = args[0];
			if (type.equals("hybrid")) {
				
				k = Integer.parseInt(args[1]);
            	m1 = Integer.parseInt(args[2]);
            	n = Integer.parseInt(args[3]);
            	m2 = Integer.parseInt(args[4]);
            	trace = args[5];
            	new hybrid(k, m1,n,m2, trace);
            }
		
			
		}
		
	}
	
    
}


