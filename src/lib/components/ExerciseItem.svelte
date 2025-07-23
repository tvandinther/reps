<script lang="ts">
	import type { Exercise } from "$lib/state.svelte";

    const { exercise }: {exercise: Exercise} = $props()
</script>

<a class="bg-white shadow-md rounded px-8 pt-2 pb-4" href={`/exercise/${exercise.id}`}>
    <span>{exercise.name}</span>
    <span>{exercise.description}</span>
</a>