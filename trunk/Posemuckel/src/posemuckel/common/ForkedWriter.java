/**
 * 
 */
package posemuckel.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * @author Posemuckel Team
 *
 */
public class ForkedWriter extends Writer {
	
	public static boolean useStream;
	
	private PrintWriter one;
	private static Logger logger = Logger.getLogger(ForkedWriter.class);
	
	
	public ForkedWriter(PrintWriter one) {
		this.one = one;
	}
	
	public static void printToStream(boolean doIt) {
		useStream = doIt;
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if(useStream) logger.debug("write: ");
		one.write(cbuf, off, len);
		if(useStream)logger.debug(cbuf);
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		if(useStream)logger.debug("flush: ");
		one.flush();
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		if(useStream)logger.debug("close: ");
		one.close();
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#print(java.lang.String)
	 */
	public void print(String s) {
		if(useStream)logger.debug("print: ");
		one.print(s);
		if(useStream)logger.debug(s);
		if(useStream)logger.debug("end writing");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#println(java.lang.String)
	 */
	public void println(String x) {
		//System.out.println("println: ");
		one.println(x);
		if(useStream)logger.debug("writing: \n" + x);
		if(useStream)logger.debug("end writing");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintWriter#write(java.lang.String)
	 */
	public void write(String s) {
		if(useStream)logger.debug("write2 ");
		one.write(s);
		//two.write(s);
	}

}
