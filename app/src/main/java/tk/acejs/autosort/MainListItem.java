package tk.acejs.autosort;

public class MainListItem {
    private String targetPath;
    private String resultPath;
    private String extension;

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public String getExtension() {
        return extension;
    }
}
