package ru.Artem.Vinyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.service.VinylRecordService;

/**
 * Контроллер для отображения HTML страниц с виниловыми пластинками.
 *
 * Страницы доступны по базовому пути: /vinyl/view
 */
@Controller
@RequestMapping("/vinyl/view")
public class VinylRecordViewController {

    private final VinylRecordService vinylRecordService;

    @Autowired
    public VinylRecordViewController(VinylRecordService vinylRecordService) {
        this.vinylRecordService = vinylRecordService;
    }

    /**
     * Отображает список всех виниловых пластинок в виде HTML таблицы.
     *
     * Страница доступна по адресу: /vinyl/view/list
     *
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для рендеринга
     */
    @GetMapping("/list")
    public String vinylRecordListView(Model model) {
        // получаем все пластинки из БД
        Iterable<VinylRecord> records = vinylRecordService.findAll();

        // добавляем их в модель под именем "records"
        model.addAttribute("records", records);

        // возвращаем имя шаблона (файл vinylRecordList.html)
        return "vinylRecordList";
    }
}