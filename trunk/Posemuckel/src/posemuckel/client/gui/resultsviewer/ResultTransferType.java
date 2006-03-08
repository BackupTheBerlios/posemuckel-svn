package posemuckel.client.gui.resultsviewer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class ResultTransferType  extends ByteArrayTransfer {
	
	public static final String FOLDER_TYPE = "folder_type";
	public static final String URL_TYPE = "url_type";
	
	private static final int  FOLDER_TYPE_ID = registerType ( FOLDER_TYPE);
	private static final int  URL_TYPE_ID = registerType(URL_TYPE);
	
	private String type;
	private int type_id;

	@Override
	protected int[] getTypeIds() {
		return new int[] {type_id};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {type};
	}
	
	protected static boolean supports(TransferData transfer) {
		if(transfer.type == FOLDER_TYPE_ID || transfer.type == URL_TYPE_ID)
			return true;
		return false;
	}
	
	protected static boolean supportsFolder(TransferData transfer) {
		if(transfer.type == FOLDER_TYPE_ID)
			return true;
		return false;
	}
	
	protected static boolean supportsURL(TransferData transfer) {
		if(transfer.type == URL_TYPE_ID)
			return true;
		return false;
	}
	
	public ResultTransferType(String type) {
		if(type.equals(FOLDER_TYPE)) {
			this.type = type; 
			type_id = FOLDER_TYPE_ID;
		} else if(type.equals(URL_TYPE)) {
			this.type = type; 
			type_id = URL_TYPE_ID;
		}
	}
	
	public void javaToNative (Object object, TransferData transferData) {
		if (!isSupportedType (transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		String[] data = (String[]) object;
		try {
			// write data to a byte array and then ask super to convert to pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream ();
			DataOutputStream writeOut = new DataOutputStream (out);
			for (int i = 0, length = data.length; i < length; i++) {
				byte [] buffer = data[i].getBytes ();
				writeOut.writeInt (buffer.length);
				writeOut.write (buffer);
			}
			byte [] buffer = out.toByteArray ();
			writeOut.close ();
			super.javaToNative (buffer, transferData);
		}
		catch (IOException e) {}
	}

	public Object nativeToJava (TransferData transferData) {
		if (isSupportedType (transferData)) {
			byte [] buffer = (byte []) super.nativeToJava (transferData);
			if (buffer == null) return null;
			//es ist nicht bekannt, wie viele Elemente das Array enthält
			Vector<String> myData = new Vector<String>();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream (buffer);
				DataInputStream readIn = new DataInputStream (in);
				while (readIn.available () > 0) {
					//die Länge des nächsten Strings
					int size = readIn.readInt ();
					byte[] b = new byte[size];
					readIn.read (b);
					myData.add(new String(b));
				}
				readIn.close ();
			}
			catch (IOException ex) {
				return null;
			}
			String[] result = new String[myData.size()];
			return myData.toArray(result);
		}
		return null;
	}
	
}
