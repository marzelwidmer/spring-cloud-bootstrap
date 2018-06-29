package ch.keepcalm.cloud.service.nutrition

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.hateoas.ResourceSupport
import java.util.*

class HalResource : ResourceSupport() {

    val embedded = HashMap<String, Any>()

    val embeddedResources: Map<String, Any>
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonProperty("_embedded")
        get() = embedded

    fun embedResource(relationship: String, resource: ResourceSupport) {
        embedded[relationship] = resource

    }

    fun embedResource(relationship: String, resourceList: List<*>) {
        embedded[relationship] = resourceList
    }
}