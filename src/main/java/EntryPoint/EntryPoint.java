package EntryPoint;

import TestDataGeneration.GenerateTestData;
import TestDataPrioritization.PrioritizeTestData;
import UI.TestDataDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import utils.Pair;
import utils.Parse;

import java.util.ArrayList;

import static TestDataCoverageCalculation.CalculateCoverage.getCoverage;


public class EntryPoint extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        Project project = event.getProject();
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        PsiFile file1 = event.getData(PlatformDataKeys.PSI_FILE);
        ArrayList<String> methodNames = new ArrayList<>();
        ArrayList<ArrayList<String>> methodParameters = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> testDataOfAllMethods = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> prioritisedTestDataOfAllMethods = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> coverageOfAllMethods = new ArrayList<>();
        ArrayList<Double> fitnessOfAllMethods = new ArrayList<>();
        ArrayList<String> returnTypes = new ArrayList<>();
        final String[] className = new String[1];

        String requiredFitness = Messages.showInputDialog(project, "Enter the Minimum Fitness Required", "TestGen", Messages.getInformationIcon(), "Fitness", new InputValidator() {
            @Override
            public boolean checkInput(String s) {
                if (s.isEmpty())
                    return false;

                try {
                    Double.parseDouble(s);
                } catch (Exception e) {
                    return false;
                }

                return true;
            }

            @Override
            public boolean canClose(String s) {
                try {
                    Double.parseDouble(s);
                } catch (Exception e) {
                    return false;
                }

                return true;
            }
        });

        Task t = new Task.Backgroundable(project, "Creating Test Data") {
            @Override
            public void run(@NotNull ProgressIndicator pi) {
                pi.setIndeterminate(true);

                Thread t = new Thread(() -> {
                    PsiJavaFile psifile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                    assert psifile != null;
                    PsiClass[] classes = psifile.getClasses();
                    className[0] = classes[0].getQualifiedName();
                    assert className[0] != null;
                    String[] splits = className[0].split(".");
                    if (splits.length > 0) {
                        className[0] = splits[splits.length - 1];
                    }
                    PsiMethod[] methods = classes[0].getMethods();
                    for (PsiMethod method : methods) {
                        PsiModifierList psiModifierList = method.getModifierList();
                        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
                        for (PsiAnnotation annotation : annotations) {
                            if (annotation.hasQualifiedName("Combinatorial.Combinatorial")) {
                                PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                                ArrayList<ArrayList<Integer>> attributesValues = Parse.getParsedAttributes(attributes);
                                assert requiredFitness != null;
                                Pair<ArrayList<ArrayList<Integer>>, Double> p = GenerateTestData.generateTestData(attributesValues, Double.parseDouble(requiredFitness));
                                ArrayList<ArrayList<Integer>> testData = p.getKey();
                                ArrayList<ArrayList<Integer>> prioritizedTestData = PrioritizeTestData.ApplyIICBP(new ArrayList<>(testData));
                                Double fitness = p.getValue();
                                String methodName = method.getName();
                                PsiParameterList parameterList = method.getParameterList();
                                PsiParameter[] parameters = parameterList.getParameters();
                                returnTypes.add(method.getReturnType().getCanonicalText());

                                ArrayList<String> parameterNames = new ArrayList<>();
                                for (PsiParameter parameter : parameters) {
                                    parameterNames.add(parameter.getName());
                                }
                                ArrayList<ArrayList<String>> coverage = getCoverage(prioritizedTestData, attributesValues, parameterNames);

                                methodNames.add(methodName);
                                methodParameters.add(parameterNames);
                                testDataOfAllMethods.add(testData);
                                prioritisedTestDataOfAllMethods.add(prioritizedTestData);
                                fitnessOfAllMethods.add(fitness);
                                coverageOfAllMethods.add(coverage);
                            }
                        }
                    }
                });

                ApplicationManager.getApplication().runReadAction(t);

                try {
                    t.join();
                    pi.setIndeterminate(false);
                    pi.setFraction(1.0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();

                assert project != null;

                new TestDataDialog(methodNames, methodParameters, testDataOfAllMethods, prioritisedTestDataOfAllMethods, coverageOfAllMethods, fitnessOfAllMethods, file1, file, project, className[0],returnTypes).show();

//                    int result = Messages.showYesNoDialog("Would you Like to Download it?", "Test Data Is Ready", Messages.getQuestionIcon());
//
//                    if (result == Messages.OK) {
//                        WriteAction.run(() -> {
//                            assert file1 != null;
//                            PsiDirectory parent = file1.getParent();
//                            assert parent != null;
//                            PsiFile[] files = parent.getFiles();
//
//                            for (PsiFile file : files) {
//                                if (file.getName().equals("result.txt")) {
//                                    file.delete();
//                                }
//                            }
//                            assert file != null;
//                            PsiDirectory d = PsiDirectoryFactory.getInstance(project).createDirectory(file.getParent());
//                            d.add(f[0]);
//                        });
//                    }

            }

            @Override
            public void onCancel() {
                super.onCancel();

            }
        };

        if (requiredFitness != null)
            ProgressManager.getInstance().run(t);

    }


}