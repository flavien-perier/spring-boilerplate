type MarkdownOptions = {
  enableTitle?: boolean;
  enableLinks?: boolean;
};

class MarkdownUtil {
  public escapeHtml(value: string): string {
    return value
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  public sanitizeUrl(url: string): string {
    const trimmed = url.trim();
    // eslint-disable-next-line no-control-regex
    const probe = trimmed.replace(/[\x00-\x20\x7f-\x9f]/g, "").toLowerCase();
    if (/^[a-z][a-z0-9+.-]*:/.test(probe)) {
      return /^(https?|mailto):/.test(probe) ? trimmed : "#";
    }
    return trimmed;
  }

  public renderMarkdown(src: string, opts: MarkdownOptions = {}): string {
    const enableTitle = opts.enableTitle !== false;
    const enableLinks = opts.enableLinks !== false;
    const lines = src.replace(/\r\n?/g, "\n").split("\n");
    const html: string[] = [];
    let paragraph: string[] = [];
    let i = 0;

    const flushParagraph = (): void => {
      if (paragraph.length > 0) {
        html.push(
          `<p>${this.parseInline(paragraph.join(" "), enableLinks)}</p>`
        );
        paragraph = [];
      }
    };

    while (i < lines.length) {
      const line = lines[i];

      if (/^```/.test(line)) {
        flushParagraph();
        const code: string[] = [];
        i += 1;
        while (i < lines.length && !/^```/.test(lines[i])) {
          code.push(lines[i]);
          i += 1;
        }
        i += 1; // skip the closing fence
        html.push(
          `<pre><code>${this.escapeHtml(code.join("\n"))}</code></pre>`
        );
        continue;
      }

      if (/^\s*([-*_])(\s*\1){2,}\s*$/.test(line)) {
        flushParagraph();
        html.push("<hr>");
        i += 1;
        continue;
      }

      const heading = enableTitle ? /^(#{1,6})\s+(.*)$/.exec(line) : null;
      if (heading) {
        flushParagraph();
        const level = heading[1].length;
        const content = heading[2].replace(/\s+#+\s*$/, "");
        html.push(
          `<h${level}>${this.parseInline(content, enableLinks)}</h${level}>`
        );
        i += 1;
        continue;
      }

      if (/^\s*>/.test(line)) {
        flushParagraph();
        const quote: string[] = [];
        while (i < lines.length && /^\s*>/.test(lines[i])) {
          quote.push(lines[i].replace(/^\s*>\s?/, ""));
          i += 1;
        }
        html.push(
          `<blockquote>${this.renderMarkdown(
            quote.join("\n"),
            opts
          )}</blockquote>`
        );
        continue;
      }

      if (/^\s*[-*+]\s+/.test(line)) {
        flushParagraph();
        const items: string[] = [];
        while (i < lines.length && /^\s*[-*+]\s+/.test(lines[i])) {
          const content = lines[i].replace(/^\s*[-*+]\s+/, "");
          items.push(`<li>${this.parseInline(content, enableLinks)}</li>`);
          i += 1;
        }
        html.push(`<ul>${items.join("")}</ul>`);
        continue;
      }

      if (/^\s*\d+\.\s+/.test(line)) {
        flushParagraph();
        const items: string[] = [];
        while (i < lines.length && /^\s*\d+\.\s+/.test(lines[i])) {
          const content = lines[i].replace(/^\s*\d+\.\s+/, "");
          items.push(`<li>${this.parseInline(content, enableLinks)}</li>`);
          i += 1;
        }
        html.push(`<ol>${items.join("")}</ol>`);
        continue;
      }

      if (/^\s*$/.test(line)) {
        flushParagraph();
        i += 1;
        continue;
      }

      paragraph.push(line);
      i += 1;
    }

    flushParagraph();
    return html.join("\n");
  }

  public highlightMarkdown(src: string, opts: MarkdownOptions = {}): string {
    const enableTitle = opts.enableTitle !== false;
    const enableLinks = opts.enableLinks !== false;
    const lines = src.split("\n");
    const out: string[] = [];
    let inFence = false;

    for (const line of lines) {
      if (/^```/.test(line)) {
        inFence = !inFence;
        out.push(`<span class="md-marker">${this.escapeHtml(line)}</span>`);
        continue;
      }
      if (inFence) {
        out.push(`<code>${this.escapeHtml(line)}</code>`);
        continue;
      }
      out.push(this.highlightLine(line, enableTitle, enableLinks));
    }

    return out.join("\n");
  }

  private parseEmphasis(text: string): string {
    return text
      .replace(/\*\*([^*]+?)\*\*/g, "<strong>$1</strong>")
      .replace(/__([^_]+?)__/g, "<strong>$1</strong>")
      .replace(/\*([^*]+?)\*/g, "<em>$1</em>")
      .replace(/_([^_]+?)_/g, "<em>$1</em>")
      .replace(/~~([^~]+?)~~/g, "<del>$1</del>");
  }

  private parseInline(text: string, enableLinks: boolean): string {
    let result = "";
    let i = 0;

    while (i < text.length) {
      const rest = text.slice(i);

      if (text[i] === "`") {
        const end = text.indexOf("`", i + 1);
        if (end !== -1) {
          const code = text.slice(i + 1, end);
          result += `<code>${this.escapeHtml(code)}</code>`;
          i = end + 1;
          continue;
        }
      }

      if (enableLinks && text[i] === "[") {
        const link = /^\[([^\]]*)\]\(([^)]*)\)/.exec(rest);
        if (link) {
          const label = this.parseEmphasis(this.escapeHtml(link[1]));
          const href = this.escapeHtml(this.sanitizeUrl(link[2]));
          result += `<a href="${href}" rel="noopener noreferrer">${label}</a>`;
          i += link[0].length;
          continue;
        }
      }

      // Plain run up to the next potential token (always advances at least one).
      let j = i + 1;
      while (
        j < text.length &&
        text[j] !== "`" &&
        !(enableLinks && text[j] === "[")
        ) {
        j += 1;
      }
      result += this.parseEmphasis(this.escapeHtml(text.slice(i, j)));
      i = j;
    }

    return result;
  }

  private highlightLine(
    line: string,
    enableTitle: boolean,
    enableLinks: boolean
  ): string {
    if (/^\s*([-*_])(\s*\1){2,}\s*$/.test(line)) {
      return `<span class="md-marker">${this.escapeHtml(line)}</span>`;
    }

    const heading = enableTitle ? /^(#{1,6})(\s+)(.*)$/.exec(line) : null;
    if (heading) {
      const level = heading[1].length;
      return (
        `<span class="md-marker">${heading[1]}${heading[2]}</span>` +
        `<span class="md-heading md-h${level}">${this.highlightInline(
          heading[3],
          enableLinks
        )}</span>`
      );
    }

    // Blockquote, unordered list and ordered list share one shape: an escaped
    // marker span followed by the highlighted remainder of the line.
    const blockPrefixes = [
      /^(\s*>\s?)(.*)$/,
      /^(\s*[-*+]\s+)(.*)$/,
      /^(\s*\d+\.\s+)(.*)$/,
    ];
    for (const prefix of blockPrefixes) {
      const m = prefix.exec(line);
      if (m) {
        return `<span class="md-marker">${this.escapeHtml(
          m[1]
        )}</span>${this.highlightInline(m[2], enableLinks)}`;
      }
    }

    return this.highlightInline(line, enableLinks);
  }

  private highlightInline(text: string, enableLinks: boolean): string {
    const wrap = (marker: string, content: string, tag: string): string =>
      `<span class="md-marker">${marker}</span>` +
      `<${tag}>${this.escapeHtml(content)}</${tag}>` +
      `<span class="md-marker">${marker}</span>`;

    let result = "";
    let i = 0;

    while (i < text.length) {
      const rest = text.slice(i);
      let m: RegExpExecArray | null;

      if ((m = /^`([^`\n]+)`/.exec(rest))) {
        result += wrap("`", m[1], "code");
        i += m[0].length;
        continue;
      }
      if ((m = /^\*\*([^*\n]+)\*\*/.exec(rest))) {
        result += wrap("**", m[1], "strong");
        i += m[0].length;
        continue;
      }
      if ((m = /^__([^_\n]+)__/.exec(rest))) {
        result += wrap("__", m[1], "strong");
        i += m[0].length;
        continue;
      }
      if ((m = /^~~([^~\n]+)~~/.exec(rest))) {
        result += wrap("~~", m[1], "del");
        i += m[0].length;
        continue;
      }
      if ((m = /^\*([^*\n]+)\*/.exec(rest))) {
        result += wrap("*", m[1], "em");
        i += m[0].length;
        continue;
      }
      if ((m = /^_([^_\n]+)_/.exec(rest))) {
        result += wrap("_", m[1], "em");
        i += m[0].length;
        continue;
      }
      if (enableLinks && (m = /^\[([^\]\n]*)\]\(([^)\n]*)\)/.exec(rest))) {
        const label = this.escapeHtml(m[1]);
        const url = m[2];
        const href = this.escapeHtml(this.sanitizeUrl(url));
        result +=
          `<span class="md-marker">[</span>` +
          `<a href="${href}" rel="noopener noreferrer">${label}</a>` +
          `<span class="md-marker">](${this.escapeHtml(url)})</span>`;
        i += m[0].length;
        continue;
      }

      result += this.escapeHtml(text[i]);
      i += 1;
    }

    return result;
  }
}

const markdownUtil = new MarkdownUtil();

export {markdownUtil};
export type {MarkdownOptions};
