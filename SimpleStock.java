import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;


public class SimpleStocks {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public SimpleStocks(){
		
		//Create HashMap and fill it with fake data
		HashMap<String, Object[]> tempData = createFakeData();
		
		// I - calculate dividend yield
		System.out.println("I - Calculate dividend yield - "+calculateDividendYield(6000, 5.55, 100, true));
		
		// II - calculate P/E ratio
		System.out.println("II - Calculate P/E Ratio - "+calculaatePERatio(5, 500));
		
		// III - Record Trade
		Object[] temp = recordTrade(100, true);
		System.out.println("III - Record Trade - TimeStamp - "+temp[0]+" - Quantity - "+temp[1]+" - isBought? - "+temp[2] );
		
		//IV Calculate Stock Price - passing in fake trade record
		double tempPrice = calculateStockPrice(tempData , 100);
		System.out.println("IV - Calculate Stock Price - "+tempPrice);
		
		// V - Calculate All Share Index
		double tempIndex = calculateAllShareIndex(tempData);
		System.out.println("V - Calculate All Share Index - "+tempIndex);
		
	}
	
	//================================================================================================================
	
	/**
	 * Method takes in fake data set multiplies all prices and
	 * uses fractional power equal to 1/mapSize 
	 * @param dataMap
	 * @return
	 */
	private double calculateAllShareIndex( HashMap<String, Object[]> dataMap){
		double allShareIndex = 0.0;
		
		for(Entry e : dataMap.entrySet()){
			Object[] tempArr = (Object[]) e.getValue();
			double currentPrice = Double.parseDouble( tempArr[1].toString() );
			
			allShareIndex = allShareIndex * currentPrice;
		}
		allShareIndex = Math.pow(allShareIndex, 1/ dataMap.size());
		
		return allShareIndex;
	}
	
	//================================================================================================================
	
	/**
	 * Method creates fake data for the application 
	 * Fills HashMap with Stocks according to this standard :
	 * key = date
	 * Every key relates to object array that consist of :
	 * [0] - name
	 * [1] - price
	 * [2] - quantity
	 * @return
	 */
	private HashMap<String, Object[]> createFakeData(){
		
		System.out.println("Generate fake data - NAME - PRICE - QUANTITY");
		
		HashMap<String, Object[]> tempData = new HashMap<String, Object[]>();
		
		Calendar calendar = Calendar.getInstance();
		long t = calendar.getTimeInMillis();
		//1 minute = 60_000 milliseconds
		
		for(int i=0; i <= 20; ++i){
			
			tempData.put( sdf.format(new Date(t - (i * 60000))) , new Object[]{"AA"+i, 5+i, 100+i});
			
		}
		
		//REDISPLAY
		for(Entry e : tempData.entrySet()){
			System.out.print( e.getKey() );
			Object[] tempArr = (Object[]) e.getValue();
			for(int i=0;i<tempArr.length; ++i){
				System.out.print(" - "+tempArr[i]);
			}
			System.out.println("");
		}
		
		return tempData;
	}
	
	//================================================================================================================
	
	public static void main(String[] args){
		
		new SimpleStocks();
		
	}
	
	//================================================================================================================
	
	
	/**
	 * This method calculates Dividend Yield.
	 * Depending on passed in values it will either use 
	 * common or preferred formula
	 * 
	 * @param dividend - double/numeric representation of dividend value
	 * @param price - double/numeric representation of price
	 * @param parValue - double/numeric representation of stated value
	 * @param isCommon - boolean variable to choose formula/type
	 * @return
	 */
	private double calculateDividendYield(double dividend, double price, double parValue, boolean isCommon){
		
		double dividendYield = 0.0;
		
		if(isCommon){
			dividendYield = dividend / price ;
		} else {
			dividendYield = dividend * parValue / price;
		}
		
		return dividendYield;
	}
	
	//================================================================================================================
	
	/**
	 * Method calculates P/E ratio using
	 * passed in values
	 * 
	 * @param price - double/numeric representation of price
	 * @param dividend - double/numeric representation of dividend value
	 * @return
	 */
	private double calculaatePERatio(double price, double dividend){
		
		double pERatio = 0.0;
		
		pERatio = price / dividend;
		
		return pERatio;
	}
	
	//================================================================================================================
	
	/**
	 * Method assembles object array of particular trade
	 * properties / values
	 * 
	 * @param quantity - double/numeric representation of quantity
	 * @param isBought - boolean indicator for buy/sell
	 * @return
	 */
	private Object[] recordTrade(double quantity, boolean isBought){
		
		//String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Date d = getCurrentDateTime();
		String timeStamp = sdf.format(d);

		Object[] rowData = new Object[]{
										timeStamp,
										quantity,
										isBought //true for purchases and false for sales
										};
		
		return rowData;
	}
	
	//================================================================================================================
	
	/**
	 * Method below calculates stock price 
	 * using average price for last 15 minutes
	 * 
	 * @param tradePrice
	 * @param quantity
	 * @return
	 */
	private double calculateStockPrice( HashMap<String, Object[]> trades, double passedInQuantity){
		
		String currentTimeStamp = null;
		String currentName = null; //not in use / display purpose
		double currentPrice = 0.0;
		double sumOfPrices = 0.0;
		double sumOfQuantitys = 0.0;
		double quantity = 0.0; // not in use / display purpose
		
		//assuming that passed in map will have timestamps as keys and
		//related data is stored in array of objects
		for(Entry e : trades.entrySet()){
			currentTimeStamp =  e.getKey().toString();
			
			//if timeStamp out of range of 15 mins continue next iteration
			if(! isDateInRange(currentTimeStamp) ){
				continue;
			}
			
			Object[] rowData = (Object[]) e.getValue();
			
				currentName = rowData[0].toString();
				currentPrice = Double.parseDouble( rowData[1].toString() );
				quantity = Double.parseDouble( rowData[2].toString() );
				
				sumOfPrices = sumOfPrices + currentPrice;
				sumOfQuantitys = sumOfQuantitys + quantity;
		}
		
		//double avaragePrice = sumOfPrices / trades.size();
		double stockPrice =  (sumOfPrices * passedInQuantity ) / sumOfQuantitys ;
		
		return stockPrice;
	}
	
	//================================================================================================================
	//=========================================== DATE OPS ===========================================================
	//================================================================================================================
	
	/**
	 * Method checks if passed in string
	 * with date is in range , means -
	 * between now and 15 minutes ago.
	 * 
	 * @param inputDateStr
	 * @return
	 */
	private boolean isDateInRange(String inputDateStr){
		
		Date now = getCurrentDateTime();
		//System.out.println(sdf.format(now));
		
		Date nowMinus15 = getDateMinus15Minutes();
		//System.out.println(sdf.format(nowMinus15));
		
		try {
			Date newDate = sdf.parse(inputDateStr);
			//System.out.println(sdf.format(newDate));
			if(newDate.before(now) && newDate.after(nowMinus15)){
				//System.out.println("Date in range");
				return true;
			} else {
				//System.out.println("Date not in range");
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private Date getCurrentDateTime(){
		Calendar calendar = Calendar.getInstance();
		long t = calendar.getTimeInMillis();
		Date date = new Date(t);
		
		return date;

	}
	
	private Date getDateMinus15Minutes(){
		
		Calendar calendar = Calendar.getInstance();
		long t = calendar.getTimeInMillis();
		//15 minutes = 900_000 milliseconds
		Date date = new Date(t - 900000 );
		
		return date;
	}
	
	
}
