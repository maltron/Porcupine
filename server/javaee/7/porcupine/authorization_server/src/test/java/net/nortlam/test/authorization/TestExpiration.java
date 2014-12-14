package net.nortlam.test.authorization;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class TestExpiration implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestExpiration.class.getName());

    public TestExpiration() {
    }
    
    @Test
    public void testExpiration() {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss.S");
        
        Calendar c1 = Calendar.getInstance();
        int minutes5 = 1000*60*1; // 5 minutes;
        c1.add(Calendar.MILLISECOND, minutes5);
        
//        for(int i=0; i < 100; i++) {
//            Calendar c2 = Calendar.getInstance();
//            System.out.printf("C1 %s > C2 %s ? %s\n",
//                s.format(c1.getTime()), s.format(c2.getTime()), 
//                c1.getTime().getTime() > c2.getTime().getTime() ? "TRUE" : "FALSE");
//            try{Thread.sleep(1000);}catch(InterruptedException e){}
//        }
        
    }

}
