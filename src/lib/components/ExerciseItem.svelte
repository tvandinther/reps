<script lang="ts">
	import type { Exercise } from "$lib/exercise";

    const { exercise }: {exercise: Exercise} = $props()
</script>

<a class="bg-white shadow-md rounded px-8 pt-2 pb-4 overflow-hidden overflow-ellipsis whitespace-nowrap" href={`/exercise/${exercise.id}`}>
    <span class="font-bold text-xl mr-2">{exercise.name}</span>
    <span class="text-sm text-gray-500">{exercise.description}</span>
</a>