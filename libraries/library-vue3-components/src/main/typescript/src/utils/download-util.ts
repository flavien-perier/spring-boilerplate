class DownloadUtil {
  public download(
    content: string | object,
    filename: string,
    mimeType: string = "application/octet-stream"
  ): void {
    const body =
      typeof content === "string" ? content : JSON.stringify(content, null, 2);

    const url = window.URL.createObjectURL(
      new Blob([body], {type: mimeType})
    );
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }

  public downloadJson(content: object, filename: string): void {
    this.download(content, filename, "application/json");
  }

  public downloadText(
    content: string,
    filename: string,
    mimeType: string = "text/plain"
  ): void {
    this.download(content, filename, mimeType);
  }
}

const downloadUtil = new DownloadUtil();

export {downloadUtil};
