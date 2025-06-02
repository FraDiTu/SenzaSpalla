package com.saslab.visitor;

import com.saslab.model.Menu;
import com.saslab.model.MenuSection;
import com.saslab.model.MenuItem;
import com.saslab.Recipe;
import com.saslab.RecipeBook;

/**
 * Concrete Visitor per esportare menù in diversi formati
 * Genera rappresentazioni testuali formattate dei menù
 */
public class MenuExportVisitor implements MenuVisitor {
    
    public enum ExportFormat {
        PLAIN_TEXT, HTML, MARKDOWN, PDF_READY
    }
    
    private StringBuilder content;
    private ExportFormat format;
    private RecipeBook recipeBook = RecipeBook.getInstance();
    private int sectionCounter = 0;
    
    public MenuExportVisitor(ExportFormat format) {
        this.format = format;
        this.content = new StringBuilder();
    }
    
    @Override
    public void visitMenu(Menu menu) {
        sectionCounter = 0;
        content.setLength(0); // Clear previous content
        
        switch (format) {
            case HTML:
                content.append("<!DOCTYPE html>\n<html>\n<head>\n");
                content.append("<title>").append(menu.getName()).append("</title>\n");
                content.append("<style>\n");
                content.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
                content.append("h1 { text-align: center; color: #333; }\n");
                content.append("h2 { color: #666; border-bottom: 2px solid #eee; padding-bottom: 10px; }\n");
                content.append(".menu-item { margin: 15px 0; }\n");
                content.append(".description { font-style: italic; color: #666; }\n");
                content.append("</style>\n</head>\n<body>\n");
                content.append("<h1>").append(menu.getName()).append("</h1>\n");
                if (menu.getDescription() != null) {
                    content.append("<p class='description'>").append(menu.getDescription()).append("</p>\n");
                }
                break;
                
            case MARKDOWN:
                content.append("# ").append(menu.getName()).append("\n\n");
                if (menu.getDescription() != null) {
                    content.append("*").append(menu.getDescription()).append("*\n\n");
                }
                content.append("---\n\n");
                break;
                
            case PDF_READY:
                content.append("\\documentclass{article}\n");
                content.append("\\usepackage[utf8]{inputenc}\n");
                content.append("\\usepackage[italian]{babel}\n");
                content.append("\\begin{document}\n");
                content.append("\\title{").append(escapeLatex(menu.getName())).append("}\n");
                content.append("\\maketitle\n");
                if (menu.getDescription() != null) {
                    content.append("\\textit{").append(escapeLatex(menu.getDescription())).append("}\n\n");
                }
                break;
                
            default: // PLAIN_TEXT
                content.append("=== ").append(menu.getName().toUpperCase()).append(" ===\n\n");
                if (menu.getDescription() != null) {
                    content.append(menu.getDescription()).append("\n\n");
                }
                content.append("=====================================\n\n");
        }
        
        // Visita le sezioni
        for (MenuSection section : menu.getSections()) {
            visitMenuSection(section);
        }
        
        // Chiudi il documento se necessario
        if (format == ExportFormat.HTML) {
            content.append("</body>\n</html>");
        } else if (format == ExportFormat.PDF_READY) {
            content.append("\\end{document}");
        }
    }
    
    @Override
    public void visitMenuSection(MenuSection section) {
        sectionCounter++;
        
        switch (format) {
            case HTML:
                content.append("<h2>").append(section.getTitle()).append("</h2>\n");
                content.append("<div class='section'>\n");
                break;
                
            case MARKDOWN:
                content.append("## ").append(section.getTitle()).append("\n\n");
                break;
                
            case PDF_READY:
                content.append("\\section{").append(escapeLatex(section.getTitle())).append("}\n\n");
                break;
                
            default: // PLAIN_TEXT
                content.append("--- ").append(section.getTitle().toUpperCase()).append(" ---\n\n");
        }
        
        // Visita gli item della sezione
        for (MenuItem item : section.getMenuItems()) {
            visitMenuItem(item);
        }
        
        if (format == ExportFormat.HTML) {
            content.append("</div>\n\n");
        } else {
            content.append("\n");
        }
    }
    
    @Override
    public void visitMenuItem(MenuItem item) {
        Recipe recipe = recipeBook.getRecipe(item.getRecipeId());
        String itemName = item.getDisplayName();
        String description = recipe != null && recipe.getDescription() != null ? 
                           recipe.getDescription() : "";
        
        switch (format) {
            case HTML:
                content.append("<div class='menu-item'>\n");
                content.append("<strong>").append(itemName).append("</strong>");
                if (item.hasPrice()) {
                    content.append(" - €").append(String.format("%.2f", item.getPrice()));
                }
                if (!description.isEmpty()) {
                    content.append("<br><span class='description'>").append(description).append("</span>");
                }
                content.append("</div>\n");
                break;
                
            case MARKDOWN:
                content.append("- **").append(itemName).append("**");
                if (item.hasPrice()) {
                    content.append(" - €").append(String.format("%.2f", item.getPrice()));
                }
                if (!description.isEmpty()) {
                    content.append("  \n  *").append(description).append("*");
                }
                content.append("\n");
                break;
                
            case PDF_READY:
                content.append("\\textbf{").append(escapeLatex(itemName)).append("}");
                if (item.hasPrice()) {
                    content.append(" -- \\EUR{").append(String.format("%.2f", item.getPrice())).append("}");
                }
                if (!description.isEmpty()) {
                    content.append("\\\\\n\\textit{").append(escapeLatex(description)).append("}");
                }
                content.append("\n\n");
                break;
                
            default: // PLAIN_TEXT
                content.append("• ").append(itemName);
                if (item.hasPrice()) {
                    content.append(" - €").append(String.format("%.2f", item.getPrice()));
                }
                content.append("\n");
                if (!description.isEmpty()) {
                    content.append("  ").append(description).append("\n");
                }
        }
    }
    
    public String getExportedContent() {
        return content.toString();
    }
    
    private String escapeLatex(String text) {
        if (text == null) return "";
        
        return text.replace("\\", "\\textbackslash{}")
                  .replace("{", "\\{")
                  .replace("}", "\\}")
                  .replace("_", "\\_")
                  .replace("^", "\\textasciicircum{}")
                  .replace("#", "\\#")
                  .replace("&", "\\&")
                  .replace("$", "\\$")
                  .replace("%", "\\%")
                  .replace("~", "\\textasciitilde{}");
    }
}