export function createAnimator() {
    let timeoutId   = null;
    let paused      = false;
    let index       = 0;
    let steps       = null;
    let getSpeedMs  = null;
    let onStep      = null;
    let onDone      = null;

    function tick() {
        if (paused || index >= steps.length) {
            if (!paused) onDone();
            return;
        }
        onStep(steps[index++]);
        timeoutId = setTimeout(tick, getSpeedMs());
    }

    function play(newSteps, speedFn, stepCallback, doneCallback) {
        stop();
        steps      = newSteps;
        getSpeedMs = speedFn;
        onStep     = stepCallback;
        onDone     = doneCallback;
        index      = 0;
        paused     = false;
        tick();
    }

    function pause() {
        if (!steps || paused) return;
        paused = true;
        clearTimeout(timeoutId);
    }

    function resume() {
        if (!paused) return;
        paused = false;
        tick();
    }

    function stop() {
        paused = false;
        clearTimeout(timeoutId);
        timeoutId = null;
        steps     = null;
        index     = 0;
    }

    return { play, pause, resume, stop };
}
