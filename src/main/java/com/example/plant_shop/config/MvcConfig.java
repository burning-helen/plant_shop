package com.example.plant_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация для настройки обработки ресурсов в приложении.
 * <p>
 * Этот класс реализует интерфейс {@link WebMvcConfigurer} и используется для настройки обработки
 * статических файлов и ресурсов в приложении, например, изображений и других файлов.
 * </p>
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Метод для добавления обработчиков для статических ресурсов.
     * <p>
     * Настроен обработчик для путей, начинающихся с "/uploads/**", чтобы они указывали на
     * директорию "uploads/" на сервере файлов.
     * </p>
     *
     * @param registry реестр для настройки обработчиков ресурсов.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
