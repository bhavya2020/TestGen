package UI;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings({"UndesirableClassUsage", "UseJBColor"})
public class TestData extends DialogWrapper {

    private ArrayList<String> methodNames;
    private ArrayList<ArrayList<String>> methodParameters;
    private ArrayList<ArrayList<ArrayList<Integer>>> testDataOfAllMethods;
    private ArrayList<ArrayList<ArrayList<Integer>>> prioritisedTestDataOfAllMethods;


    public TestData(ArrayList<String> methodNames, ArrayList<ArrayList<String>> methodParameters, ArrayList<ArrayList<ArrayList<Integer>>> testDataOfAllMethods, ArrayList<ArrayList<ArrayList<Integer>>> prioritisedTestDataOfAllMethods) {
        super(true);


        this.methodNames = methodNames;
        this.methodParameters = methodParameters;
        this.testDataOfAllMethods = testDataOfAllMethods;
        this.prioritisedTestDataOfAllMethods = prioritisedTestDataOfAllMethods;

        init();
        setTitle("TestGen");
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {


        JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(JBColor.WHITE);

        int count = 0;
        for (String methodName : methodNames) {
            JLabel label = new JLabel(methodName);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setForeground(JBColor.RED);
            label.setFont(label.getFont().deriveFont(Font.BOLD,20));
            Font font = label.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label.setFont(font.deriveFont(attributes));
            mainPanel.add(label, getConstraints(count*2,0,15,10));
            JPanel subPanel = new JPanel();
            subPanel.setLayout(new GridLayout(1, 2, 10, 0));
            subPanel.setBackground(JBColor.WHITE);
            subPanel.add(getTable(methodParameters.get(count), testDataOfAllMethods.get(count), true));
            subPanel.add(getTable(methodParameters.get(count), prioritisedTestDataOfAllMethods.get(count), false));
            mainPanel.add(subPanel, getConstraints(count*2+1,0,10,10));
            count++;
        }


        scrollPane.setViewportView(mainPanel);
        return scrollPane;
    }

    private JComponent getTable(ArrayList<String> parameterNames, ArrayList<ArrayList<Integer>> testSet, boolean isTestData) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(JBColor.WHITE);

        JLabel label = new JLabel(isTestData ? "Test Data" : "Prioritised Test Data");
        label.setFont(label.getFont().deriveFont(Font.BOLD,17));
        panel.add(label,getConstraints(0,0,5,10));

        JTable table = new JTable(convertArrayListToArray(testSet), parameterNames.toArray(new String[0]));

        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.setDefaultRenderer(Object.class, new CellRenderer());

        table.setRowHeight(2 * table.getRowHeight());

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(sp.getPreferredSize().width, 200));
        sp.setColumnHeader(new JViewport() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = d.height*2;
                return d;
            }
        });

        panel.add(sp, getConstraints(1,0,20,10));
        return panel;
    }

    private Integer[][] convertArrayListToArray(ArrayList<ArrayList<Integer>> testSet) {
        Integer[][] arrayTestSet = new Integer[testSet.size()][];
        int count = 0;
        for (ArrayList<Integer> testSetRow : testSet) {
            arrayTestSet[count] = testSetRow.toArray(new Integer[0]);
            count++;
        }

        return arrayTestSet;
    }

    private GridBagConstraints getConstraints(int row, int col,int paddingY, int paddingX){
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.gridx = col;
        c.ipady = paddingY;
        c.ipadx = paddingX;
        return  c;
    }
}
