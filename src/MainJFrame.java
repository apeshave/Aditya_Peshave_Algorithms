
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aditya
 */
public class MainJFrame extends javax.swing.JFrame{

    private ArrayList<Point> cities;
    Graphics g1;
    Drone d1,d2;
  
    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        
        initComponents();
        drone1Label.setVisible(false);
        drone2Label.setVisible(false);
        status1.setVisible(false);
        status2.setVisible(false);
        
        d1=new Drone(drone1Label,weatherJLabel);
        d2=new Drone(drone2Label,weatherJLabel1);
        
        cities=new ArrayList<Point>();
    
        initAllCities();
        initComboBox();
        
    }
    
    public void initComboBox()
    {
        destinationComboBox.removeAllItems();
        bloodComboBox.removeAllItems();
        for(Point p:cities)
        {
            destinationComboBox.addItem(p);
        }
        bloodComboBox.addItem("A");
        bloodComboBox.addItem("B");
        bloodComboBox.addItem("AB");
    }
    
    public void initAllCities()
    {
        Point p1=new Point("Helena",helena.getX(),helena.getY());
        System.out.println(p1.bloodGroup);
        helenaBlood.setText(p1.bloodGroup);
        Point p2=new Point("Raleigh",raleigh.getX(),raleigh.getY());
        RaleighBlood.setText(p2.bloodGroup);
        Point p3=new Point("Arizona",arizona.getX(),arizona.getY());
        arizonaBlood.setText(p3.bloodGroup);
        Point p4=new Point("Detroit",detroit.getX(),detroit.getY());
        detroitBlood.setText(p4.bloodGroup);
        Point p5=new Point("Memphis",memphis.getX(),memphis.getY());
        memphisiBlood.setText(p5.bloodGroup);
        Point p6=new Point("Lincoln",lincoln.getX(),lincoln.getY());
        lincolnBlood.setText(p6.bloodGroup);
        Point p7=new Point("Sacramento",Sacramento.getX(),Sacramento.getY());
        sacramentoBlood.setText(p7.bloodGroup);
        Point p8=new Point("Augusta",augusta.getX(),augusta.getY());
        augustaBlood.setText(p8.bloodGroup);
        Point p9=new Point("Austin",austin.getX(),austin.getY());
        autinBlood.setText(p9.bloodGroup);
        
        //Add all the points to the list of cities
        addPointsToCityList(p1,p2,p3,p4,p5,p6,p7,p8,p9);
    }
    
    public void addPointsToCityList(Point... p)
    {
        for(Point pt : p)
            cities.add(pt);
    }
    
    public void moveDroneInPath(ArrayList<Point> straightLine,final Drone d,
            Point source,JLabel weatherLabel, final JProgressBar fuelBar,final JLabel statusLabel) 
    {
            final JLabel droneLabel = d.droneLabel;
            Point destination = d.finalDestination;
            int x = 0, y = 0;
            droneLabel.setLocation(source.x, source.y);
            droneLabel.setVisible(true);
            x = source.x;
            y = source.y;
            int x1 =destination.x;
            int y1 = destination.y;
            final ArrayList<Point> line=generateRandomPointBetweenEdge(x, y, x1, y1,
                    straightLine,weatherLabel,fuelBar,statusLabel);
            
            ActionListener al = new ActionListener() {
            int var=0; 
            int progress=d.fuel;
            Point p=null;
            ArrayList<Point> deflectedPathPoints = new ArrayList<Point>();
            int n=line.size();
            
            @Override
            public void actionPerformed(ActionEvent e) {
              boolean flag = false;  
              
               if(var>=n-1|| progress<=55)
               {
                ((Timer)e.getSource()).stop();
                fuelBar.setStringPainted(true);
                fuelBar.setValue(progress);
                d.fuel=progress;
                d.isFuelLow=true;
                
                notificationTextArea.setText(notificationTextArea.getText() + "\nLow on fuel.\nRedirecting...");
                
                flag = true;
                if(progress<=55)
                {
                    Point point=getClosestPoint(droneLabel.getX(), droneLabel.getY());
                    deflectedPathPoints=updateLine(droneLabel.getX(), 
                    droneLabel.getY(), point,deflectedPathPoints);
                    ArrayList<Point> newLine=getLineFromBresenhamAlgo(point.x,point.y,d.finalDestination.x,d.finalDestination.y);
                    moveDroneInStraightLine(deflectedPathPoints,d,point,newLine,fuelBar,statusLabel);
                }
               }
               if(!flag){
                    Point p=line.get(var);
                    droneLabel.setLocation(p.x, p.y);
                    fuelBar.setStringPainted(true);
                    fuelBar.setValue(progress);
                    d.fuel=progress;
                    progress-=3;
                    var+=30;
               }       
            }
        };
        Timer t = new Timer(1000, al);
        t.start();
    }
    
    public ArrayList<Point> generateRandomPointBetweenEdge(int x0,int y0,int x1,int y1,
            ArrayList<Point> straightLine,JLabel weatherLabel,
            JProgressBar fuelBar,JLabel statusLabel)
    {
        int xmin=Math.min(x1,x0);
        int xmax=Math.max(x1,x0);
        int ymin=Math.min(y1,y0);
        int ymax=Math.max(y1,y0);
        Point deflectedPoint=null;
        if(isWeather())
        {
            weatherLabel.setVisible(true);
            statusLabel.setText("BAD WEATHER");
            statusLabel.setForeground(Color.RED);
            statusLabel.setVisible(true);
            deflectedPoint = drawWeatherInfectedArea(x0,y0,x1,y1,straightLine,weatherLabel);
        }
        else
        {
            weatherLabel.setVisible(false);
            statusLabel.setText("WINDY");
            statusLabel.setForeground(Color.RED);
            statusLabel.setVisible(true);
            deflectedPoint = findBoxFromPoints(xmin,ymin,xmax,ymax,straightLine);
        }
        if(deflectedPoint!=null)
        {
            xmin=Math.min(deflectedPoint.x, x1);
            xmax=Math.max(deflectedPoint.x, x1);
            ymin=Math.min(deflectedPoint.y,y1);
            ymax=Math.max(deflectedPoint.y,y1);
            ArrayList<Point> pathFirst=getLineFromBresenhamAlgo(x0, y0,deflectedPoint.x,deflectedPoint.y);
            Point deflectedPoint2 = findBoxFromPoints(xmin,ymin,xmax,ymax,straightLine);
            if(deflectedPoint2!=null)
            {
                ArrayList<Point> pathSecond = getLineFromBresenhamAlgo(deflectedPoint.x,
                        deflectedPoint.y, deflectedPoint2.x, deflectedPoint2.y);
                ArrayList<Point> pathThird = getLineFromBresenhamAlgo(deflectedPoint2.x,
                        deflectedPoint2.y, x1, y1);
               
                straightLine = pathFirst;
                for(Point p:pathSecond)
                   straightLine.add(p);
                
                for(Point p:pathThird)
                     straightLine.add(p);
            }
           }
       return straightLine;   
     }
     
    public boolean isWeather()
    {
        Random r=new Random();
        //setting the probability to 70% for weather
        
        int value=r.nextInt(10);
        if(value <= 7)
            return true;
        else
            return false;
    }
    
   public Point findBoxFromPoints(int x0, int y0, int x1, int y1,ArrayList<Point> straightLine) {
        Point p = new Point();
        if(x0 == x1 && y0 == y1)
            return null;
        Random r = new Random();

        int ax = Math.abs(x0 + (Math.abs((x1 - x0)) / 3));
        int bx = Math.abs(x1 - (Math.abs((x1 - x0)) / 3));
        int randx = Math.abs(r.nextInt(Math.abs(bx - ax)) + ax);

        int ay = Math.abs(y0 + (Math.abs((y1 - y0)) / 3));
        int by = Math.abs(y1 - (Math.abs((y1 - y0)) / 3));
        int randy = Math.abs(r.nextInt(Math.abs(by - ay)) + ay);

        p.x = randx;
        p.y = randy;

        return p;

    }
    public ArrayList<Point> getLineFromBresenhamAlgo(int x0,int y0,int x1,int y1)
    {
        ArrayList<Point> newLine = new ArrayList<Point>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        int sx = x0 < x1 ? 1 : -1; 
        int sy = y0 < y1 ? 1 : -1; 
        
        int err = dx-dy;
        int e2;
        while (true) 
        {
            Point p=new Point();
            
            p.x=x0;
            p.y=y0;
            
            newLine.add(p);
            
            if (x0 == x1 && y0 == y1) 
                break;
            
            e2 = 2 * err;
            
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        return newLine;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        drone1Label = new javax.swing.JLabel();
        Sacramento = new javax.swing.JLabel();
        memphis = new javax.swing.JLabel();
        lincoln = new javax.swing.JLabel();
        detroit = new javax.swing.JLabel();
        augusta = new javax.swing.JLabel();
        austin = new javax.swing.JLabel();
        raleigh = new javax.swing.JLabel();
        helena = new javax.swing.JLabel();
        arizona = new javax.swing.JLabel();
        status1 = new javax.swing.JLabel();
        memphisiBlood = new javax.swing.JLabel();
        sacramentoBlood = new javax.swing.JLabel();
        helenaBlood = new javax.swing.JLabel();
        autinBlood = new javax.swing.JLabel();
        lincolnBlood = new javax.swing.JLabel();
        arizonaBlood = new javax.swing.JLabel();
        detroitBlood = new javax.swing.JLabel();
        augustaBlood = new javax.swing.JLabel();
        RaleighBlood = new javax.swing.JLabel();
        destinationComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bloodComboBox = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        drone2Label = new javax.swing.JLabel();
        weatherJLabel = new javax.swing.JLabel();
        weatherJLabel1 = new javax.swing.JLabel();
        fuel1 = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fuel2 = new javax.swing.JProgressBar();
        status2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationTextArea = new javax.swing.JTextArea();
        mapLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        drone1Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/airplane.png"))); // NOI18N
        drone1Label.setText("jLabel1");
        jPanel1.add(drone1Label);
        drone1Label.setBounds(30, 50, 40, 20);

        Sacramento.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Sacramento.setForeground(new java.awt.Color(255, 255, 255));
        Sacramento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        Sacramento.setText("Sacramento");
        jPanel1.add(Sacramento);
        Sacramento.setBounds(10, 240, 110, 40);

        memphis.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        memphis.setForeground(new java.awt.Color(255, 255, 255));
        memphis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        memphis.setText("Memphis");
        jPanel1.add(memphis);
        memphis.setBounds(700, 370, 100, 30);

        lincoln.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lincoln.setForeground(new java.awt.Color(255, 255, 255));
        lincoln.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        lincoln.setText("Lincoln");
        jPanel1.add(lincoln);
        lincoln.setBounds(420, 230, 100, 40);

        detroit.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        detroit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        detroit.setText("Detroit");
        jPanel1.add(detroit);
        detroit.setBounds(750, 170, 80, 40);

        augusta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        augusta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        augusta.setText("Augusta");
        jPanel1.add(augusta);
        augusta.setBounds(1000, 30, 90, 40);

        austin.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        austin.setForeground(new java.awt.Color(255, 255, 255));
        austin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        austin.setText("Austin");
        jPanel1.add(austin);
        austin.setBounds(460, 500, 80, 30);

        raleigh.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        raleigh.setForeground(new java.awt.Color(255, 255, 255));
        raleigh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        raleigh.setText("Raleigh");
        jPanel1.add(raleigh);
        raleigh.setBounds(890, 330, 90, 30);

        helena.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        helena.setForeground(new java.awt.Color(255, 255, 255));
        helena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        helena.setText("Helena");
        jPanel1.add(helena);
        helena.setBounds(250, 80, 80, 40);

        arizona.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        arizona.setForeground(new java.awt.Color(255, 255, 255));
        arizona.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pin_2.png"))); // NOI18N
        arizona.setText("Arizona");
        jPanel1.add(arizona);
        arizona.setBounds(190, 410, 80, 30);

        status1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        status1.setText("jLabel1");
        jPanel1.add(status1);
        status1.setBounds(1060, 530, 120, 20);

        memphisiBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        memphisiBlood.setText("jLabel1");
        jPanel1.add(memphisiBlood);
        memphisiBlood.setBounds(710, 390, 90, 50);

        sacramentoBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        sacramentoBlood.setText("jLabel1");
        jPanel1.add(sacramentoBlood);
        sacramentoBlood.setBounds(30, 260, 100, 50);

        helenaBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        helenaBlood.setText("jLabel1");
        jPanel1.add(helenaBlood);
        helenaBlood.setBounds(250, 90, 110, 70);

        autinBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        autinBlood.setText("jLabel1");
        jPanel1.add(autinBlood);
        autinBlood.setBounds(460, 520, 90, 40);

        lincolnBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        lincolnBlood.setText("jLabel1");
        jPanel1.add(lincolnBlood);
        lincolnBlood.setBounds(420, 250, 100, 50);

        arizonaBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        arizonaBlood.setText("jLabel1");
        jPanel1.add(arizonaBlood);
        arizonaBlood.setBounds(190, 420, 120, 50);

        detroitBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        detroitBlood.setText("jLabel1");
        jPanel1.add(detroitBlood);
        detroitBlood.setBounds(750, 190, 100, 50);

        augustaBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        augustaBlood.setText("jLabel1");
        jPanel1.add(augustaBlood);
        augustaBlood.setBounds(1000, 50, 90, 40);

        RaleighBlood.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        RaleighBlood.setText("jLabel1");
        jPanel1.add(RaleighBlood);
        RaleighBlood.setBounds(890, 350, 90, 50);

        destinationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(destinationComboBox);
        destinationComboBox.setBounds(1060, 130, 120, 30);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel1.setText("DESTINATION");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(1070, 90, 110, 30);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel2.setText("BLOOD GROUP");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(1060, 170, 110, 30);

        bloodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(bloodComboBox);
        bloodComboBox.setBounds(1060, 200, 120, 30);

        jButton1.setText("ORDER");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(1060, 250, 120, 30);

        drone2Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/airplane.png"))); // NOI18N
        drone2Label.setText("jLabel1");
        jPanel1.add(drone2Label);
        drone2Label.setBounds(30, 10, 40, 20);
        jPanel1.add(weatherJLabel);
        weatherJLabel.setBounds(480, 40, 40, 20);
        jPanel1.add(weatherJLabel1);
        weatherJLabel1.setBounds(700, 380, 0, 30);
        jPanel1.add(fuel1);
        fuel1.setBounds(1060, 570, 130, 20);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("DRONE 2");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(980, 600, 70, 30);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("DRONE 1");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(980, 530, 70, 20);
        jPanel1.add(fuel2);
        fuel2.setBounds(1060, 640, 130, 20);

        status2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        status2.setText("jLabel5");
        jPanel1.add(status2);
        status2.setBounds(1060, 600, 130, 30);

        notificationTextArea.setColumns(20);
        notificationTextArea.setLineWrap(true);
        notificationTextArea.setRows(5);
        jScrollPane1.setViewportView(notificationTextArea);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(980, 296, 210, 220);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jPanel1, gridBagConstraints);

        mapLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usa-map-blue-no-label.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mapLabel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        notificationTextArea.setText("");
        
        //Get the destination from the combobox 
        Point destination=(Point)destinationComboBox.getSelectedItem();
        
        //Get the blood type required
        
        String bloodRequired=(String)bloodComboBox.getSelectedItem();
        notificationTextArea.setText("Destinaiton: " + destination.name + 
                "\nBlood type required: "  + bloodRequired + "\nRunning checks...");
        //Calculate the nearest sources which have the specified blood supply
        ArrayList<Point> nearestSources=getNearestSourceWithRequiredBlood(destination, bloodRequired);
        
        d1.fuel = 100;
        d2.fuel = 100;
        
        if(nearestSources.size() >= 2)
        {
            
            d1.source=nearestSources.get(0);
            d2.source=nearestSources.get(1);

            d1.finalDestination=destination;
            d2.finalDestination=destination;

            ArrayList<Point> straightLine1=getLineFromBresenhamAlgo(d1.source.x,d1.source.y,destination.x,destination.y);
            ArrayList<Point> straightLine2=getLineFromBresenhamAlgo(d2.source.x,d2.source.y,destination.x,destination.y);

            notificationTextArea.setText(notificationTextArea.getText() + "\nDrone 1: \nSource : " +
                    nearestSources.get(0).name + "\nDrone 2:\nSource: " + nearestSources.get(1).name);
           
            
            moveDroneInPath(straightLine1,d1,d1.source,d1.weatherLabel,fuel1,status1);
            moveDroneInPath(straightLine2,d2,d2.source,d2.weatherLabel,fuel2,status2);
            
            notificationTextArea.setText(notificationTextArea.getText() + "\nReached Destination.");
           
        }
        else if(nearestSources.size() == 1)
        {
            d1.source=nearestSources.get(0);
            d1.finalDestination=destination;
            notificationTextArea.setText(notificationTextArea.getText() + "\nDrone 1: \nSource : " +
                    nearestSources.get(0).name);
            ArrayList<Point> straightLine1=getLineFromBresenhamAlgo(d1.source.x,d1.source.y,destination.x,destination.y);
            moveDroneInPath(straightLine1,d1,d1.source,d1.weatherLabel,fuel1,status1);
            
            notificationTextArea.setText(notificationTextArea.getText() + "\nReached Destination.");
        }
        else
        {
            notificationTextArea.setText(notificationTextArea.getText() + "No suitable cities found "
                    + "matching required critera."
                    + "\nMake another selection\nWaiting....");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public ArrayList<Point> getNearestSourceWithRequiredBlood(Point destination, String bloodRequired)
    {
        ArrayList<Point> possibleSources=new ArrayList<Point>();
        for(Point p: cities)
        {
            if((!p.equals(destination))&&p.bloodGroup.equals(bloodRequired))
            {
               p.calculateDistance(destination);
               possibleSources.add(p);
            }
        }
        Collections.sort(possibleSources);
        return possibleSources;
    }
    
    public Point getClosestPoint(int x,int y){
        ArrayList<Point> distance = new ArrayList<>();
        for(Point p : cities){
            p.calculateDistance(new Point(x,y));
            distance.add(p);
        }
        Collections.sort(distance);       
      
        Point point = distance.get(0);  
      
        return point;
    }
  
    public ArrayList<Point> updateLine(int x,int y,Point city,
            ArrayList<Point> pointList){
       
        ArrayList<Point> pathFirst =getLineFromBresenhamAlgo(x, y, 
                city.x, city.y);
        pointList = pathFirst;
        return pointList;
    }
    private Point selectRadomPointOnStraightLine(ArrayList<Point> straightLine)
    {
        Random r=new Random();
        List<Point> subList=straightLine.subList(straightLine.size()/3,((2*straightLine.size())/3));
        Point weatherPoint=null;
        if(subList.size()>1)
        {
            weatherPoint=subList.get(r.nextInt(subList.size()-1));
        }
        return weatherPoint;
    }
    
    private Point findDeflectedUsingWeatherPoint(int x0,int y0,int x1,int y1,Point weatherPoint,int radius) 
    {
        Point deflectedPoint=new Point();
        float slope=slopeOfPoints(x0, y0, x1, y1);
        System.out.println("Slope: " + slope);
        if(slope<0)
        {
            deflectedPoint.x=weatherPoint.x+(2*radius);
            deflectedPoint.y=weatherPoint.y+(2*radius);
        }
        else
        {
            deflectedPoint.x=weatherPoint.x+(3*radius);
            deflectedPoint.y=weatherPoint.y-(int)(Math.abs(1-slope)*radius);
        }
        return deflectedPoint;
        
    }
    public float slopeOfPoints(int x0,int y0,int x1,int y1)
    {
        float y=y1-y0;
        float x=x1-x0;
        return y/x;
    }

    private Point drawWeatherInfectedArea(int x0,int y0,int x1,int y1,
            ArrayList<Point> straightLine,JLabel weatherLabel)
    {
        Point weatherPoint=selectRadomPointOnStraightLine(straightLine);  
        Point deflectedPoint=null;
        if(weatherPoint!=null)
        {
        System.out.println("Weather point" + weatherPoint.x);
        int radius=getRandomRadius(x0,y0,x1,y1);
        if(radius!=0)
        {
            BufferedImage img = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_RGB );
            Image image = Toolkit.getDefaultToolkit().getImage("./src/no_fly.png");  
            Image scaled = image.getScaledInstance(radius*2,radius*2, 5);
            ImageIcon icon = new ImageIcon(scaled);
            weatherLabel.setIcon(icon);
            weatherLabel.setSize(radius*2+1, radius*2+1);
            weatherLabel.setVisible(true);
            weatherLabel.setLocation(new java.awt.Point(weatherPoint.x - radius/2,
                    weatherPoint.y - radius/2));
        }
            deflectedPoint=findDeflectedUsingWeatherPoint(x0,y0,x1,y1,
                    weatherPoint,radius);
        }
        else
        {
            deflectedPoint=straightLine.get(straightLine.size()/2);
        }
        return deflectedPoint;
    }

    private int getRandomRadius(int x0,int y0,int x1,int y1) {
        Random r=new Random();
        Point p1=new Point(x0,y0);
        Point p2=new Point(x1,y1);
        p1.calculateDistance(p2);
        int distance=(int)p1.distanceToDestination;
        int rad=0;
        if(distance>=40)
        {
        rad=r.nextInt(distance/15)+distance/20;
        }
        
        return rad;
    }

    public void moveDroneInStraightLine(
            final ArrayList<Point> deflectedPathPoints,final Drone d,
            final Point intermediatePoint,final ArrayList<Point> line,
            final JProgressBar fuelBar,final JLabel statusLabel){
          ActionListener al = new ActionListener() {
            int var = 0;
            int n = deflectedPathPoints.size();
            int progress = d.fuel;
            Point p =null;
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
               
                if (var >= n - 1) {
                    ((Timer) e.getSource()).stop();
                     d.fuel=100;
                     fuelBar.setValue(d.fuel);
                    flag = true;
                    d.isFuelLow=false;
                     
                    moveDroneInPath(line, d,intermediatePoint,d.weatherLabel,fuelBar,statusLabel);
                }
                if (!flag) {
                    p = deflectedPathPoints.get(var);        
                    d.droneLabel.setLocation(p.x, p.y); 
                    var += 30;
                    fuelBar.setStringPainted(true);
                    fuelBar.setValue(progress);
                    d.fuel=progress;
                    progress-=3;            
                }
            }
        };
        Timer t = new Timer(1000, al);
        t.start();
    }
  
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel RaleighBlood;
    private javax.swing.JLabel Sacramento;
    private javax.swing.JLabel arizona;
    private javax.swing.JLabel arizonaBlood;
    private javax.swing.JLabel augusta;
    private javax.swing.JLabel augustaBlood;
    private javax.swing.JLabel austin;
    private javax.swing.JLabel autinBlood;
    private javax.swing.JComboBox bloodComboBox;
    private javax.swing.JComboBox destinationComboBox;
    private javax.swing.JLabel detroit;
    private javax.swing.JLabel detroitBlood;
    private javax.swing.JLabel drone1Label;
    private javax.swing.JLabel drone2Label;
    private javax.swing.JProgressBar fuel1;
    private javax.swing.JProgressBar fuel2;
    private javax.swing.JLabel helena;
    private javax.swing.JLabel helenaBlood;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lincoln;
    private javax.swing.JLabel lincolnBlood;
    private javax.swing.JLabel mapLabel;
    private javax.swing.JLabel memphis;
    private javax.swing.JLabel memphisiBlood;
    private javax.swing.JTextArea notificationTextArea;
    private javax.swing.JLabel raleigh;
    private javax.swing.JLabel sacramentoBlood;
    private javax.swing.JLabel status1;
    private javax.swing.JLabel status2;
    private javax.swing.JLabel weatherJLabel;
    private javax.swing.JLabel weatherJLabel1;
    // End of variables declaration//GEN-END:variables

}
