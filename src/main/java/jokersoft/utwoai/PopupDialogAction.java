package jokersoft.utwoai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
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
    // Using the event, create and show a dialog
    Project currentProject = event.getProject();
    Document doc = event.getRequiredData(CommonDataKeys.EDITOR).getDocument();
    String codeToTest = doc.getText();
    String dlgTitle = event.getPresentation().getDescription();
    String prompt = String.format("%s\n# Unit test \n", codeToTest);

    CompletionModel completionModel = new CompletionModel(prompt);
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
              .header("Authorization", "Bearer ")
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
        Messages.showMessageDialog(currentProject, completionResponse.choices[0].text, dlgTitle, Messages.getInformationIcon());
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
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
