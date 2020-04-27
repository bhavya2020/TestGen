package UI;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class HeaderRenderer implements TableCellRenderer {

    DefaultTableCellRenderer renderer;

    public HeaderRenderer() {
        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderer.setBackground(JBColor.BLACK);
        renderer.setForeground(JBColor.WHITE);
        renderer.setFont(table.getFont().deriveFont(Font.BOLD, 17));
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
