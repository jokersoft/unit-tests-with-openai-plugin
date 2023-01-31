
package jokersoft.utwoai.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

  private AppSettingsComponent mySettingsComponent;

  // A default constructor with no arguments is required because this implementation
  // is registered as an applicationConfigurable EP

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "UTWOAI: Plugin Settings";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return mySettingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    mySettingsComponent = new AppSettingsComponent();
    return mySettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    AppSettingsState settings = AppSettingsState.getInstance();
    boolean modified = !mySettingsComponent.getApiKeyText().equals(settings.apiKey);
    modified |= mySettingsComponent.getMaxTokensText() != settings.maxTokens;
    return modified;
  }

  @Override
  public void apply() {
    AppSettingsState settings = AppSettingsState.getInstance();
    settings.apiKey = mySettingsComponent.getApiKeyText();
    settings.maxTokens = mySettingsComponent.getMaxTokensText();
  }

  @Override
  public void reset() {
    AppSettingsState settings = AppSettingsState.getInstance();
    mySettingsComponent.setApiKeyText(settings.apiKey);
    mySettingsComponent.setMaxTokensText(settings.maxTokens);
  }

  @Override
  public void disposeUIResources() {
    mySettingsComponent = null;
  }

}
