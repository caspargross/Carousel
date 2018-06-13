package playground;

public class Read {
    private double start;
    private double length;
    private boolean direction;
    private String sequence;
    private String cigar;
    private boolean overlap;

    public Read (double start, double length, boolean direction, String sequence,String cigar,boolean overlap){
        // If direction = false (counter-clockwise) we set length at its negative value. Thus we save some work later (we can just add start+length and not have to worry about the direction in the math-part)
        if(direction){
            this.length = length;
        }
        else{
            this.length =length*(-1);
        }
        this.start = start;
        this.direction = direction;
        this.sequence = sequence;
        this.cigar = cigar;
        this.overlap = overlap;
    }
    //SETTER
    public void setLength(double length){
        this.length = length;
    }
    public void setStart(double start){
        this.start = start;
    }
    public void setDirection(boolean direction){
        this.direction = direction;
    }
    public void setSequence(String sequence){
        this.sequence = sequence;
    }
    public void setCigar(String cigar){
        this.cigar = cigar;
    }
    public void setOverlap(boolean overlap){
        this.overlap = overlap;
    }

    //GETTER
    public double getLength() {
        return length;
    }
    public double getStart() {
        return start;
    }
    public boolean getDirection(){
        return direction;
    }
    public String getSequence() {
        return sequence;
    }
    public String getCigar(){
        return cigar;
    }
    public boolean getOverlap() {
        return overlap;
    }
}
