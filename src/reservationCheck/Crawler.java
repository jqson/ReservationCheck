package reservationCheck;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Crawler {
	//private final List<String> dates;

	private final String domain;

	private final String UrlPrefix;

	public Crawler(String domain, String param/*, List<String> dates*/) {
		this.domain = domain;
		this.UrlPrefix = domain + param;

		//this.dates = dates;
	}

	public boolean crawl(String urlString, String date) {
		boolean found = false;
		String pageUrl = UrlPrefix + urlString;

		String nextUrlString = pageUrl;
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(nextUrlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
		    connection.setRequestProperty("Content-Type", "text/plain");
		    connection.setRequestProperty("charset", "utf-8");

			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to: " + url);
			if (responseCode != 200) {
				System.out.println("Response Code : " + responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
			}
			in.close();

			String htmlPage = response.toString();

			if (findAvailable(htmlPage, date)) {
				found = true;
				Toolkit.getDefaultToolkit().beep();
			}
			//nextPageUrl = findNextPage(htmlPage);
		} catch (Exception e) {
			e.printStackTrace();
			return found;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		System.out.println("Done.\n");
		return found;
	}

	private boolean findAvailable(String htmlPage, String date) {
		boolean found = false;

		int startIdx = htmlPage.indexOf("id=\"csitecalendar\"");
		if (startIdx == -1) {
			System.out.println("ERROR: No calendar found");
			return false;
		}

		startIdx = htmlPage.indexOf("<tbody>", startIdx);

		int endIdx = htmlPage.indexOf("</tbody>", startIdx);

		String table = htmlPage.substring(startIdx, endIdx);

		//System.out.println(table);
		int hrefIdx = table.indexOf("status a");
		while (hrefIdx != -1) {
			hrefIdx = table.indexOf("<a href=\'", hrefIdx) + "<a href=\'".length();
			int endHref = table.indexOf("\'", hrefIdx);
			String link = table.substring(hrefIdx, endHref).replace("amp;", "");

			if (link.contains(date)) {
				found = true;
				System.out.println(domain + link);
			}

			hrefIdx = table.indexOf("status a", hrefIdx);
		}

		return found;
	}

	// Deprecate because the site uses Cookie to track session ID
	private String findNextPage(String htmlPage) {
		int preTagIdx = htmlPage.lastIndexOf("<", htmlPage.indexOf(">Next <"));
		int startIdx = htmlPage.indexOf("<a href=\'", preTagIdx);

		if (preTagIdx != startIdx) {
			return null;
		}

		startIdx += "<a href=\'".length();
		int endIdx = htmlPage.indexOf("\'", startIdx);
		System.out.println(domain + htmlPage.substring(startIdx, endIdx));

		return domain + htmlPage.substring(startIdx, endIdx);
	}
}
