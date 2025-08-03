<script lang="ts">
	import { getExerciseRepsUnitFromType } from '$lib/actions';
	import { ExerciseEvent, ExerciseNote, ExerciseSet } from '$lib/schema';
    import { is } from 'valibot'

    const {events}: {events: ExerciseEvent[]} = $props()
    const reversed = true;
    const sortedEvents = $derived(events.toSorted((a, b) => {
        const rev = reversed ? -1 : 1
        return (a.createdAt < b.createdAt) ? -1 * rev : ((a.createdAt > b.createdAt) ? 1 * rev : 0)
    }))
</script>

{#each sortedEvents as event, i (i)}
        {#if is(ExerciseNote, event)}
            <div class="border rounded">
                <span class="text-sm text-gray-500">{new Date(event.createdAt).toLocaleString()}</span>
                <br>
                <span>{event.content}</span>
            </div>
        {/if}
        {#if is(ExerciseSet, event)}
            <div class="border rounded">
                <span class="text-sm text-gray-500">{new Date(event.createdAt).toLocaleString()}</span>
                <br>
                <span>{event.resistance}</span> / <span>{event.reps}</span> <span>{getExerciseRepsUnitFromType(event.repsUnitType).shortName}</span> / <span>{event.exertionRating}</span>
            </div>
        {/if}
        {/each}