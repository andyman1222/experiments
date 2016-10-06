import java.math.BigDecimal;
class main{
    public static void main(String[] args){
        BigDecimal output = new BigDecimal(3);
        long currentValue = 2;
        while(true){
            output = output.add(new BigDecimal(4.0/(currentValue*(currentValue+1)*(currentValue+2))));
            currentValue+=2;
            System.out.println(output);
            output = output.subtract(new BigDecimal(4.0/(currentValue*(currentValue+1)*(currentValue+2))));
            currentValue+=2;
            System.out.println(output);
        }
    }
}