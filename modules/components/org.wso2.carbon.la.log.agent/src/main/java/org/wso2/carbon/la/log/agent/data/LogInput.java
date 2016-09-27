package org.wso2.carbon.la.log.agent.data;

/**
 * Created by malith on 11/24/15.
 */
public class LogInput {

    private String inputType;

    private String filePath;

    private String startPosition;

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String file_path) {
        this.filePath = file_path;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String start_position) {
        this.startPosition = start_position;
    }
}
