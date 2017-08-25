package reservationCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		String domain = "https://www.recreation.gov";
		String params = "/campsiteCalendar.do?page=calendar&contractCode=NRSO";
		int tryTimes = 500;

		List<String> dates = new ArrayList<String>();
		dates.add("9/2/2017");
		//dates.add("9/4/2017");

		Map<String, String> sites = new HashMap<String, String>();
		sites.put("MANZANITA LAKE", "74045");
		sites.put("SUMMIT LAKE NORTH", "74047");
		sites.put("SUMMIT LAKE SOUTH", "74046");

		//sites.put("MANZANITA LAKE", "&parkId=74045&calarvdate=08/01/2017&sitepage=true&startIdx=0");

		Crawler crawler = new Crawler(domain, params);

		boolean found = false;
		for (int i = 0; i < tryTimes; i++) {
			System.out.println("Repeat Count: " + i);
			for (Map.Entry<String, String> site : sites.entrySet()) {
				System.out.println(site.getKey());
				String parkIdParam = "&parkId=" + site.getValue();
				for (String date : dates) {
					String parkIdDateParam = parkIdParam + "&calarvdate=" + date + "&sitepage=true&startIdx=0";
					if (crawler.crawl(parkIdDateParam, date)) {
						found = true;
					}
					try {
					    Thread.sleep(1000);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
			}
			if (found) {
				return;
			}
			try {
			    Thread.sleep(10000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}
}
