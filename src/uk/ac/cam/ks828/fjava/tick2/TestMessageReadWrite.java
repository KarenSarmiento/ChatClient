package uk.ac.cam.ks828.fjava.tick2;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

class TestMessageReadWrite {

    // Create an instance of "TestMessage" with "text" set
    // to "message" and serialise it into a file called "filename".
    // Return "true" if write was successful; "false" otherwise.
    static boolean writeMessage(String message, String filename) {
        TestMessage testMessage = new TestMessage(message);
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(testMessage);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	    return true;
    }

    static String readMessage(String location) {
	// If "location" begins with "http://" or "https://" then
	// attempt to download and deserialise an instance of
	// TestMessage; you should use the java.net.URL and
	// java.net.URLConnection classes.  If "location" does not
	// begin with "http://" or "https://" attempt to deserialise
	// an instance of TestMessage by assuming that "location" is
	// the name of a file in the filesystem.
	//
	// If deserialisation is successful, return a reference to the 
	// field "text" in the deserialised object. In case of error, 
	// return "null".
        TestMessage result;
        try {
            if (location.startsWith("http://") || location.startsWith("https://")) {
                // Treat String as java.net.URL, which represents a pointer to a resource on the WWW.
                try {
                    URL url = new URL(location);
                    URLConnection connection = url.openConnection();
                    InputStream urlInputStream = connection.getInputStream();
                    // download the text from the url
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(urlInputStream);
                    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                    int bytesRead = -1;
                    byte[] buffer = new byte[1024];
                    String resultInter = "";
                    while ((bytesRead = urlInputStream.read(buffer)) != -1) {
                        resultInter += new String(buffer);
                    }
                    result = (TestMessage) objectInputStream.readObject();
                    urlInputStream.close();
                    bufferedInputStream.close();
                    objectInputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            } else /* Assume that it is a file path */{
                FileInputStream fileInputStream = new FileInputStream(location);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                result = (TestMessage) objectInputStream.readObject();
                fileInputStream.close();
                bufferedInputStream.close();
                objectInputStream.close();
            }
        } catch (IOException | ClassNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
	    return result.getMessage();
    }

    public static void main(String args[]) {
        String urlText = "https://www.cl.cam.ac.uk/teaching/current/FJava/testmessage-ks828.jobj";
        System.out.println("URL: readMessage(urlText) = " + readMessage(urlText));

        writeMessage("Hello I wrote this message!", "file.jobj");
        System.out.println("FILE: readMessage(\"file.jobj\") = " + readMessage("file.jobj"));
    }
}
