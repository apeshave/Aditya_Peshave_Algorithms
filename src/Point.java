import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aditya
 */
public class Point implements Comparable<Point>
{
    
    public String name;
    public int x;
    public int y;
    public double distanceToDestination;
    String bloodGroup;
    
    Point(String name,int x,int y)
    {
        this.name=name;
        this.x=x;
        this.y=y;   
        this.bloodGroup=randomlyAssign();
        
    }
    Point(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
    Point()
    {
        
    }
    
    private String randomlyAssign() 
    {
        String bloodGroup=null;
        Random r=new Random();
        int randomNum=r.nextInt(8);
        if(randomNum>=0 && randomNum<=2)
        {
            bloodGroup="A";
        }
        else if(randomNum>=3 && randomNum<=5)
        {
            bloodGroup="B";
        }
        if(randomNum>=6 && randomNum<=8)
        {
            bloodGroup="AB";
        }
        return bloodGroup;
    }
    
    public void calculateDistance(Point Destination)
    {
        Point source=this;
        double x=Math.pow((Destination.x-source.x),2);
        double y=Math.pow((Destination.y-source.y),2);
        double distance=Math.sqrt(x+y);
        this.distanceToDestination=distance;
        
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(Point o) 
    {
        int compareDistance=(int)o.distanceToDestination;
        return (int)this.distanceToDestination-compareDistance;
    }

    @Override
    public boolean equals(Object obj) {
        Point p=(Point)obj;
        if(p.x==this.x && p.y==this.y)
        {
            return true;
        }
        return false;
        
    }    
}
