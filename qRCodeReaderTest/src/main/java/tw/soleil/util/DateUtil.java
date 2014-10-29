package tw.soleil.util;

public class DateUtil {
	public static String getEDateWithSep(String strCDate, String strSep) {
		return getDateWithSep(getEDate(strCDate), strSep);
	}

	public static String getDateWithSep(String strInit, String strSep) {
		StringBuffer sb = null;

		if(strInit == null || strInit.trim().equals("")){
			return "";
		}

		try {

			int dateLen = strInit.length();

			if(dateLen > 5){ 

				String strFirst = strInit.substring(0, dateLen - 4);
				String strSecond = strInit.substring(dateLen - 4, dateLen - 2);
				String strThird = strInit.substring(dateLen - 2);

				sb = new StringBuffer(strFirst);

				sb.append(strSep).append(strSecond).append(strSep).append(strThird);

			} else if (dateLen == 5 || dateLen == 4){
				sb = new StringBuffer(strInit.substring(0, dateLen - 2)).append(strSep)
				.append(strInit.substring(dateLen - 2));

			} else {
				return strInit;
			}

		}
		catch (Exception e) {
			return strInit;
		}

		return sb.toString();
	}
	
	/** REFINE
	 * 由民國日期字串取得西元日期字串
	 * @param strCDate: yyyMMdd
	 * @return yyyyMMdd
	 */
	public static String getEDate(String strCDate) {
		StringBuffer sbEDate = new StringBuffer();

		if (strCDate != null) {
			int len = strCDate.length();
			String year = strCDate.substring(0, len - 4);
			String month = strCDate.substring(len - 4, len - 2);
			String day = strCDate.substring(len - 2);

			if (year.length() < 4) {
				year = String.valueOf(Integer.valueOf(year).intValue() + 1911);
			} else {
				year = String.valueOf(Integer.valueOf(year).intValue());
			}

			sbEDate.append(year);
			sbEDate.append(month);
			sbEDate.append(day);

		}

		return sbEDate.toString();
	}
}
