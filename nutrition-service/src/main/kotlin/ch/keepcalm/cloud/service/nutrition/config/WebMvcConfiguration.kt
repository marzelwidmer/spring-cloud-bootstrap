package ch.keepcalm.cloud.service.nutrition.config

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL])
internal class WebConfig : WebMvcConfigurer {
    companion object {
        fun extendMessageConverters(converters: List<HttpMessageConverter<*>>?) {
            for (converter in converters!!) {
                if (converter is MappingJackson2HttpMessageConverter) {
                    val mapper = converter.objectMapper
                    mapper.setFilterProvider(SimpleFilterProvider().addFilter("foodFilter", SimpleBeanPropertyFilter.serializeAll()))
                }
            }
        }
    }
 }

