
package jokersoft.utwoai.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

  private final JPanel myMainPanel;
  private final JBTextField myApiKeyText = new JBTextField();
  private final JBTextField myMaxTokensText = new JBTextField();

  public AppSettingsComponent() {
    myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Enter API Key: "), myApiKeyText, 1, false)
            .addLabeledComponent(new JBLabel("Enter max_tokens: "), myMaxTokensText, 1, false)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return myApiKeyText;
  }

  @NotNull
  public String getApiKeyText() {
    return myApiKeyText.getText();
  }

  @NotNull
  public String getMaxTokensText() { return myMaxTokensText.getText(); }

  public void setApiKeyText(@NotNull String newText) {
    myApiKeyText.setText(newText);
  }

  public void setMaxTokensText(@NotNull String newText) {
    myMaxTokensText.setText(newText);
  }
}
