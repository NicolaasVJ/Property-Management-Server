package Server;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
  
// Server class
public class nServer 
{
    // Vector to store active clients
    static Vector<ClientHandler> activeVector = new Vector<>();
      
    // counter for clients
    static int i = 0;
  
    public static void main(String[] args) throws IOException 
    {  try{
        File myObj = new File("calcServerConfig.txt");
      Scanner myReader = new Scanner(myObj);
     
        String data = myReader.nextLine();
      
      
      StringTokenizer st = new StringTokenizer(data, "");

                // server port
                int Ssocket = Integer.parseInt(st.nextToken());
                        
      myReader.close(); 
        ServerSocket ss = new ServerSocket(Ssocket);
          
        InetAddress localhost = InetAddress.getLocalHost();

        Socket s;
    
    
 JFrame f=new JFrame();  
    JOptionPane.showMessageDialog(f,"The property management calculation server is now running on "+(localhost.getHostAddress()).trim()+":"+Ssocket);
        // running infinite loop for getting
        // client request
        while (true) 
        {
            // Accept the incoming request
            s = ss.accept();
  
            System.out.println("New client request received : " + s);
              
            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
              
            System.out.println("Creating a new handler for this client...");
  
            // Create a new handler object for handling this request.
            ClientHandler clientHandler = new ClientHandler(s,"client" + i, dis, dos);
  
            // Create a new Thread with this object.
            Thread t = new Thread(clientHandler);
              
            System.out.println("Adding this client to active client list");
  
            // add this client to active clients list
            activeVector.add(clientHandler);
            // start the thread.
            t.start();
            // increment i for new client.
            i++;
  
        }
        }
    catch (FileNotFoundException e) {
           JOptionPane.showMessageDialog(null,"calcConfig.txt file not found, it should contain the port number used for clients to connect");
        } catch (IOException e) {
           JOptionPane.showMessageDialog(null,"Unable to read the calcServerConfig.txt file, it should contain ip:port");
        }
    catch(Exception e){
        JFrame a=new JFrame();
       JOptionPane.showMessageDialog(null,"Server is already running"); 
       System.exit(0);
    }
    }
  
}

// ClientHandler class
class ClientHandler implements Runnable 
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
      
    // constructor
    public ClientHandler(Socket s, String name,
                            DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }
    
  
    @Override
    public void run()throws NoSuchElementException {
  
        String received;
        while (true) 
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }
                else{
                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                 System.out.println("Tokens contained "+st.countTokens());//Can use this to get the data in client

                  
                String MsgToSend = st.nextToken();
                System.out.println("The message "+MsgToSend);
                String recipient = st.nextToken();//default #client0
                  System.out.println("The targeted reci " +recipient);
                 StringTokenizer loan = new StringTokenizer(received, "?");
               
                String sender= this.name;
               
                    
                recipient=sender;
                 float initialPrincipalP=Float.parseFloat(loan.nextToken());
                 System.out.println("1 "+initialPrincipalP);
                 float interestRateR= Float.parseFloat(loan.nextToken());
                 System.out.println("2 "+interestRateR);
                 float numPaymentsN= Float.parseFloat(loan.nextToken());
                 System.out.println("3 "+numPaymentsN);
                 
                 if(interestRateR < 1)
        {
            JOptionPane.showMessageDialog(null, "Please enter Annual Interest Rate","Add Interest Rate",2);
        }
        else
        {
            interestRateR=(interestRateR/12)/100;//Calculate per period
            System.out.println(interestRateR);//interest rate per month
        }
        
        //getting the Total Number of payments)
        if(numPaymentsN < 1)
        {
            JOptionPane.showMessageDialog(null, "Please enter the Total Number by YEAR!","Add Total Number of payments",2);
        }
        else
        {
            numPaymentsN=numPaymentsN*12;//Calculate number of months
            System.out.println(numPaymentsN);//how many months
        }
        
                 float monthlyPaymentA = 0;
                  monthlyPaymentA= (float) ((initialPrincipalP * interestRateR) / (1 - Math.pow(1 + interestRateR, -numPaymentsN)));
                   System.out.println("4 "+monthlyPaymentA);
                  MsgToSend=("Monthly Payment: "+" R "+String.valueOf(monthlyPaymentA) 
                +"\n Period : "+(int)numPaymentsN+" months"
                +"\n Total repayment amount: R "+(float)(monthlyPaymentA*numPaymentsN));
               
               
                // search for the recipient in the connected devices list.
                // activeVector is the vector storing client of active users
               // 
                for (ClientHandler mc : nServer.activeVector) 
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.isloggedin==true) 
                    {System.out.println(this.name);//who sent
                       // mc.dos.writeUTF(this.name+":"+MsgToSend);//Changes what the client receives.
                         mc.dos.writeUTF(MsgToSend);
                         
                        break;
                    }
                    }
                }
                this.s.close();
               
            } catch (IOException e) {
                  
                e.printStackTrace();
                break;
            }
            catch (NoSuchElementException e ) {
                    System.out.println("PLEASE SPECIFY RECIPIENT");
                    }
              
        }//*/
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
              
        }catch(IOException e){
            e.printStackTrace();
            
        }
    }
}
