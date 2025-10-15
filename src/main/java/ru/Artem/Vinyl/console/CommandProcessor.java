package ru.Artem.Vinyl.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.service.VinylRecordService;

@Component
public class CommandProcessor {

    private final VinylRecordService recordService;

    @Autowired
    public CommandProcessor(VinylRecordService recordService) {
        this.recordService = recordService;
    }

    public void processCommand(String input) {
        String[] cmd = input.split("\\s+", 2);
        String action = cmd[0].toLowerCase();

        try {
            switch (action) {
                case "create" -> {
                    // Ожидаем данные: id artist title year country style
                    String[] params = cmd[1].split("\\s+");
                    if (params.length < 6) {
                        System.out.println("Недостаточно аргументов для create.");
                        break;
                    }
                    Long id = Long.valueOf(params[0]);
                    String artist = params[1];
                    String title = params[2];
                    int year = Integer.parseInt(params[3]);
                    String country = params[4];
                    String style = params[5];
                    recordService.addRecord(id, artist, title, year, country, style);
                    System.out.println("Запись успешно добавлена.");
                }
                case "delete" -> {
                    Long id = Long.valueOf(cmd[1]);
                    recordService.deleteRecord(id);
                    System.out.println("Запись с id=" + id + " удалена.");
                }
                case "get" -> {
                    Long id = Long.valueOf(cmd[1]);
                    VinylRecord record = recordService.getRecord(id);
                    if (record != null) {
                        System.out.println("Найдена запись: " +
                                record.getArtist() + " - \"" + record.getTitle() + "\" (" +
                                record.getYear() + ", " + record.getCountry() + ", " + record.getStyle() + ")");
                    } else {
                        System.out.println("Запись с id=" + id + " не найдена.");
                    }
                }
                case "update" -> {
                    // Ожидаем все поля аналогично create
                    String[] params = cmd[1].split("\\s+");
                    if (params.length < 6) {
                        System.out.println("Недостаточно аргументов для update.");
                        break;
                    }
                    Long id = Long.valueOf(params[0]);
                    String artist = params[1];
                    String title = params[2];
                    int year = Integer.parseInt(params[3]);
                    String country = params[4];
                    String style = params[5];
                    recordService.updateRecord(id, artist, title, year, country, style);
                    System.out.println("Запись с id=" + id + " обновлена.");
                }
                case "list" -> {
                    System.out.println("Все записи:");
                    for (VinylRecord r : recordService.listAllRecords()) {
                        System.out.println(r.getId() + ": " + r.getArtist() + " - \"" + r.getTitle() + "\" (" +
                                r.getYear() + ", " + r.getCountry() + ", " + r.getStyle() + ")");
                    }
                }
                default -> System.out.println("Неизвестная команда: " + action);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}