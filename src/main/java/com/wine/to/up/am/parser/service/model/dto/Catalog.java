package com.wine.to.up.am.parser.service.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Catalog {

    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("brand")
    private List<CatalogProp> brands;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("color")
    private List<CatalogProp> colors;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("country")
    private List<CatalogProp> countries;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("grape_sort")
    private List<CatalogProp> grapes;
    @JsonDeserialize(using = CatalogPropDeserializer.class)
    @JsonProperty("sugar")
    private List<CatalogProp> sugars;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogProp {
        private String importId;
        private String value;
    }

    public static class CatalogPropDeserializer extends JsonDeserializer<List<CatalogProp>> {
        @Override
        public List<CatalogProp> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            List<CatalogProp> catalogProps = new ArrayList<>();

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Iterator<JsonNode> iterator = node.get("values").elements();

            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next();
                catalogProps.add(new CatalogProp(jsonNode.get("id").asText(), jsonNode.get("value").asText()));
            }
            return catalogProps;
        }
    }
}
