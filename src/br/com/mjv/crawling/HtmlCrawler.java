package br.com.mjv.crawling;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.FormAuthInfo;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class HtmlCrawler extends WebCrawler {

	private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

	// more code

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String urlString = url.getURL().toLowerCase();
		return !EXCLUSIONS.matcher(urlString).matches() && urlString.startsWith("https://stou.ifractal.com.br");
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String title = htmlParseData.getTitle();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("GOOD!!");
		}
	}

	public static void main(String[] args) throws Exception {
		
		File crawlStorage = new File("src/test/resources/crawler4j");
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
		
		String username        = "luiz.orta";
		String password        = "Rattic203040";
		String loginUrl        = "https://stou.ifractal.com.br/mjv/login.php";
		String usernameFormStr = "login";
		String passwordFormStr = "senha";
		
		AuthInfo authInfo = new FormAuthInfo( username,  password,  loginUrl,  usernameFormStr,  passwordFormStr);
		config.addAuthInfo(authInfo);

		int numCrawlers = 1;

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		//controller.addSeed("https://stou.ifractal.com.br/mjv/index.php#ponto_espelho");
		controller.addSeed("https://stou.ifractal.com.br/mjv/db/verifica_acesso.php");
		// controller.addSeed("https://www.baeldung.com/");

		CrawlController.WebCrawlerFactory<HtmlCrawler> factory = HtmlCrawler::new;

		controller.start(factory, numCrawlers);
	}
}