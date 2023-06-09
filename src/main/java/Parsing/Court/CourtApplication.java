package Parsing.Court;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;

import java.io.IOException;

@SpringBootApplication
@RestController
public class CourtApplication {
	@GetMapping("/")
	public String showForm() {
		return "<html><body>" +
				"<form action='/table' method='get'>" +
				"Дата (ДД.ММ.ГГГГ): <input type='text' name='date'><br><br>" +
				"<input type='submit' value='Отправить'>" +
				"</form></body></html>";
	}

	@GetMapping("/table")
	public String parseTable(@RequestParam String date) throws IOException {
		String url = "https://oblsud--wld.sudrf.ru/modules.php?name=sud_delo&srv_num=1&H_date=" + date;
		Document doc = Jsoup.connect(url).get();
		Elements tableRows = doc.select("table tr");
		StringBuilder sb = new StringBuilder("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body><script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script> <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.min.js\"></script><script src=\"/script.js\"></script><table id='Table'>");
		for (int i = 0; i < tableRows.size(); i++) {
			sb.append("<tr>");
			Elements rowColumns = tableRows.get(i).select("td");
			for (int j = 0; j < rowColumns.size(); j++) {
				Element link = rowColumns.get(j).selectFirst("a");
				if (!tableRows.get(i).text().contains("Режим работы суда")&&!tableRows.get(i).text().contains("Вывести список дел, назначенных на дату")&&!tableRows.get(i).text().contains("obl")&&!tableRows.get(i).text().contains("Поиск")) {
					if (link != null) {
						sb.append("<td><a href=\"").append(link.attr("href")).append("\">").append(link.text()).append("</a></td>");
					} else {
						sb.append("<td>").append(rowColumns.get(j).text()).append("</td>");
					}
				}
			}
			sb.append("</tr>");
		}
		sb.append("</table></body></html>");
		return sb.toString();
	}

	@GetMapping("/parse")
	public String parseUrl(@RequestParam String url) {
		try {
			url = "https://oblsud--wld.sudrf.ru" + url;
			Document doc = Jsoup.connect(url).get();
			Elements tableRows = doc.select("div#cont1 table tr");
			StringBuilder sb = new StringBuilder("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"></head><body>");
			int repetitions=0;
			sb.append("<h4>ДЕЛО</h4>");
			for(repetitions=0;repetitions<5;repetitions++) {
				sb.append("<table>");
				for (int i = 0; i < tableRows.size(); i++) {
					sb.append("<tr>");
					Elements rowColumns = tableRows.get(i).select("td");
					for (int j = 0; j < rowColumns.size(); j++) {
						Element link = rowColumns.get(j).selectFirst("a");
						if (!tableRows.get(i).text().contains("Режим работы суда") && !tableRows.get(i).text().contains("Вывести список дел, назначенных на дату") && !tableRows.get(i).text().contains("obl") && !tableRows.get(i).text().contains("Поиск")) {
							if (link != null) {
								sb.append("<td><a href=\"").append(link.attr("href")).append("\">").append(link.text()).append("</a></td>");
							} else {
								sb.append("<td>").append(rowColumns.get(j).text()).append("</td>");
							}
						}
					}
					sb.append("</tr>");
				}
				sb.append("</table><br>");
				switch (repetitions) {
					case 0:
						sb.append("<h4>РАССМОТРЕНИЕ В НИЖЕСТОЯЩЕМ СУДЕ</h4>");
						tableRows = doc.select("div#cont2 table tr");
						break;
					case 1:
						sb.append("<h4>СЛУШАНИЯ</h4>");
						tableRows = doc.select("div#cont3 table tr");
						break;
					case 2:
						sb.append("<h4>ЛИЦА</h4>");
						tableRows = doc.select("div#cont4 table tr");
						break;
					case 3:
						sb.append("<h4>СТОРОНЫ<h4>");
						tableRows = doc.select("div#cont5 table tr");
						break;
				}
			}
			sb.append("</body></html>");
			return sb.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
			return "Error occurred: " + e.getMessage();
		}
	}
	public static void main(String[] args) throws IOException {
		SpringApplication.run(CourtApplication.class, args);
	}

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}
