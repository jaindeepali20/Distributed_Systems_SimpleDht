package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.util.Log;

import static android.content.ContentValues.TAG;


    public class SimpleDhtProvider extends ContentProvider {

    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final String[] ports = {REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};
    static final String[] plist = {"5554", "5556", "5558", "5560", "5562"};
    static final int SERVER_PORT = 10000;
    Uri mUri = buildUri("content", "content://edu.buffalo.cse.cse486586.simpledht.provider");
    String myPort=null;
    ArrayList<String> arr = new ArrayList<String>();
    String pred=null;
    String succ=null;
    HashMap<String, String> hm = new HashMap<String, String>();
    String myId;
    String filename;
    HashMap<String, String> local = new HashMap<String, String>();
    TreeMap<String, String> hashPortMap = new TreeMap<String, String>();
//    Map<String, String> treeMap = new TreeMap<String, String>();
    Context con;
    String avd;
    int counter=0;
    String portStr;
    String hashPort=null;



        private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        try {
            if (selection.equals("*") && pred.equals(myId) && succ.equals(myId)) {

                for (Map.Entry<String, String> entry : local.entrySet()) {
                    System.out.println(entry.getKey() + " => " + entry.getValue());
                    Log.i(TAG, "SELECTION : " + selection);
                    Log.i(TAG, entry.getKey() + " => " + entry.getValue());
//                    matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                    local.remove(entry.getKey());
                }
            }

            else if (selection.equals("@")) {

                for (Map.Entry<String, String> entry : local.entrySet()) {
                    System.out.println(entry.getKey() + " => " + entry.getValue());
                    Log.i(TAG, "SELECTION : " + selection);
                    Log.i(TAG, entry.getKey() + " => " + entry.getValue());
//                    matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                    local.remove(entry.getKey());
                }
            }
            else if(selection.equals("*")){

                for (Map.Entry<String, String> entry : local.entrySet()) {
                    System.out.println(entry.getKey() + " => " + entry.getValue());
                    Log.i(TAG, "SELECTION : " + selection);
                    Log.i(TAG, entry.getKey() + " => " + entry.getValue());
//                    matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                    local.remove(entry.getKey());
                }

                for(int i=0; i<arr.size(); i++){
                    if(!arr.get(i).equals(myId)){
//                                String port = Integer.toString(Integer.parseInt(hashPortMap.get(arr.get(i)))*2);
                        String port = String.valueOf(Integer.parseInt(hashPortMap.get(arr.get(i)))*2);
                        Socket socket = null;
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        Object[] o = new Object[]{"Q"};
                        oos.writeObject(o);
                        oos.flush();
//                        oos.close();

                        //Reading from a socket
                        ObjectInputStream readObj = new ObjectInputStream(socket.getInputStream());
                        Object[] obj = (Object[]) readObj.readObject();

                        HashMap<String, String> hm = (HashMap<String, String>) obj[0];
                        for (Map.Entry<String, String> entry : hm.entrySet()) {
                            System.out.println(entry.getKey() + " => " + entry.getValue());
//                            matrixCursor.newRow().add("key", entry.getKey()).add("value", entry.getValue());
                            hm.remove(entry.getKey());
                        }

                        socket.close();
                    }
                }


            }
            else {
                String keySelectionHash = genHash(selection);
                String destAvdHash = getPosition(keySelectionHash);
                String currentAvdHash = genHash(portStr);
                Log.i(TAG,"key"+selection);
                Log.i(TAG,"Hash of key"+genHash(selection));

                Log.i(TAG,"Current avd ->"+currentAvdHash+"---"+"destAvd ->"+destAvdHash);

                if(destAvdHash.equals(currentAvdHash))
                {
                    Log.i(TAG,"Case 1");
//                  matrixCursor.newRow().add("key",selection).add("value",local.get(selection));
                    local.remove(selection);
                }
                else{
                    Socket socket = null;
                    String port = String.valueOf(Integer.parseInt(hashPortMap.get(destAvdHash))*2);
                    Log.i(TAG,"port"+port);


                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    Object[] o = new Object[]{"Q"};
                    Log.i(TAG," object written "+o[0]);
                    oos.writeObject(o);
                    Log.d(TAG, "*********** In senTest: Wrote object msgToSend: (Sent insert) - <" + selection + ">");
                    oos.flush();
//                oos.close();
                    ObjectInputStream readObj = new ObjectInputStream(socket.getInputStream());
                    Log.i(TAG,"Read stream created");
                    Object[] obj = (Object[]) readObj.readObject();
                    Log.i(TAG,"Read object"+obj[0]);

                    HashMap<String, String> hm = (HashMap<String, String>) obj[0];
                    for (Map.Entry<String, String> entry : hm.entrySet()) {
                        System.out.println(entry.getKey() + " => " + entry.getValue());
//                                    if(selection.equals(entry.getKey())){
//                                        matrixCursor.newRow().add("key", entry.getKey()).add("value", entry.getValue());
//                                    }

                    }
//                    matrixCursor.newRow().add("key", selection).add("value", hm.get(selection));
                    hm.remove(selection);

                }

            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "ClientTask UnknownHostException :  " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "ClientTask socket IOException :  " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPosition(String hs){
        Log.i(TAG, "Array elements: "+arr);
        Log.i(TAG, "Key Hash: "+hs);
        if(hs.compareTo(arr.get(0))<0){
            return arr.get(0);
        }
        else if(hs.compareTo(arr.get(arr.size()-1))>0){
            return arr.get(0);
        }
        else
        {
            for(int i=1; i<arr.size(); i++) {
                if(hs.compareTo( arr.get(i-1) )>0 && hs.compareTo( arr.get(i) )<=0 ) {
                    return arr.get(i);
                }
            }
        }

        return null;
    }

    public void setPredSucc(ArrayList<String> arr){
            int index = arr.indexOf(hashPort);
            int size = arr.size();

            if(index+1==size)
            {
                 succ=arr.get(0);
            }
            else
            {
                 succ=arr.get(index+1);
            }

            if(index==0)
            {
                pred=arr.get(size-1);
            }
            else{
                pred=arr.get(index-1);
            }
    }


        public String getPort(String hash)
    {

        for(int i=0; i< plist.length; i++)
        {
            try{
                if(genHash(plist[i]).equals(hash)){
                    Log.i(TAG,"Ports[i]"+ports[i]);
                    return ports[i];
                }
            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        String k = (String) values.get("key");
        String v = values.getAsString("value");
        String keyHash="";
        try {
            keyHash = genHash(k);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"arr "+arr);
        String destAvdHash = getPosition(keyHash);
        String str = "I:"+k+":"+v;
        Log.d(TAG, "Message to be saved: " + str);
        Log.d(TAG, "Hash where the value needs to be saved: " + destAvdHash);
        Log.d(TAG, "Port corresponding to the hash: " + destAvdHash + " is: " + hashPortMap.get(destAvdHash));
        String port = String.valueOf(Integer.parseInt(hashPortMap.get(destAvdHash))*2);

        Log.i(TAG,"portStr"+portStr);

        if(pred.equals(myId) && succ.equals(myId))
        {
            local.put(k,v);
        }
        else
        {
            if(port.equals(myPort)){
                local.put(k,v);
            }
            else{
                sendText(str,port);
            }
        }

        return uri;
    }


    public String getPredcessor(String myId)
    {

        for(int i=arr.size()-1; i>=0; i--)
        {
            if(arr.get(i).compareTo( myId )<0)
            {
                return arr.get(i);
            }

        }
        if(arr.size()>=1)
        {
            //return last element of arr
            return arr.get(arr.size()-1);
        }
        return null;
    }

    public String getSuccessor(String myId)
    {

        for(int i=0; i<arr.size(); i++)
        {
            if(myId.compareTo( arr.get(i) )<0)
            {
                return arr.get(i);
            }
        }
        if(arr.size()>=1)
        {
            //return last element of arr
            return arr.get(0);
        }
        return null;
    }

    public void sendText(String msg, String port)
        {
            Socket socket = null;
            try {
                String[] m = msg.split(":");
                String identifier = m[0];
                String k=m[1];
                String v=m[2];

                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Object[] o = new Object[]{identifier, k, v};
                oos.writeObject(o);
                Log.d(TAG, "*********** In senTest: Wrote object msgToSend: (Sent insert) - <" + k + v + ">");
//                oos.flush();
//                oos.close();
            } catch (Exception e) {
                Log.e(TAG,"Couldn't create socket in insert"+e.getMessage());
            }
        }

        @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
            try
            {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                Log.i(TAG,"************* Created ServerSocket");
                new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            }
            catch (IOException e)
            {
                Log.e(TAG, "************* Can't create a ServerSocket");
                Log.e(TAG, e.getMessage());


            }

        Context context=getContext();

        TelephonyManager tel = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));    //11108

            try {
                hashPort=genHash(portStr);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            for(String str: plist)
            {
            try {
                hashPortMap.put(genHash(str), str);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            }

        Log.i(TAG, "portStr : "+portStr);


        //For every avd->call the client task


            try
            {
                Log.i(TAG,"portStr"+portStr);
                myId =genHash(portStr);
                Log.i(TAG,"myId"+myId);
                pred=succ=myId;
                String str = "J:"+myId;

                Log.i(TAG,"********* Current avd : portStr->"+portStr+ "    hash ->"+myId);

                arr.add(myId);
                if(portStr.equals("5554"))
                {
                    Log.i(TAG,"In avd0 (onCreate)"+arr);
                }
                else
                {
//                    sendText(str,"11108");
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,str,"11108");
                }



            }
            catch(Exception e){
                Log.e(TAG,"************ Client Task can't be called!"+e.getMessage());
            }

        return true;
    }


    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        String message;
//        ObjectInputStream readObj=null;

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            Log.i(TAG,"In server task");
//            Socket soc=null;
            while (true) {

                try {
                    Log.d(TAG, "Currently on this line");
                    Socket soc = serverSocket.accept();
                    Log.d(TAG, "Socket created");
                    ObjectInputStream readObj = new ObjectInputStream(soc.getInputStream());
                    Log.d(TAG, "Input stream created");
                    Object[] obj = (Object[]) readObj.readObject();
                    Log.d(TAG, "Object Read: " + obj[0]);
//                    String[] str = ((String) obj[0]).split(":");
                    String idn=(String) obj[0];


                    if (idn.equals("J")) {
                        avd = (String) obj[1];      //hash of avd sending join request
                        if (!arr.contains(avd)) {
                            arr.add(avd);
                            Collections.sort(arr);
                            Socket[] socket = new Socket[arr.size()];

                            for (int i = 0; i < arr.size(); i++) {
                                socket[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(ports[i]));
                                ObjectOutputStream oos2 = new ObjectOutputStream(socket[i].getOutputStream());
                                Object[] objToSend = new Object[]{"B", arr};
                                oos2.writeObject(objToSend);
                                oos2.flush();

                            }
                        }
//                        readObj.close();
//                        soc.close();
                    }
                    if (idn.equals("B")) {


                        arr = (ArrayList<String>) obj[1];

                        setPredSucc(arr);
                        Log.i(TAG,"Array - "+arr);
                        Log.i(TAG,"Predecessor : "+pred);
                        Log.i(TAG,"Successor : "+succ);

                    }
                    if (idn.equals("I")) {
                        Log.i(TAG,"In I");
                        String k=(String)obj[1];
                        String v=(String)obj[2];


                        local.put(k,v);
                        Log.i("TAG","<"+k+" , "+v+"> inserted !!!");

                    }
                    if (idn.equals("Q")) {
                        Log.i(TAG, "In Q*********");


                            try {
                                Log.i(TAG,"in server of query");
                                ObjectOutputStream oos2 = new ObjectOutputStream(soc.getOutputStream());


//                                    for( Map.Entry<String, StString port = Integer.toString(Integer.parseInt(hashPortMap.get(arr.get(arr.size()-1)))*2);ring> entry : local.entrySet() ){
//                                        o = new Object[]{entry.getKey(), entry.getValue()};
//                                    }

//                                global.putAll(local);
                                Log.i(TAG,"local"+local);
                                Object[] o = new Object[]{local};
//                                Log.i(TAG,"object written"+o[1]);
                                oos2.writeObject(o);
//                                oos2.flush();
//                                oos2.close();

                            } catch (Exception e) {
                                Log.e(TAG, "*********** Couldn't create socket* :  " + e.getMessage());


                            }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "*Couldn't create serverSocket in server task "+e.getMessage());
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "#Couldn't create serverSocket "+e.getMessage());
                    e.printStackTrace();
                }
}



        }
        }

    private class ClientTask extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... msgs) {

                String[] m = msgs[0].split(":");
                String flag = m[0];
                Log.i(TAG, "Flag" + flag);

                if (flag.equals("J") ){
                    try {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(msgs[1]));
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        Object[] o = new Object[]{flag, m[1]};
                        oos.writeObject(o);
//                        oos.flush();

                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't create Socket " + e.getMessage());
                    }
            }
                if (flag.equals("B")){
                    try {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(msgs[1]));
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        Object[] o = new Object[]{flag, arr};
                        oos.writeObject(o);
//                        oos.flush();

                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't create Socket " + e.getMessage());
                    }
                }

//                if (flag.equals("I")) {
//                    try {
//                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(msgs[1]));
//                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                        Object[] o = new Object[]{flag, m[1], m[2]};
//                        oos.writeObject(o);
//                        Log.d(TAG, "*********** In Client class: Wrote object msgToSend: (Sent insert) - <" + m[1] + m[2] + ">");
//                        oos.flush();
//                        oos.close();
////                    socket.close();
//
//                    } catch (Exception e) {
//                        Log.e(TAG, "Couldn't create socket (in insert)  " + e.getMessage());
//                    }
//                }

                return null;
            }

        }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                                String sortOrder) {
                // TODO Auto-generated method stub
        Log.i(TAG,"In Query");

                MatrixCursor matrixCursor = new MatrixCursor(new String[]{"key", "value"});
                //        MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
                con = getContext();


                try {
                    if (selection.equals("*") && pred.equals(myId) && succ.equals(myId)) {

                        for (Map.Entry<String, String> entry : local.entrySet()) {
                            System.out.println(entry.getKey() + " => " + entry.getValue());
                            Log.i(TAG, "SELECTION : " + selection);
                            Log.i(TAG, entry.getKey() + " => " + entry.getValue());
                            matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                        }
                    }

                    else if (selection.equals("@")) {

                        for (Map.Entry<String, String> entry : local.entrySet()) {
                            System.out.println(entry.getKey() + " => " + entry.getValue());
                            Log.i(TAG, "SELECTION : " + selection);
                            Log.i(TAG, entry.getKey() + " => " + entry.getValue());
                            matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                        }
                    }
                    else if(selection.equals("*")){

                        for (Map.Entry<String, String> entry : local.entrySet()) {
                            System.out.println(entry.getKey() + " => " + entry.getValue());
                            Log.i(TAG, "SELECTION : " + selection);
                            Log.i(TAG, entry.getKey() + " => " + entry.getValue());
                            matrixCursor.newRow().add("key",entry.getKey()).add("value",entry.getValue());
                        }

                        for(int i=0; i<arr.size(); i++){
                            if(!arr.get(i).equals(myId)){
//                                String port = Integer.toString(Integer.parseInt(hashPortMap.get(arr.get(i)))*2);
                                String port = String.valueOf(Integer.parseInt(hashPortMap.get(arr.get(i)))*2);
                                Socket socket = null;
                                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                Object[] o = new Object[]{"Q"};
                                oos.writeObject(o);
                                oos.flush();
//                        oos.close();

                                //Reading from a socket
                                ObjectInputStream readObj = new ObjectInputStream(socket.getInputStream());
                                Object[] obj = (Object[]) readObj.readObject();

                                HashMap<String, String> hm = (HashMap<String, String>) obj[0];
                                for (Map.Entry<String, String> entry : hm.entrySet()) {
                                    System.out.println(entry.getKey() + " => " + entry.getValue());
                                    matrixCursor.newRow().add("key", entry.getKey()).add("value", entry.getValue());
                            }

                                socket.close();
                            }
                        }


                    }
                    else {
                        String keySelectionHash = genHash(selection);
                        String destAvdHash = getPosition(keySelectionHash);
                        String currentAvdHash = genHash(portStr);
                        Log.i(TAG,"key"+selection);
                        Log.i(TAG,"Hash of key"+genHash(selection));

                        Log.i(TAG,"Current avd ->"+currentAvdHash+"---"+"destAvd ->"+destAvdHash);

                        if(destAvdHash.equals(currentAvdHash))
                        {
                            Log.i(TAG,"Case 1");
                            matrixCursor.newRow().add("key",selection).add("value",local.get(selection));
                        }
                        else{
                            Socket socket = null;
                            String port = String.valueOf(Integer.parseInt(hashPortMap.get(destAvdHash))*2);
                            Log.i(TAG,"port"+port);


                                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(port));
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                Object[] o = new Object[]{"Q"};
                                Log.i(TAG," object written "+o[0]);
                                oos.writeObject(o);
                                Log.d(TAG, "*********** In senTest: Wrote object msgToSend: (Sent insert) - <" + selection + ">");
                                oos.flush();
//                oos.close();
                                ObjectInputStream readObj = new ObjectInputStream(socket.getInputStream());
                                Log.i(TAG,"Read stream created");
                                Object[] obj = (Object[]) readObj.readObject();
                                Log.i(TAG,"Read object"+obj[0]);

                                HashMap<String, String> hm = (HashMap<String, String>) obj[0];
                                for (Map.Entry<String, String> entry : hm.entrySet()) {
                                    System.out.println(entry.getKey() + " => " + entry.getValue());
//                                    if(selection.equals(entry.getKey())){
//                                        matrixCursor.newRow().add("key", entry.getKey()).add("value", entry.getValue());
//                                    }

                                }
                            matrixCursor.newRow().add("key", selection).add("value", hm.get(selection));

                        }

                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException :  " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException :  " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }



                Log.i(TAG,"Query Successful!!!");
                return matrixCursor;
            }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
