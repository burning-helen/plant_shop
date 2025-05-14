package com.example.plant_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс для запуска Spring Boot приложения.
 * <p>
 * Это точка входа в приложение, которая запускает Spring Boot и настраивает все необходимые компоненты.
 * </p>
 */
@SpringBootApplication
public class PlantShopApplication {

	/**
	 * Метод для запуска приложения.
	 * <p>
	 * Этот метод вызывает SpringApplication.run(), который запускает приложение, выполняя настройку контекста и
	 * загружая все необходимые компоненты для работы приложения.
	 * </p>
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(PlantShopApplication.class, args);
	}

}
