import { ref, type Ref } from "vue";

export interface DraggableToggleOptions<TValue> {
  trackRef: Ref<HTMLElement | null>;
  thumbRef: Ref<HTMLElement | null>;
  stops: TValue[];
  isDisabled: () => boolean;
  onCommit: (value: TValue) => void;
}

const DRAG_THRESHOLD = 3;

export function useDraggableToggle<TValue>(
  options: DraggableToggleOptions<TValue>
) {
  const dragging = ref(false);
  const dragOffset = ref<number | null>(null);

  let maxOffset = 0;
  let downX = 0;
  let moved = false;

  function readGeom() {
    const track = options.trackRef.value;
    const thumb = options.thumbRef.value;
    if (!track || !thumb) return null;
    const trackRect = track.getBoundingClientRect();
    const style = getComputedStyle(track);
    const padLeft = parseFloat(style.paddingLeft) || 0;
    const borderLeft = parseFloat(style.borderLeftWidth) || 0;
    const thumbWidth = thumb.getBoundingClientRect().width;
    const max =
      trackRect.width -
      padLeft -
      (parseFloat(style.paddingRight) || 0) -
      borderLeft -
      (parseFloat(style.borderRightWidth) || 0) -
      thumbWidth;
    return {
      trackRect,
      padLeft,
      borderLeft,
      thumbWidth,
      max: Math.max(0, max),
    };
  }

  function offsetToValue(offset: number): TValue {
    const stops = options.stops;
    const count = stops.length - 1;
    if (count <= 0) return stops[0];
    const ratio = offset / maxOffset;
    const idx = Math.max(0, Math.min(count, Math.round(ratio * count)));
    return stops[idx];
  }

  function onPointerDown(event: PointerEvent) {
    if (options.isDisabled()) return;
    if (event.button !== 0 && event.pointerType === "mouse") return;

    const geom = readGeom();
    if (!geom) return;
    maxOffset = geom.max;
    downX = event.clientX;
    moved = false;
    dragging.value = false;

    window.addEventListener("pointermove", onPointerMove);
    window.addEventListener("pointerup", onPointerUp, { once: true });
  }

  function onPointerMove(event: PointerEvent) {
    const track = options.trackRef.value;
    const thumb = options.thumbRef.value;
    if (!track || !thumb) return;

    if (!moved && Math.abs(event.clientX - downX) < DRAG_THRESHOLD) return;
    moved = true;
    dragging.value = true;

    const geom = readGeom();
    if (!geom) return;
    maxOffset = geom.max;

    const relX =
      event.clientX -
      geom.trackRect.left -
      geom.padLeft -
      geom.borderLeft -
      geom.thumbWidth / 2;
    dragOffset.value = Math.max(0, Math.min(maxOffset, relX));
    event.preventDefault();
  }

  function onPointerUp() {
    window.removeEventListener("pointermove", onPointerMove);

    if (dragging.value && dragOffset.value !== null) {
      options.onCommit(offsetToValue(dragOffset.value));
    }

    dragging.value = false;
    dragOffset.value = null;

    if (moved) {
      window.addEventListener("click", suppressClick, {
        capture: true,
        once: true,
      });
    }
  }

  function suppressClick(event: Event) {
    event.preventDefault();
    event.stopPropagation();
  }

  return { dragging, dragOffset, onPointerDown };
}
