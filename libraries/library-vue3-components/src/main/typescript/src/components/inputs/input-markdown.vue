<template>
  <div class="input-field">
    <label
      v-if="label"
      :for="editorId"
      class="input-label"
      :class="props.size && `input-label--${props.size}`"
      >{{ label }}</label
    >
    <div v-if="showToolbar" class="fio-input-markdown__toolbar">
      <button
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.bold')"
        :disabled="disabled"
        @mousedown.prevent
        @click="wrapSelection('**', t('fio.markdown.placeholder.text'))"
      >
        <fio-icon icon="bold" />
      </button>
      <button
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.italic')"
        :disabled="disabled"
        @mousedown.prevent
        @click="wrapSelection('*', t('fio.markdown.placeholder.text'))"
      >
        <fio-icon icon="italic" />
      </button>
      <button
        v-if="enableTitle"
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.heading')"
        :disabled="disabled"
        @mousedown.prevent
        @click="prefixLine('# ')"
      >
        <fio-icon icon="heading" />
      </button>
      <button
        v-if="enableLinks"
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.link')"
        :disabled="disabled"
        @mousedown.prevent
        @click="insertLink"
      >
        <fio-icon icon="link" />
      </button>
      <button
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.list')"
        :disabled="disabled"
        @mousedown.prevent
        @click="prefixLine('- ')"
      >
        <fio-icon icon="list-ul" />
      </button>
      <button
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.code')"
        :disabled="disabled"
        @mousedown.prevent
        @click="wrapSelection('`', t('fio.markdown.placeholder.text'))"
      >
        <fio-icon icon="code" />
      </button>
      <button
        type="button"
        class="fio-input-markdown__tool"
        :title="t('fio.markdown.quote')"
        :disabled="disabled"
        @mousedown.prevent
        @click="prefixLine('> ')"
      >
        <fio-icon icon="quote-right" />
      </button>
    </div>
    <div
      ref="editor"
      :id="editorId"
      class="input-control fio-input-markdown__area"
      :class="[
        { 'input-control--disabled': disabled },
        props.size && `input-control--${props.size}`,
      ]"
      :contenteditable="!disabled"
      :data-placeholder="placeholder"
      @input="onInput"
      @keydown="onKeydown"
      @paste="onPaste"
      @compositionstart="onCompositionStart"
      @compositionend="onCompositionEnd"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, useId, watch } from "vue";
import { useI18n } from "vue-i18n";
import FioIcon from "../icon.vue";
import { markdownUtil } from "@/utils/markdown-util";
import type { InputComponent } from "./model/input-component";
import type { InputSize } from "@/model/input-size";

defineOptions({
  name: "FioInputMarkdown",
});

const { t } = useI18n();

const editorId = useId();

// True while an IME (CJK / dead-key) composition is in progress. The input
// handler rebuilds innerHTML, which would corrupt an in-flight composition.
let isComposing = false;

const props = withDefaults(
  defineProps<
    InputComponent<string> & {
      label?: string;
      placeholder?: string;
      showToolbar?: boolean;
      enableTitle?: boolean;
      enableLinks?: boolean;
      size?: InputSize;
    }
  >(),
  {
    modelValue: "",
    disabled: false,
    showToolbar: true,
    enableTitle: true,
    enableLinks: true,
  }
);

const emit = defineEmits<{
  "update:modelValue": [value: string];
  input: [event: Event];
}>();

const model = computed<string>({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

const editor = ref<HTMLElement | null>(null);

function getSelectionOffsets(root: HTMLElement): {
  start: number;
  end: number;
} {
  const selection = window.getSelection();
  if (!selection || selection.rangeCount === 0) {
    const end = (root.textContent ?? "").length;
    return { start: end, end };
  }
  const range = selection.getRangeAt(0);
  if (
    !root.contains(range.startContainer) ||
    !root.contains(range.endContainer)
  ) {
    const end = (root.textContent ?? "").length;
    return { start: end, end };
  }
  const offsetOf = (container: Node, offset: number): number => {
    const measure = range.cloneRange();
    measure.selectNodeContents(root);
    measure.setEnd(container, offset);
    return measure.toString().length;
  };
  return {
    start: offsetOf(range.startContainer, range.startOffset),
    end: offsetOf(range.endContainer, range.endOffset),
  };
}

/** Resolve a character offset to a concrete text node and local offset. */
function locate(
  root: HTMLElement,
  offset: number
): { node: Text; offset: number } | null {
  const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
  let remaining = offset;
  let node = walker.nextNode() as Text | null;
  let last: Text | null = null;
  while (node) {
    const length = node.data.length;
    if (remaining <= length) {
      return { node, offset: remaining };
    }
    remaining -= length;
    last = node;
    node = walker.nextNode() as Text | null;
  }
  if (last) {
    return { node: last, offset: last.data.length };
  }
  return null;
}

function selectRange(root: HTMLElement, start: number, end: number): void {
  const selection = window.getSelection();
  if (!selection) return;
  const range = document.createRange();
  const startPos = locate(root, start);
  if (startPos) {
    range.setStart(startPos.node, startPos.offset);
  } else {
    range.selectNodeContents(root);
    range.collapse(true);
  }
  const endPos = locate(root, end);
  if (endPos) {
    range.setEnd(endPos.node, endPos.offset);
  }
  selection.removeAllRanges();
  selection.addRange(range);
}

function setCaret(root: HTMLElement, offset: number): void {
  selectRange(root, offset, offset);
}

function render(value: string): void {
  const el = editor.value;
  if (!el) return;
  let html = markdownUtil.highlightMarkdown(value, {
    enableTitle: props.enableTitle,
    enableLinks: props.enableLinks,
  });
  // With `white-space: pre-wrap`, a trailing "\n" leaves no visible empty last
  // line in a contenteditable. Append a display-only <br> so that blank line
  // shows. <br> contributes nothing to textContent, so `textContent === value`
  // still holds and caret save/restore by character offset is unaffected.
  if (value.endsWith("\n")) {
    html += "<br>";
  }
  el.innerHTML = html;
}

function emitInput(): void {
  emit("input", new Event("input"));
}

function handleInput(event: Event): void {
  const el = editor.value;
  if (!el) return;
  const { end } = getSelectionOffsets(el);
  const value = el.textContent ?? "";
  model.value = value;
  render(value);
  setCaret(el, end);
  emit("input", event);
}

function onInput(event: Event): void {
  if (isComposing) return;
  handleInput(event);
}

function onCompositionStart(): void {
  isComposing = true;
}

function onCompositionEnd(event: Event): void {
  isComposing = false;
  handleInput(event);
}

function applyEdit(
  value: string,
  selectionStart: number,
  selectionEnd: number
): void {
  const el = editor.value;
  if (!el) return;
  model.value = value;
  render(value);
  el.focus();
  selectRange(el, selectionStart, selectionEnd);
  emitInput();
}

function insertText(text: string): void {
  const el = editor.value;
  if (!el || props.disabled) return;
  const { start, end } = getSelectionOffsets(el);
  const current = el.textContent ?? "";
  const caret = start + text.length;
  applyEdit(current.slice(0, start) + text + current.slice(end), caret, caret);
}

function onKeydown(event: KeyboardEvent): void {
  if (event.key === "Enter") {
    event.preventDefault();
    insertText("\n");
  }
}

function onPaste(event: ClipboardEvent): void {
  event.preventDefault();
  const text = event.clipboardData?.getData("text/plain") ?? "";
  insertText(text);
}

function wrapSelection(marker: string, placeholder: string): void {
  const el = editor.value;
  if (!el || props.disabled) return;
  const { start, end } = getSelectionOffsets(el);
  const current = el.textContent ?? "";
  const selected = current.slice(start, end) || placeholder;
  const value =
    current.slice(0, start) + marker + selected + marker + current.slice(end);
  applyEdit(
    value,
    start + marker.length,
    start + marker.length + selected.length
  );
}

function prefixLine(prefix: string): void {
  const el = editor.value;
  if (!el || props.disabled) return;
  const { start } = getSelectionOffsets(el);
  const current = el.textContent ?? "";
  const lineStart = start === 0 ? 0 : current.lastIndexOf("\n", start - 1) + 1;
  const value = current.slice(0, lineStart) + prefix + current.slice(lineStart);
  const caret = start + prefix.length;
  applyEdit(value, caret, caret);
}

function insertLink(): void {
  const el = editor.value;
  if (!el || props.disabled) return;
  const { start, end } = getSelectionOffsets(el);
  const current = el.textContent ?? "";
  const urlPlaceholder = t("fio.markdown.placeholder.url");
  const label =
    current.slice(start, end) || t("fio.markdown.placeholder.link-text");
  const snippet = `[${label}](${urlPlaceholder})`;
  const value = current.slice(0, start) + snippet + current.slice(end);
  // Select the url placeholder: "[" + label + "](" precedes it.
  const urlStart = start + label.length + 3;
  applyEdit(value, urlStart, urlStart + urlPlaceholder.length);
}

// React only to EXTERNAL model changes; while the user types the editor's
// textContent already equals the model so we skip to avoid fighting the caret.
watch(model, (value) => {
  const el = editor.value;
  if (!el) return;
  if ((el.textContent ?? "") === value) return;
  render(value);
});

onMounted(() => {
  render(model.value);
});
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.fio-input-markdown {
  &__toolbar {
    display: flex;
    flex-wrap: wrap;
    gap: $margin-xs;
    margin-bottom: $margin-xs;
  }

  &__tool {
    display: inline-flex;
    align-items: center;
    padding: $margin-xxs $margin-xs;
    background: transparent;
    border: none;
    border-radius: $border-radius-size;
    color: darker(secondary, 30);
    cursor: pointer;
    opacity: 0.8;
    transition: color 0.2s ease, background-color 0.2s ease, opacity 0.2s ease;

    &:hover:not(:disabled) {
      opacity: 1;
      color: darker(secondary, 60);
      background-color: lighter(secondary, 70);
    }

    &:disabled {
      cursor: not-allowed;
      opacity: 0.4;
    }
  }

  &__area {
    white-space: pre-wrap;
    word-break: break-word;
    min-height: 8rem;
    line-height: 1.6;
    overflow-y: auto;

    &:empty::before {
      content: attr(data-placeholder);
      color: darker(secondary, 30);
      pointer-events: none;
    }

    :deep(.md-marker) {
      color: darker(secondary, 30);
      opacity: 0.6;
    }

    :deep(.md-heading) {
      font-weight: bold;

      &.md-h1 {
        font-size: $font-xxl-size;
      }

      &.md-h2 {
        font-size: $font-xl-size;
      }

      &.md-h3 {
        font-size: $font-l-size;
      }

      &.md-h4,
      &.md-h5 {
        font-size: $font-size;
      }

      &.md-h6 {
        font-size: $font-s-size;
      }
    }

    :deep(strong) {
      font-weight: bold;
    }

    :deep(em) {
      font-style: italic;
    }

    :deep(del) {
      text-decoration: line-through;
    }

    :deep(code) {
      padding: 0 $margin-xxs;
      background-color: lighter(secondary, 80);
      border-radius: $border-radius-size;
      font-family: monospace;
    }

    :deep(a) {
      color: $primary;
    }
  }
}
</style>
