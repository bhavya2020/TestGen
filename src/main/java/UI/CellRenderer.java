package UI;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class CellRenderer implements TableCellRenderer {

    DefaultTableCellRenderer renderer;

    public CellRenderer() {
        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (!isSelected) {
            Color c1 = table.getBackground();
            if ((row % 2) == 0 &&
                    c1.getRed() > 10 && c1.getGreen() > 10 && c1.getBlue() > 10)
                renderer.setBackground(new Color(c1.getRed() - 20, c1.getGreen() - 20, c1.getBlue() - 60));
            else
                renderer.setBackground(c1);
        }
        renderer.setFont(table.getFont().deriveFont(Font.PLAIN, 15));

        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    }
}
