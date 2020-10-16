package com.wine.to.up.am.parser.service.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
@Setter
@ApiModel(description = "All details about the Dictionary (DTO)")
public class Dictionary {

    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("brand")
    private Map<String, CatalogProp> brands;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("color")
    private Map<String, CatalogProp> colors;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("country")
    private Map<String, CatalogProp> countries;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("grape_sort")
    private Map<String, CatalogProp> grapes;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("sugar")
    private Map<String, CatalogProp> sugars;

    public Dictionary() {
        brands = new HashMap<>();
        colors = new HashMap<>();
        countries = new HashMap<>();
        grapes = new HashMap<>();
        sugars = new HashMap<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogProp {
        private String importId;
        private String value;
    }

    public static class CatalogPropDeserializer extends JsonDeserializer<Map<String, CatalogProp>> {
        @Override
        public Map<String, CatalogProp> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            Map<String, CatalogProp> catalogProps = new HashMap<>();

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Iterator<JsonNode> iterator = node.get("values").elements();

            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next();
                catalogProps.put(jsonNode.get("id").asText(), new CatalogProp(jsonNode.get("id").asText(), jsonNode.get("value").asText()));
            }
            return catalogProps;
        }
    }
}
