package UI;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static UI.FormatFile.getCoverageFileText;

public class CoverageDialog extends DialogWrapper {

    private ArrayList<String> methodNames;
    private ArrayList<ArrayList<ArrayList<String>>> coverageOfAllMethods;
    private PsiFile file1;
    private VirtualFile file;
    private Project project;


    protected CoverageDialog(ArrayList<String> methodNames, ArrayList<ArrayList<ArrayList<String>>> coverageOfAllMethods, PsiFile file1, VirtualFile file, Project project) {
        super(true);

        this.methodNames = methodNames;
        this.coverageOfAllMethods = coverageOfAllMethods;
        this.file1 = file1;
        this.file = file;
        this.project = project;

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

        ArrayList<String> totalCoverageOfAllMethods = getTotalCoverageOfAllMethods(coverageOfAllMethods);


        int count = 0;
        for (String methodName : methodNames) {
            JLabel label = new JLabel(methodName + " ( Coverage = " + totalCoverageOfAllMethods.get(count) + "% )");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setForeground(JBColor.RED);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 20));
            Font font = label.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label.setFont(font.deriveFont(attributes));
            mainPanel.add(label, getConstraints(count * 2, 0, 15, 10));
            mainPanel.add(getTable(coverageOfAllMethods.get(count)), getConstraints(count * 2 + 1, 0, 10, 10));
            coverageOfAllMethods.get(count).add(new ArrayList<>(Arrays.asList(totalCoverageOfAllMethods.get(count))));
            count++;
        }


        scrollPane.setViewportView(mainPanel);
        return scrollPane;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), new DownloadCoverageFileAction()};
    }

    private ArrayList<String> getTotalCoverageOfAllMethods(ArrayList<ArrayList<ArrayList<String>>> coverageOfAllMethods) {
        ArrayList<String> totalCoverages = new ArrayList<>();

        for (ArrayList<ArrayList<String>> coverage : coverageOfAllMethods) {
            totalCoverages.add(coverage.get(coverage.size() - 1).get(0));
            coverage.remove(coverage.size() - 1);
        }

        return totalCoverages;
    }

    private JComponent getTable(ArrayList<ArrayList<String>> coverage) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(JBColor.WHITE);

        JLabel label = new JLabel("Coverage Details");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 17));
        panel.add(label, getConstraints(0, 0, 5, 10));

        JTable table = new JTable(convertArrayListToArray(coverage), new String[]{"Variable Pairs", "Variable Value Combinations Covered", "Coverage (%)"});

        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.setDefaultRenderer(Object.class, new CellRenderer());

        table.setRowHeight(2 * table.getRowHeight());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn tableColumn1 = table.getColumnModel().getColumn(0);
        tableColumn1.setPreferredWidth(100);

        TableColumn tableColumn2 = table.getColumnModel().getColumn(1);
        tableColumn2.setPreferredWidth(600);

        TableColumn tableColumn3 = table.getColumnModel().getColumn(2);
        tableColumn3.setPreferredWidth(100);


        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(810, 200));
        sp.setColumnHeader(new JViewport() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = d.height * 2;
                return d;
            }
        });

        panel.add(sp, getConstraints(1, 0, 20, 10));
        return panel;
    }

    private String[][] convertArrayListToArray(ArrayList<ArrayList<String>> testSet) {
        String[][] arrayTestSet = new String[testSet.size()][];
        int count = 0;
        for (ArrayList<String> testSetRow : testSet) {
            arrayTestSet[count] = testSetRow.toArray(new String[0]);
            count++;
        }

        return arrayTestSet;
    }

    private GridBagConstraints getConstraints(int row, int col, int paddingY, int paddingX) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.gridx = col;
        c.ipady = paddingY;
        c.ipadx = paddingX;
        return c;
    }


    private class DownloadCoverageFileAction extends DialogWrapperAction {

        protected DownloadCoverageFileAction() {
            super("Download Coverage Details");
            putValue(Action.NAME, "Download Coverage Details");
        }

        @Override
        protected void doAction(ActionEvent actionEvent) {
            WriteAction.run(() -> {
                PsiDirectory parent = file1.getParent();
                assert parent != null;
                PsiFile[] files = parent.getFiles();

                for (PsiFile file : files) {
                    if (file.getName().equals("coverage.txt")) {
                        file.delete();
                    }
                }
                PsiFile f = PsiFileFactory.getInstance(project).createFileFromText("coverage.txt", PlainTextFileType.INSTANCE, getCoverageFileText(coverageOfAllMethods, methodNames));
                PsiDirectory d = PsiDirectoryFactory.getInstance(project).createDirectory(file.getParent());
                d.add(f);
                doOKAction();
            });
        }
    }


}
