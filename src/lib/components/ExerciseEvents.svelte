<script lang="ts">
	import { isExerciseNote, isExerciseSet } from "$lib/exercise";
	import { ExerciseEvent, getExerciseRepsUnitFromType } from "$lib/schema";

    const {events}: {events: ExerciseEvent[]} = $props()
    const reversed = true;
    const sortedEvents = $derived(events.toSorted((a, b) => {
        const rev = reversed ? -1 : 1
        return (a.createdAt < b.createdAt) ? -1 * rev : ((a.createdAt > b.createdAt) ? 1 * rev : 0)
    }))
</script>

{#each sortedEvents as event, i (i)}
        {#if isExerciseNote(event)}
            <div class="border rounded">
                <span class="text-sm text-gray-500">{new Date(event.createdAt).toLocaleString()}</span>
                <br>
                <span>{event.content}</span>
            </div>
        {/if}
        {#if isExerciseSet(event)}
            <div class="border rounded">
                <span class="text-sm text-gray-500">{new Date(event.createdAt).toLocaleString()}</span>
                <br>
                <span>{event.resistance}</span> / <span>{event.reps}</span> <span>{getExerciseRepsUnitFromType(event.repsUnitType).shortName}</span> / <span>{event.exertionRating}</span>
            </div>
        {/if}
        {/each}