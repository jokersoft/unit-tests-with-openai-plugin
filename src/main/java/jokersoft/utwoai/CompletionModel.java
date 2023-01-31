package jokersoft.utwoai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public final class CompletionModel {
    public final String model = "code-davinci-002";
    public final float temperature = 0;
    public final int topP = 1;
    public final int frequencyPenalty = 0;
    public final int presencePenalty = 0;
    public final String[] stop = {"\"\"\""};

    public String prompt;
    public int maxTokens = 64;

    public CompletionModel(String prompt, String maxTokens) {
        this.prompt = prompt;
        this.maxTokens = Integer.parseInt(maxTokens);
    }
}
