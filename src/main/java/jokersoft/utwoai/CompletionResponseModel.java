package jokersoft.utwoai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CompletionResponseModel {
    public String id;
    public String object;
    public int created;
    public String model;
    public CompletionChoiceModel[] choices;
}
