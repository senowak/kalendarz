package com.example.demo;


import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
public class Controller {

    @RequestMapping("/kalendarz")
    public Object calendar(@RequestParam(defaultValue = "2020") int year, @RequestParam(defaultValue = "01") int month) {
        String CALENDAR_URL = "http://weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php";
        String YEAR_PARAM = "?rok=";
        String MONTH_PARAM = "&miesiac=";
        String HTML_SELECTOR = "td";
        String CLASS_NAME = "active";
        String MATCHER = "a class=\"active\" href=\"javascript:void();\"";
        String REGEX = "\\n";
        String FILENAME = "calendar.ics";
        String MEDIA_TYPE = "text/calendar";
        int INDEX = 0;

        String url = CALENDAR_URL + YEAR_PARAM + year + MONTH_PARAM + month;
        List<Event> events = new ArrayList<>();
        ICalendar calendar = new ICalendar();
        LocalDate localDate = LocalDate.now();
        Month actualMonth = localDate.getMonth();

        try {
            Document HtmlDocument = Jsoup.connect(url).get();
            for (Element tdElement : HtmlDocument.select(HTML_SELECTOR)) {
                for (Element activeClassElement : tdElement.getElementsByClass(CLASS_NAME)) {
                    if (activeClassElement.toString().contains(MATCHER)) {
                        String[] splitted = activeClassElement.toString().split(REGEX);
                        String day = splitted[INDEX].substring(splitted[INDEX].lastIndexOf("\">") + 2, splitted[INDEX].lastIndexOf("<"));
                        for (Element pElement : activeClassElement.getElementsByTag("p")) {
                            String pString = pElement.toString();
                            String event = pString.substring(pString.indexOf(">") + 1, pString.lastIndexOf("<"));
                            events.add(new Event(day, event));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Event calendarEvent : events) {
            VEvent event = new VEvent();
            event.setSummary(calendarEvent.getNazwa());
            Date eventDate = new GregorianCalendar(localDate.getYear(), actualMonth.getValue(), Integer.parseInt(calendarEvent.getDzien()) + 1).getTime();
            event.setDateStart(eventDate);
            event.setDateEnd(eventDate);
            calendar.addEvent(event);
        }

        File calendarFile = new File(FILENAME);
        try {
            Biweekly.write(calendar).go(calendarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Resource fileSystemResource = new FileSystemResource(calendarFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MEDIA_TYPE))
                .body(fileSystemResource);
    }
}