package com.saslab.adapter;

import com.saslab.model.Event;
import com.saslab.model.Menu;
import com.saslab.Recipe;

/**
 * Adapter pattern per integrare sistemi esterni
 * Implementa pattern Adapter (GoF) per compatibilità con diversi formati di esportazione
 */
public class ExternalSystemAdapter {
    
    /**
     * Interface per sistemi esterni di gestione eventi
     */
    public interface ExternalEventSystem {
        void exportEvent(String eventData);
        String getEventFormat();
    }
    
    /**
     * Interface per sistemi esterni di gestione ricette
     */
    public interface ExternalRecipeSystem {
        void exportRecipe(String recipeData);
        String getRecipeFormat();
    }
    
    /**
     * Adapter per sistema di catering esterno (formato JSON)
     */
    public static class JSONCateringSystemAdapter implements ExternalEventSystem, ExternalRecipeSystem {
        
        @Override
        public void exportEvent(String eventData) {
            // Simula esportazione in formato JSON
            System.out.println("Esportando evento in formato JSON: " + eventData);
        }
        
        @Override
        public void exportRecipe(String recipeData) {
            // Simula esportazione ricetta in formato JSON
            System.out.println("Esportando ricetta in formato JSON: " + recipeData);
        }
        
        @Override
        public String getEventFormat() {
            return "JSON";
        }
        
        @Override
        public String getRecipeFormat() {
            return "JSON";
        }
        
        /**
         * Converte un Event del nostro sistema in formato JSON
         */
        public String convertEventToJSON(Event event) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"id\": \"").append(event.getId()).append("\",\n");
            json.append("  \"name\": \"").append(event.getName()).append("\",\n");
            json.append("  \"location\": \"").append(event.getLocation()).append("\",\n");
            json.append("  \"startDate\": \"").append(event.getStartDate()).append("\",\n");
            json.append("  \"endDate\": \"").append(event.getEndDate()).append("\",\n");
            json.append("  \"state\": \"").append(event.getState()).append("\",\n");
            json.append("  \"expectedGuests\": ").append(event.getExpectedGuests()).append(",\n");
            json.append("  \"services\": [\n");
            
            for (int i = 0; i < event.getServices().size(); i++) {
                var service = event.getServices().get(i);
                json.append("    {\n");
                json.append("      \"id\": \"").append(service.getId()).append("\",\n");
                json.append("      \"type\": \"").append(service.getType()).append("\",\n");
                json.append("      \"date\": \"").append(service.getDate()).append("\",\n");
                json.append("      \"startTime\": \"").append(service.getStartTime()).append("\",\n");
                json.append("      \"endTime\": \"").append(service.getEndTime()).append("\"\n");
                json.append("    }");
                if (i < event.getServices().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            return json.toString();
        }
        
        /**
         * Converte una Recipe del nostro sistema in formato JSON
         */
        public String convertRecipeToJSON(Recipe recipe) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"id\": \"").append(recipe.getId()).append("\",\n");
            json.append("  \"name\": \"").append(recipe.getName()).append("\",\n");
            json.append("  \"description\": \"").append(recipe.getDescription() != null ? recipe.getDescription() : "").append("\",\n");
            json.append("  \"preparationTime\": ").append(recipe.getPreparationTime()).append(",\n");
            json.append("  \"basePortions\": ").append(recipe.getBasePortions()).append(",\n");
            json.append("  \"state\": \"").append(recipe.getState()).append("\",\n");
            json.append("  \"ingredients\": [\n");
            
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                var ingredient = recipe.getIngredients().get(i);
                json.append("    {\n");
                json.append("      \"name\": \"").append(ingredient.getName()).append("\",\n");
                json.append("      \"quantity\": ").append(ingredient.getQuantity()).append(",\n");
                json.append("      \"unit\": \"").append(ingredient.getUnit()).append("\"\n");
                json.append("    }");
                if (i < recipe.getIngredients().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ],\n");
            json.append("  \"tags\": [");
            for (int i = 0; i < recipe.getTags().size(); i++) {
                json.append("\"").append(recipe.getTags().get(i)).append("\"");
                if (i < recipe.getTags().size() - 1) {
                    json.append(", ");
                }
            }
            json.append("]\n");
            json.append("}");
            return json.toString();
        }
    }
    
    /**
     * Adapter per sistema legacy (formato XML)
     */
    public static class XMLLegacySystemAdapter implements ExternalEventSystem, ExternalRecipeSystem {
        
        @Override
        public void exportEvent(String eventData) {
            // Simula esportazione in formato XML legacy
            System.out.println("Esportando evento in formato XML legacy: " + eventData);
        }
        
        @Override
        public void exportRecipe(String recipeData) {
            // Simula esportazione ricetta in formato XML legacy
            System.out.println("Esportando ricetta in formato XML legacy: " + recipeData);
        }
        
        @Override
        public String getEventFormat() {
            return "XML_LEGACY";
        }
        
        @Override
        public String getRecipeFormat() {
            return "XML_LEGACY";
        }
        
        /**
         * Converte un Event del nostro sistema in formato XML legacy
         */
        public String convertEventToXML(Event event) {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<event>\n");
            xml.append("  <id>").append(event.getId()).append("</id>\n");
            xml.append("  <name><![CDATA[").append(event.getName()).append("]]></name>\n");
            xml.append("  <location><![CDATA[").append(event.getLocation()).append("]]></location>\n");
            xml.append("  <startDate>").append(event.getStartDate()).append("</startDate>\n");
            xml.append("  <endDate>").append(event.getEndDate()).append("</endDate>\n");
            xml.append("  <state>").append(event.getState()).append("</state>\n");
            xml.append("  <expectedGuests>").append(event.getExpectedGuests()).append("</expectedGuests>\n");
            xml.append("  <services>\n");
            
            for (var service : event.getServices()) {
                xml.append("    <service>\n");
                xml.append("      <id>").append(service.getId()).append("</id>\n");
                xml.append("      <type>").append(service.getType()).append("</type>\n");
                xml.append("      <date>").append(service.getDate()).append("</date>\n");
                xml.append("      <startTime>").append(service.getStartTime()).append("</startTime>\n");
                xml.append("      <endTime>").append(service.getEndTime()).append("</endTime>\n");
                xml.append("    </service>\n");
            }
            
            xml.append("  </services>\n");
            xml.append("</event>");
            return xml.toString();
        }
        
        /**
         * Converte una Recipe del nostro sistema in formato XML legacy
         */
        public String convertRecipeToXML(Recipe recipe) {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<recipe>\n");
            xml.append("  <id>").append(recipe.getId()).append("</id>\n");
            xml.append("  <name><![CDATA[").append(recipe.getName()).append("]]></name>\n");
            xml.append("  <description><![CDATA[").append(recipe.getDescription() != null ? recipe.getDescription() : "").append("]]></description>\n");
            xml.append("  <preparationTime>").append(recipe.getPreparationTime()).append("</preparationTime>\n");
            xml.append("  <basePortions>").append(recipe.getBasePortions()).append("</basePortions>\n");
            xml.append("  <state>").append(recipe.getState()).append("</state>\n");
            xml.append("  <ingredients>\n");
            
            for (var ingredient : recipe.getIngredients()) {
                xml.append("    <ingredient>\n");
                xml.append("      <name><![CDATA[").append(ingredient.getName()).append("]]></name>\n");
                xml.append("      <quantity>").append(ingredient.getQuantity()).append("</quantity>\n");
                xml.append("      <unit><![CDATA[").append(ingredient.getUnit()).append("]]></unit>\n");
                xml.append("    </ingredient>\n");
            }
            
            xml.append("  </ingredients>\n");
            xml.append("  <tags>\n");
            for (String tag : recipe.getTags()) {
                xml.append("    <tag><![CDATA[").append(tag).append("]]></tag>\n");
            }
            xml.append("  </tags>\n");
            xml.append("</recipe>");
            return xml.toString();
        }
    }
    
    /**
     * Factory per creare gli adapter appropriati
     * Implementa pattern Abstract Factory (GoF)
     */
    public static class AdapterFactory {
        
        public static ExternalEventSystem createEventAdapter(String format) {
            switch (format.toUpperCase()) {
                case "JSON":
                    return new JSONCateringSystemAdapter();
                case "XML":
                case "XML_LEGACY":
                    return new XMLLegacySystemAdapter();
                default:
                    throw new IllegalArgumentException("Formato non supportato: " + format);
            }
        }
        
        public static ExternalRecipeSystem createRecipeAdapter(String format) {
            switch (format.toUpperCase()) {
                case "JSON":
                    return new JSONCateringSystemAdapter();
                case "XML":
                case "XML_LEGACY":
                    return new XMLLegacySystemAdapter();
                default:
                    throw new IllegalArgumentException("Formato non supportato: " + format);
            }
        }
    }
    
    /**
     * Facade per semplificare l'uso degli adapter
     * Implementa pattern Facade (GoF)
     */
    public static class ExportFacade {
        
        /**
         * Esporta un evento nel formato specificato
         */
        public static void exportEvent(Event event, String format) {
            ExternalEventSystem adapter = AdapterFactory.createEventAdapter(format);
            
            String eventData;
            if (adapter instanceof JSONCateringSystemAdapter) {
                eventData = ((JSONCateringSystemAdapter) adapter).convertEventToJSON(event);
            } else if (adapter instanceof XMLLegacySystemAdapter) {
                eventData = ((XMLLegacySystemAdapter) adapter).convertEventToXML(event);
            } else {
                eventData = event.toString();
            }
            
            adapter.exportEvent(eventData);
        }
        
        /**
         * Esporta una ricetta nel formato specificato
         */
        public static void exportRecipe(Recipe recipe, String format) {
            ExternalRecipeSystem adapter = AdapterFactory.createRecipeAdapter(format);
            
            String recipeData;
            if (adapter instanceof JSONCateringSystemAdapter) {
                recipeData = ((JSONCateringSystemAdapter) adapter).convertRecipeToJSON(recipe);
            } else if (adapter instanceof XMLLegacySystemAdapter) {
                recipeData = ((XMLLegacySystemAdapter) adapter).convertRecipeToXML(recipe);
            } else {
                recipeData = recipe.toString();
            }
            
            adapter.exportRecipe(recipeData);
        }
        
        /**
         * Esporta un menù nel formato specificato
         */
        public static void exportMenu(Menu menu, String format) {
            // Per semplicità, esportiamo il menù come stringa formattata
            String menuData = formatMenuForExport(menu);
            
            ExternalEventSystem adapter = AdapterFactory.createEventAdapter(format);
            adapter.exportEvent(menuData);
        }
        
        private static String formatMenuForExport(Menu menu) {
            StringBuilder data = new StringBuilder();
            data.append("MENU: ").append(menu.getName()).append("\n");
            data.append("STATO: ").append(menu.getState().getDisplayName()).append("\n");
            data.append("CHEF: ").append(menu.getChefId()).append("\n");
            
            if (menu.getDescription() != null) {
                data.append("DESCRIZIONE: ").append(menu.getDescription()).append("\n");
            }
            
            data.append("\nSEZIONI:\n");
            for (var section : menu.getSections()) {
                data.append("- ").append(section.getTitle()).append("\n");
                for (var item : section.getMenuItems()) {
                    data.append("  * ").append(item.getDisplayName()).append("\n");
                }
            }
            
            return data.toString();
        }
    }
}