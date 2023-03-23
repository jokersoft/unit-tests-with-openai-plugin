package jokersoft.utwoai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import jokersoft.utwoai.settings.AppSettingsState;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 */
public class PopupDialogAction extends AnAction {

  /**
   * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
   * declarations. Only needed in {@link PopupDialogAction} class because a second constructor is overridden.
   *
   * @see AnAction#AnAction()
   */
  public PopupDialogAction() {
    super();
  }

  /**
   * Gives the user feedback when the dynamic action menu is chosen.
   * Pops a simple message dialog. See the psi_demo plugin for an
   * example of how to use {@link AnActionEvent} to access data.
   *
   * @param event Event received when the associated menu item is chosen.
   */
  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    // Get all the required data from data keys
    // Editor and Project were verified in update(), so they are not null.
    final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
    final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
    final Document document = editor.getDocument();

    // Using the event, create and show a dialog
    String codeToTest = document.getText();
    String prompt = String.format("%s\n# Unit test \n", codeToTest);
    String apiKey = AppSettingsState.getInstance().apiKey;
    String maxTokens = AppSettingsState.getInstance().maxTokens;

    CompletionModel completionModel = new CompletionModel(prompt, maxTokens);
    String requestPayload = null;
    try {
      requestPayload = new ObjectMapper().writeValueAsString(completionModel);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    HttpRequest request = null;
    try {
      request = HttpRequest.newBuilder()
              .uri(new URI("https://api.openai.com/v1/completions"))
              .header("Content-Type", "application/json")
              .header("Authorization", String.format("Bearer %s", apiKey))
              .POST(HttpRequest.BodyPublishers.ofString(requestPayload))
              .build();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    HttpResponse<String> response = null;
    try {
      response = HttpClient
              .newBuilder()
              .build()
              .send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    String json = response.body();

    if (!json.isEmpty()) {
      try {
        CompletionResponseModel completionResponse = new ObjectMapper().readValue(json, CompletionResponseModel.class);
        String completionText = completionResponse.choices[0].text;

//    Language language = LanguageUtil.getLanguageForPsi(project, virtualFile, fileType);

        final VirtualFile projectDir = project.getBaseDir();
        if (projectDir != null) {
          final PsiManager psiManager = PsiManager.getInstance(project);
          PsiDirectory targetDir = psiManager.findDirectory(projectDir);
          assert targetDir != null;
          PsiFile testFile = targetDir.createFile("test.java");

          final Document testDocument = PsiDocumentManager.getInstance(project).getDocument(testFile);
          if (testDocument != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
              testDocument.setText(completionText);
//              PsiDocumentManager.getInstance(project).commitDocument(testDocument);
            });
          }
        }
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    //TODO: catch 403

  }

  /**
   * Determines whether this menu item is available for the current context.
   * Requires a project to be open.
   *
   * @param e Event received when the associated group-id menu is chosen.
   */
  @Override
  public void update(AnActionEvent e) {
    // Set the availability based on whether a project is open
    Project project = e.getProject();
    e.getPresentation().setEnabledAndVisible(project != null);
  }
}
