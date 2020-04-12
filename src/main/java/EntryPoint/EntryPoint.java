package EntryPoint;

import TestDataGeneration.GenerateTestData;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;
import utils.Pair;
import utils.Parse;

import java.util.ArrayList;


public class EntryPoint extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        Project project = event.getProject();
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        PsiFile file1 = event.getData(PlatformDataKeys.PSI_FILE);
        StringBuilder text = new StringBuilder();

        Task t = new Task.Backgroundable(project, "Creating Test Data") {
            @Override
            public void run(@NotNull ProgressIndicator pi) {
                pi.setIndeterminate(true);

                Thread t = new Thread(() -> {
                    PsiJavaFile psifile = (PsiJavaFile) event.getData(CommonDataKeys.PSI_FILE);
                    assert psifile != null;
                    PsiClass[] classes = psifile.getClasses();
                    PsiMethod[] methods = classes[0].getMethods();
                    for (PsiMethod method : methods) {
                        PsiModifierList psiModifierList = method.getModifierList();
                        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
                        for (PsiAnnotation annotation : annotations) {
                            if (annotation.hasQualifiedName("Combinatorial.Combinatorial")) {
                                PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                                ArrayList<ArrayList<Integer>> attributesValues = Parse.getParsedAttributes(attributes);
                                Pair<ArrayList<ArrayList<Integer>>, Double> p = GenerateTestData.generateTestData(attributesValues);
                                ArrayList<ArrayList<Integer>> testData = p.getKey();
                                Double fitness = p.getValue();
                                String methodName = method.getName();
                                PsiParameterList parameterList = method.getParameterList();
                                PsiParameter[] parameters = parameterList.getParameters();
                                ArrayList<String> parameterNames = new ArrayList<>();
                                for (PsiParameter parameter : parameters) {
                                    parameterNames.add(parameter.getName());
                                }

                                text.append(methodName);
                                text.append("\n");
                                text.append(parameterNames);
                                text.append("\n");
                                text.append(testData);
                                text.append("\n");
                                text.append(fitness);
                                text.append("\n\n");
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

                final PsiFile[] f = new PsiFile[1];

                Thread t = new Thread(() -> {
                    f[0] = PsiFileFactory.getInstance(project).createFileFromText("result.txt", PlainTextFileType.INSTANCE, text.toString());
                });

                ApplicationManager.getApplication().runWriteAction(t);

                try {
                    t.join();

                    int result = Messages.showYesNoDialog("Would you Like to Download it?", "Test Data Is Ready", Messages.getQuestionIcon());

                    if (result == Messages.OK) {
                        WriteAction.run(() -> {
                            assert file1 != null;
                            PsiDirectory parent = file1.getParent();
                            assert parent != null;
                            PsiFile[] files = parent.getFiles();

                            for (PsiFile file : files) {
                                if (file.getName().equals("result.txt")) {
                                    file.delete();
                                }
                            }
                            assert file != null;
                            PsiDirectory d = PsiDirectoryFactory.getInstance(project).createDirectory(file.getParent());
                            d.add(f[0]);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ProgressManager.getInstance().run(t);

    }

}